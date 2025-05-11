package org.application.shikiapp.models.viewModels

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource.LoadParams
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.events.DrawerEvent
import org.application.shikiapp.events.FilterEvent
import org.application.shikiapp.events.FilterEvent.SetCensored
import org.application.shikiapp.events.FilterEvent.SetDuration
import org.application.shikiapp.events.FilterEvent.SetFranchise
import org.application.shikiapp.events.FilterEvent.SetGenre
import org.application.shikiapp.events.FilterEvent.SetKind
import org.application.shikiapp.events.FilterEvent.SetMyList
import org.application.shikiapp.events.FilterEvent.SetOrder
import org.application.shikiapp.events.FilterEvent.SetPublisher
import org.application.shikiapp.events.FilterEvent.SetRating
import org.application.shikiapp.events.FilterEvent.SetRole
import org.application.shikiapp.events.FilterEvent.SetScore
import org.application.shikiapp.events.FilterEvent.SetSeason
import org.application.shikiapp.events.FilterEvent.SetSeasonS
import org.application.shikiapp.events.FilterEvent.SetSeasonYF
import org.application.shikiapp.events.FilterEvent.SetSeasonYS
import org.application.shikiapp.events.FilterEvent.SetStatus
import org.application.shikiapp.events.FilterEvent.SetStudio
import org.application.shikiapp.events.FilterEvent.SetTitle
import org.application.shikiapp.generated.type.MangaKindEnum
import org.application.shikiapp.models.states.CatalogState
import org.application.shikiapp.models.states.FiltersState
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.paging.ContentPaging
import org.application.shikiapp.utils.enums.CatalogItems
import org.application.shikiapp.utils.enums.PeopleFilterItems.MANGAKA
import org.application.shikiapp.utils.enums.PeopleFilterItems.PRODUCER
import org.application.shikiapp.utils.enums.PeopleFilterItems.SEYU
import org.application.shikiapp.utils.navigation.Screen
import org.application.shikiapp.utils.setScore

class CatalogViewModel(saved: SavedStateHandle) : ViewModel() {
    private val args = saved.toRoute<Screen.Catalog>()

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _event = Channel<DrawerEvent?>()
    val event = _event.receiveAsFlow()

    private val _navEvent = Channel<Boolean>()
    val navEvent = _navEvent.receiveAsFlow().onStart { emit(args.showOngoing) }

    private val filters = CatalogItems.entries.associateWith { MutableStateFlow(FiltersState()) }
    private val pagers = mutableMapOf<CatalogItems, Flow<PagingData<Content>>>()

    private val _currentFilters = MutableStateFlow(FiltersState())
    val currentFilters = _currentFilters.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = _state.flatMapLatest { state ->
        _currentFilters.flatMapLatest { currentFilters ->
            pagers.getOrPut(state.menu) {
                createPagingFlow(state.menu, currentFilters).cachedIn(viewModelScope)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val genres = _state.flatMapLatest {
        when (it.menu) {
            CatalogItems.ANIME -> GraphQL.getAnimeGenres()
            CatalogItems.MANGA, CatalogItems.RANOBE -> GraphQL.getMangaGenres()
            else -> flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun showFilters(menu: CatalogItems) = _state.update {
        when (menu) {
            CatalogItems.ANIME -> it.copy(showFiltersA = true)
            CatalogItems.MANGA -> it.copy(showFiltersM = true)
            CatalogItems.RANOBE -> it.copy(showFiltersR = true)
            CatalogItems.PEOPLE -> it.copy(showFiltersP = true)
            else -> it
        }
    }

    fun hideFilters() = _state.update {
        it.copy(
            showFiltersA = false, showFiltersM = false, showFiltersR = false, showFiltersP = false
        )
    }

    fun onDrawerClick() {
        viewModelScope.launch {
            _event.send(DrawerEvent.Click)
        }
    }

    fun pick(menu: CatalogItems) {
        filters.getValue(_state.value.menu).update { _currentFilters.value }
        _currentFilters.update { filters.getValue(menu).value }


        _state.update {
            it.copy(
                menu = menu,
                search = _currentFilters.value.title,
                drawerState = DrawerState(DrawerValue.Closed)
            )
        }

        viewModelScope.launch {
            _event.send(DrawerEvent.Clear)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun createPagingFlow(item: CatalogItems, filtersState: FiltersState) = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false), pagingSourceFactory = {
            ContentPaging(filters = { filtersState }, fetch = { filters, page, params ->
                fetchData(item, filters, page, params)
            })
        }).flow.debounce(350L).flowOn(Dispatchers.IO).retryWhen { cause, attempt ->
            cause is ClientRequestException && attempt < 3
        }

    private suspend fun fetchData(item: CatalogItems, query: FiltersState, page: Int, params: LoadParams<Int>) = when (item) {
        CatalogItems.ANIME -> GraphQL.getAnimeList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.joinToString(","),
            status = query.status.joinToString(","),
            season = query.season.joinToString(","),
            score = setScore(query.status, query.score),
            duration = query.duration.joinToString(","),
            rating = query.rating.joinToString(","),
            genre = query.genres.joinToString(","),
            search = query.title
        )

        CatalogItems.MANGA -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.joinToString(","),
            status = query.status.joinToString(","),
            season = query.season.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
            search = query.title
        )

        CatalogItems.RANOBE -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = if (query.kind.isNotEmpty()) query.kind.joinToString(",")
            else listOf(MangaKindEnum.light_novel, MangaKindEnum.novel).joinToString(","),
            status = query.status.joinToString(","),
            season = query.season.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
            search = query.title
        )

        CatalogItems.CHARACTERS -> GraphQL.getCharacters(
            page = page, limit = params.loadSize, search = query.title
        )

        CatalogItems.PEOPLE -> GraphQL.getPeople(
            page = page,
            limit = params.loadSize,
            search = query.title,
            isSeyu = query.isSeyu,
            isProducer = query.isProducer,
            isMangaka = query.isMangaka
        )
    }

    fun onEvent(event: FilterEvent) {
        when (event) {
            is SetOrder -> _currentFilters.update {
                it.copy(order = event.order)
            }

            is SetStatus -> _currentFilters.update {
                it.copy(
                    status = it.status.apply {
                        if (event.status in it.status) remove(event.status) else add(event.status)
                    }
                )
            }

            is SetKind -> _currentFilters.update {
                it.copy(
                    kind = it.kind.apply {
                        if (event.kind in it.kind) remove(event.kind) else add(event.kind)
                    }
                )
            }

            is SetSeasonYS -> if (event.year.length <= 4) _currentFilters.update { it.copy(seasonYS = event.year) }

            is SetSeasonYF -> if (event.year.length <= 4) _currentFilters.update { it.copy(seasonYF = event.year) }

            is SetSeasonS -> _currentFilters.update {
                it.copy(
                    season = it.seasonS.apply {
                        if (event.season in it.seasonS) remove(event.season) else add(event.season)
                    }
                )
            }

            is SetSeason -> {
                val yearStart = _currentFilters.value.seasonYS.toIntOrNull() ?: 1900
                val yearEnd = _currentFilters.value.seasonYF.toIntOrNull() ?: 2100
                val selectedSeasons = _currentFilters.value.seasonS

                val seasons = (yearStart..yearEnd).flatMapTo(SnapshotStateList()) { year ->
                    if (selectedSeasons.isEmpty()) listOf(year.toString())
                    else selectedSeasons.map { season -> "${season}_$year" }
                }

                _currentFilters.update { it.copy(season = seasons) }
            }

            is SetScore -> _currentFilters.update {
                it.copy(
                    score = event.score
                )
            }

            is SetDuration -> _currentFilters.update {
                it.copy(duration = it.duration.apply {
                    if (event.duration in it.duration) remove(event.duration) else add(event.duration)
                })
            }

            is SetRating -> _currentFilters.update {
                it.copy(rating = it.rating.apply {
                    if (event.rating in it.rating) remove(event.rating) else add(event.rating)
                })
            }

            is SetGenre -> _currentFilters.update {
                it.copy(genres = it.genres.apply {
                    if (event.genre in it.genres) remove(event.genre) else add(event.genre)
                })
            }

            is SetStudio -> {}
            is SetPublisher -> {}
            is SetFranchise -> {}
            is SetCensored -> {}
            is SetMyList -> {}
            is SetRole -> _currentFilters.update {
                when (event.item) {
                    SEYU -> it.copy(
                        isSeyu = event.flag,
                        roles = it.roles.apply { if (event.flag) add(SEYU) else remove(SEYU) })

                    PRODUCER -> it.copy(
                        isProducer = event.flag,
                        roles = it.roles.apply { if (event.flag) add(PRODUCER) else remove(PRODUCER) })

                    MANGAKA -> it.copy(
                        isMangaka = event.flag,
                        roles = it.roles.apply { if (event.flag) add(MANGAKA) else remove(MANGAKA) })
                }
            }

            is SetTitle -> {
                _state.update {
                    it.copy(
                        search = event.title
                    )
                }

                _currentFilters.update {
                    it.copy(
                        title = event.title
                    )
                }
            }
        }

        _state.value.menu.let(pagers::remove)
    }
}
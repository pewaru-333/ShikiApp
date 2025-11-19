@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package org.application.shikiapp.models.viewModels

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
import org.application.shikiapp.events.FilterEvent.SetStatus
import org.application.shikiapp.events.FilterEvent.SetStudio
import org.application.shikiapp.events.FilterEvent.SetTitle
import org.application.shikiapp.generated.type.MangaKindEnum
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.states.CatalogState
import org.application.shikiapp.models.states.ExpandedFilters
import org.application.shikiapp.models.states.FiltersState
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.mappers.toContent
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.paging.ContentPaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.CatalogItem
import org.application.shikiapp.utils.enums.PeopleFilterItem.MANGAKA
import org.application.shikiapp.utils.enums.PeopleFilterItem.PRODUCER
import org.application.shikiapp.utils.enums.PeopleFilterItem.SEYU
import org.application.shikiapp.utils.extensions.commaJoin
import org.application.shikiapp.utils.extensions.toggle
import org.application.shikiapp.utils.navigation.Screen
import org.application.shikiapp.utils.setScore
import java.util.concurrent.ConcurrentHashMap

class CatalogViewModel(val saved: SavedStateHandle) : ViewModel() {
    private val args by lazy { saved.toRoute<Screen.Catalog>() }

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _navEvent = Channel<Screen.Catalog>()
    val navEvent = _navEvent.receiveAsFlow().onStart { emit(args) }

    private val _filters = MutableStateFlow(CatalogItem.entries.associateWith { FiltersState() })
    private val _flowCache = ConcurrentHashMap<CatalogItem, Flow<PagingData<BasicContent>>>()

    val currentFilters = combine(_state, _filters) { state, map ->
        map[state.menu] ?: FiltersState()
    }.stateIn(viewModelScope, SharingStarted.Lazily, FiltersState())

    val list = _state
        .map { it.menu }
        .distinctUntilChanged()
        .flatMapLatest(::getOrCreateFlow)

    private val genresAnime = GraphQL.getAnimeGenres()
    private val genresManga = GraphQL.getMangaGenres()
    val genres = _state
        .map { it.menu }
        .distinctUntilChanged()
        .flatMapLatest { menu ->
            when (menu) {
                CatalogItem.ANIME -> genresAnime
                CatalogItem.MANGA, CatalogItem.RANOBE -> genresManga
                else -> flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun getOrCreateFlow(menu: CatalogItem) = _flowCache.getOrPut(menu) {
        _filters
            .map { it[menu] ?: FiltersState() }
            .distinctUntilChanged()
            .debounce(350L)
            .flatMapLatest { filters -> createPagingFlow(menu, filters) }
            .cachedIn(viewModelScope)
    }

    fun showFilters(menu: CatalogItem? = null) = _state.update {
        it.copy(dialogFilter = menu?.dialogFilter)
    }

    fun toggleExpandedFilter(filter: ExpandedFilters) = _state.update {
        it.copy(expandedFilters = it.expandedFilters.toggle(filter))
    }

    fun pick(menu: CatalogItem) {
        args.linkedType?.let {
            saved.keys().forEach { key ->
                saved[key] = null
            }
        }

        _state.update {
            it.copy(
                menu = menu,
                search = _filters.value.getValue(menu).title,
            )
        }
    }

    private fun createPagingFlow(item: CatalogItem, filtersState: FiltersState) = Pager(
        config = PagingConfig(
            pageSize = 50,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            if (item == CatalogItem.CLUBS) {
                CommonPaging(BasicContent::id) { page, params ->
                    fetchData(item, filtersState, page, params)
                }
            } else {
                ContentPaging(filtersState) { filters, page, params ->
                    fetchData(item, filters, page, params)
                }
            }
        }
    ).flow
        .retryWhen { cause, attempt ->
            cause is ClientRequestException && attempt < 3
        }

    private suspend fun fetchData(item: CatalogItem, query: FiltersState, page: Int, params: LoadParams<Int>) = when (item) {
        CatalogItem.ANIME -> GraphQL.getAnimeList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = setScore(query.status, query.score),
            duration = query.duration.commaJoin(),
            rating = query.rating.commaJoin(),
            genre = query.genres.commaJoin(),
            studio = query.studio,
            search = query.title
        )

        CatalogItem.MANGA -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = setScore(query.status, query.score),
            genre = query.genres.commaJoin(),
            publisher = query.publisher,
            search = query.title
        )

        CatalogItem.RANOBE -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.ifEmpty { listOf(MangaKindEnum.light_novel, MangaKindEnum.novel) }.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = setScore(query.status, query.score),
            genre = query.genres.commaJoin(),
            publisher = query.publisher,
            search = query.title
        )

        CatalogItem.CHARACTERS -> GraphQL.getCharacters(
            page = page, limit = params.loadSize, search = query.title
        )

        CatalogItem.PEOPLE -> GraphQL.getPeople(
            page = page,
            limit = params.loadSize,
            search = query.title,
            isSeyu = query.roles.contains(SEYU).takeIf { it },
            isProducer = query.roles.contains(PRODUCER).takeIf { it },
            isMangaka = query.roles.contains(MANGAKA).takeIf { it }
        )

        CatalogItem.USERS -> GraphQL.getUsers(
            search = query.title,
            page = page,
            limit = params.loadSize
        )

        CatalogItem.CLUBS -> Network.clubs.getClubs(
            search = query.title,
            page = page,
            limit = params.loadSize
        ).map(Club::toContent)
    }

    fun onEvent(event: FilterEvent) {
        when (event) {
            FilterEvent.ClearFilters -> {
                updateFilters { FiltersState() }
                _state.update { it.copy(search = BLANK) }
            }

            is SetOrder -> updateFilters {
                it.copy(order = event.order)
            }

            is SetStatus -> updateFilters {
                it.copy(status = it.status.toggle(event.status))
            }

            is SetKind -> updateFilters {
                it.copy(kind = it.kind.toggle(event.kind))
            }

            is SetSeason -> {
                when (event) {
                    is SetSeason.SetStartYear -> updateFilters { it.copy(seasonYearStart = event.year) }
                    is SetSeason.SetFinalYear -> updateFilters { it.copy(seasonYearFinal = event.year) }
                    is SetSeason.ToggleSeasonYear -> updateFilters {
                        it.copy(seasonYearSeason = it.seasonYearSeason.toggle(event.yearSeason))
                    }
                }
                val current = _filters.value[_state.value.menu] ?: FiltersState()

                val yearStart = current.seasonYearStart.toIntOrNull() ?: 1900
                val yearEnd = current.seasonYearFinal.toIntOrNull() ?: 2100
                val selectedSeasons = current.seasonYearSeason

                val seasons = (yearStart..yearEnd).flatMapTo(HashSet()) { year ->
                    if (selectedSeasons.isEmpty()) listOf(year.toString())
                    else selectedSeasons.map { season -> "${season}_$year" }
                }

                updateFilters { it.copy(seasonSet = seasons) }
            }

            is SetScore -> updateFilters {
                it.copy(score = event.score)
            }

            is SetDuration -> updateFilters {
                it.copy(duration = it.duration.toggle(event.duration))
            }

            is SetRating -> updateFilters {
                it.copy(rating = it.rating.toggle(event.rating))
            }

            is SetGenre -> updateFilters {
                it.copy(genres = it.genres.toggle(event.genre))
            }

            is SetStudio -> updateFilters { it.copy(studio = event.studio) }
            is SetPublisher -> updateFilters { it.copy(publisher = event.publisher) }
            is SetFranchise -> {}
            is SetCensored -> {}
            is SetMyList -> {}
            is SetRole -> updateFilters {
                it.copy(roles = it.roles.toggle(event.item))
            }

            is SetTitle -> {
                _state.update { it.copy(search = event.title) }

                updateFilters { it.copy(title = event.title) }
            }
        }
    }

    fun updateFilters(block: (FiltersState) -> FiltersState) {
        val currentMenu = _state.value.menu

        _filters.update { map ->
            val current = map[currentMenu] ?: FiltersState()
            map + (currentMenu to block(current))
        }
    }
}
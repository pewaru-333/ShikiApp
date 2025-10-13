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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
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
import org.application.shikiapp.utils.extensions.toggle
import org.application.shikiapp.utils.navigation.Screen
import org.application.shikiapp.utils.setScore

class CatalogViewModel(val saved: SavedStateHandle) : ViewModel() {
    private val args: Screen.Catalog
        get() = saved.toRoute<Screen.Catalog>()

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _navEvent = Channel<Screen.Catalog>()
    val navEvent = _navEvent.receiveAsFlow().onStart { emit(args) }

    private val filters = CatalogItem.entries.associateWith { MutableStateFlow(FiltersState()) }
    private val pagers = mutableMapOf<CatalogItem, Flow<PagingData<BasicContent>>>()

    private val _currentFilters = MutableStateFlow(FiltersState())
    val currentFilters = _currentFilters.stateIn(viewModelScope, SharingStarted.Lazily, FiltersState())

    val list = combine(_state, _currentFilters) { state, currentFilters ->
        pagers.getOrPut(state.menu) {
            createPagingFlow(state.menu, currentFilters).cachedIn(viewModelScope)
        }
    }.flatMapLatest { it }

    private val genresAnime = GraphQL.getAnimeGenres()
    private val genresManga = GraphQL.getMangaGenres()
    private val _genres = mapOf(
        CatalogItem.ANIME to genresAnime,
        CatalogItem.MANGA to genresManga,
        CatalogItem.RANOBE to genresManga
    )

    val genres = _state.flatMapLatest { _genres.getOrDefault(it.menu, emptyFlow()) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun showFilters(menu: CatalogItem? = null) = _state.update {
        it.copy(dialogFilter = menu?.dialogFilter)
    }

    fun toggleExpandedFilter(filter: ExpandedFilters) = _state.update {
        it.copy(expandedFilters = it.expandedFilters.toggle(filter))
    }

    fun pick(menu: CatalogItem) {
        filters.getValue(_state.value.menu).update { _currentFilters.value }
        _currentFilters.update { filters.getValue(menu).value }

        args.linkedType?.let {
            saved.keys().forEach { key ->
                saved[key] = null
            }
        }

        _state.update {
            it.copy(
                menu = menu,
                search = _currentFilters.value.title,
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
        .debounce(350L)
        .retryWhen { cause, attempt ->
            cause is ClientRequestException && attempt < 3
        }

    private suspend fun fetchData(item: CatalogItem, query: FiltersState, page: Int, params: LoadParams<Int>) = when (item) {
        CatalogItem.ANIME -> GraphQL.getAnimeList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.joinToString(","),
            status = query.status.joinToString(","),
            season = query.seasonSet.joinToString(","),
            score = setScore(query.status, query.score),
            duration = query.duration.joinToString(","),
            rating = query.rating.joinToString(","),
            genre = query.genres.joinToString(","),
            studio = query.studio,
            search = query.title
        )

        CatalogItem.MANGA -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = query.kind.joinToString(","),
            status = query.status.joinToString(","),
            season = query.seasonSet.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
            publisher = query.publisher,
            search = query.title
        )

        CatalogItem.RANOBE -> GraphQL.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order.name.lowercase(),
            kind = if (query.kind.isNotEmpty()) query.kind.joinToString(",")
            else listOf(MangaKindEnum.light_novel, MangaKindEnum.novel).joinToString(","),
            status = query.status.joinToString(","),
            season = query.seasonSet.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
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
                _currentFilters.update { FiltersState() }
                _state.update { it.copy(search = BLANK) }
            }

            is SetOrder -> _currentFilters.update {
                it.copy(order = event.order)
            }

            is SetStatus -> _currentFilters.update {
                it.copy(status = it.status.toggle(event.status))
            }

            is SetKind -> _currentFilters.update {
                it.copy(kind = it.kind.toggle(event.kind))
            }

            is SetSeason -> {
                when (event) {
                    is SetSeason.SetStartYear -> _currentFilters.update { it.copy(seasonYearStart = event.year) }
                    is SetSeason.SetFinalYear -> _currentFilters.update { it.copy(seasonYearFinal = event.year) }
                    is SetSeason.ToggleSeasonYear -> _currentFilters.update {
                        it.copy(seasonYearSeason = it.seasonYearSeason.toggle(event.yearSeason))
                    }
                }

                val yearStart = _currentFilters.value.seasonYearStart.toIntOrNull() ?: 1900
                val yearEnd = _currentFilters.value.seasonYearFinal.toIntOrNull() ?: 2100
                val selectedSeasons = _currentFilters.value.seasonYearSeason

                val seasons = (yearStart..yearEnd).flatMapTo(HashSet()) { year ->
                    if (selectedSeasons.isEmpty()) listOf(year.toString())
                    else selectedSeasons.map { season -> "${season}_$year" }
                }

                _currentFilters.update { it.copy(seasonSet = seasons) }
            }

            is SetScore -> _currentFilters.update {
                it.copy(score = event.score)
            }

            is SetDuration -> _currentFilters.update {
                it.copy(duration = it.duration.toggle(event.duration))
            }

            is SetRating -> _currentFilters.update {
                it.copy(rating = it.rating.toggle(event.rating))
            }

            is SetGenre -> _currentFilters.update {
                it.copy(genres = it.genres.toggle(event.genre))
            }

            is SetStudio -> _currentFilters.update { it.copy(studio = event.studio) }
            is SetPublisher -> _currentFilters.update { it.copy(publisher = event.publisher) }
            is SetFranchise -> {}
            is SetCensored -> {}
            is SetMyList -> {}
            is SetRole -> _currentFilters.update {
                it.copy(roles = it.roles.toggle(event.item))
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
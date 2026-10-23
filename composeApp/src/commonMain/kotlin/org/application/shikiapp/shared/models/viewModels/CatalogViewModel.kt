@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.serialization.builtins.nullable
import org.application.shikiapp.generated.common.fragment.Genres
import org.application.shikiapp.generated.common.type.MangaKindEnum
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.FilterEvent
import org.application.shikiapp.shared.models.data.Club
import org.application.shikiapp.shared.models.states.CatalogState
import org.application.shikiapp.shared.models.states.ExpandedFilters
import org.application.shikiapp.shared.models.states.FiltersState
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.mappers.toContent
import org.application.shikiapp.shared.network.client.GraphQL
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging
import org.application.shikiapp.shared.network.paging.ContentPaging
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.CatalogItem
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.PeopleFilterItem
import org.application.shikiapp.shared.utils.extensions.commaJoin
import org.application.shikiapp.shared.utils.extensions.toggle
import org.application.shikiapp.shared.utils.extensions.toggleFilter
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.serializableNavType
import org.application.shikiapp.shared.utils.ui.Formatter
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.milliseconds

class CatalogViewModel(saved: SavedStateHandle) : ViewModel() {
    private val pageSize = 50
    private val args = saved.toRoute<Screen.Catalog>(
        typeMap = mapOf(
            typeOf<LinkedType?>() to serializableNavType(LinkedType.serializer().nullable)
        )
    )

    private val _state = MutableStateFlow(
        value = CatalogState(
            menu = when (args.linkedType) {
                LinkedType.ANIME -> CatalogItem.ANIME
                LinkedType.MANGA -> CatalogItem.MANGA
                LinkedType.RANOBE -> CatalogItem.RANOBE
                else -> if (args.publisher != null) CatalogItem.RANOBE else CatalogItem.ANIME
            }
        )
    )
    val state = _state.asStateFlow()

    private val _filters = MutableStateFlow(
        value = CatalogItem.entries.associateWith { menu ->
            if (menu == _state.value.menu) {
                FiltersState(
                    genres = setOfNotNull(args.genre),
                    studio = args.studio,
                    publisher = args.publisher,
                    status = if (args.showOngoing == true) setOf("ongoing") else emptySet()
                )
            } else FiltersState()
        }
    )
    private val _flowCache = mutableMapOf<CatalogItem, Flow<PagingData<BasicContent>>>()
    private val _genresCache = HashMap<LinkedType, List<Genres>>()

    val filters = _filters.asStateFlow()
    val list = _state
        .map { it.menu }
        .distinctUntilChanged()
        .flatMapLatest(::getOrCreateFlow)

    val genres = _state
        .map {
            when (it.menu) {
                CatalogItem.ANIME -> LinkedType.ANIME
                CatalogItem.MANGA, CatalogItem.RANOBE -> LinkedType.MANGA
                else -> null
            }
        }
        .distinctUntilChanged()
        .flatMapLatest { type ->
            if (type == null) return@flatMapLatest flowOf(emptyList())

            val cached = _genresCache[type]
            if (cached != null) return@flatMapLatest flowOf(cached)

            val request = if (type == LinkedType.ANIME) GraphQL.getAnimeGenres()
            else GraphQL.getMangaGenres()

            request
                .onEach { _genresCache[type] = it }
                .onStart { emit(emptyList()) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun getOrCreateFlow(menu: CatalogItem) = _flowCache.getOrPut(menu) {
        var previous: FiltersState? = null

        _filters
            .map { it.getValue(menu) }
            .distinctUntilChanged()
            .transformLatest { filters ->
                val old = previous
                previous = filters

                val isDelay = old != null && filters != FiltersState() && old == filters.copy(
                    title = old.title,
                    score = old.score,
                    seasonYearStart = old.seasonYearStart,
                    seasonYearFinal = old.seasonYearFinal,
                    seasonSet = old.seasonSet
                )

                if (isDelay) delay(350.milliseconds)

                emit(
                    value = filters.copy(
                        seasonYearStart = BLANK,
                        seasonYearFinal = BLANK,
                        seasonYearSeason = emptySet()
                    )
                )
            }
            .distinctUntilChanged()
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
        _state.update {
            it.copy(
                menu = menu,
                search = _filters.value.getValue(menu).title,
            )
        }
    }

    private fun createPagingFlow(item: CatalogItem, filtersState: FiltersState) = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            if (item == CatalogItem.CLUBS) {
                CommonPaging(BasicContent::id) { page, _ ->
                    fetchData(item, filtersState, page)
                }
            } else {
                ContentPaging { page ->
                    fetchData(item, filtersState, page)
                }
            }
        }
    ).flow

    private suspend fun fetchData(item: CatalogItem, query: FiltersState, page: Int) = when (item) {
        CatalogItem.ANIME -> GraphQL.getAnimeList(
            page = page,
            limit = pageSize,
            order = query.order.name.lowercase(),
            kind = query.kind.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = Formatter.setScore(query.status, query.score),
            duration = query.duration.commaJoin(),
            rating = query.rating.commaJoin(),
            genre = query.genres.commaJoin(),
            studio = query.studio,
            search = query.title
        )

        CatalogItem.MANGA -> GraphQL.getMangaList(
            page = page,
            limit = pageSize,
            order = query.order.name.lowercase(),
            kind = query.kind.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = Formatter.setScore(query.status, query.score),
            genre = query.genres.commaJoin(),
            publisher = query.publisher,
            search = query.title
        )

        CatalogItem.RANOBE -> GraphQL.getMangaList(
            page = page,
            limit = pageSize,
            order = query.order.name.lowercase(),
            kind = buildSet {
                addAll(query.kind)
                if (query.kind.none { !it.startsWith("!") }) {
                    add(MangaKindEnum.light_novel.toString())
                    add(MangaKindEnum.novel.toString())
                }
            }.commaJoin(),
            status = query.status.commaJoin(),
            season = query.seasonSet.commaJoin(),
            score = Formatter.setScore(query.status, query.score),
            genre = query.genres.commaJoin(),
            publisher = query.publisher,
            search = query.title
        )

        CatalogItem.CHARACTERS -> GraphQL.getCharacters(
            page = page, limit = pageSize, search = query.title
        )

        CatalogItem.PEOPLE -> GraphQL.getPeople(
            page = page,
            limit = pageSize,
            search = query.title,
            isSeyu = query.roles.contains(PeopleFilterItem.SEYU).takeIf { it },
            isProducer = query.roles.contains(PeopleFilterItem.PRODUCER).takeIf { it },
            isMangaka = query.roles.contains(PeopleFilterItem.MANGAKA).takeIf { it }
        )

        CatalogItem.USERS -> GraphQL.getUsers(
            search = query.title,
            page = page,
            limit = pageSize
        )

        CatalogItem.CLUBS -> Network.clubs.getClubs(
            search = query.title,
            page = page,
            limit = pageSize
        ).map(Club::toContent)
    }

    fun onEvent(event: FilterEvent) {
        when (event) {
            FilterEvent.ClearFilters -> {
                updateFilters { FiltersState() }
                _state.update { it.copy(search = BLANK) }
            }

            is FilterEvent.SetOrder -> {
                updateFilters {
                    it.copy(order = event.order)
                }

                if (Preferences.rememberCatalogOrder.value) {
                    Preferences.lastCatalogOrder.value = event.order
                }
            }

            is FilterEvent.SetStatus -> updateFilters {
                it.copy(status = it.status.toggleFilter(event.status))
            }

            is FilterEvent.SetKind -> updateFilters {
                it.copy(kind = it.kind.toggleFilter(event.kind))
            }

            is FilterEvent.SetSeason -> updateFilters { current ->
                val filters = when (event) {
                    is FilterEvent.SetSeason.SetStartYear -> current.copy(seasonYearStart = event.year)
                    is FilterEvent.SetSeason.SetFinalYear -> current.copy(seasonYearFinal = event.year)
                    is FilterEvent.SetSeason.ToggleSeasonYear -> current.copy(
                        seasonYearSeason = current.seasonYearSeason.toggleFilter(event.yearSeason)
                    )
                }

                val start = filters.seasonYearStart
                val end = filters.seasonYearFinal
                val selectedSeasons = filters.seasonYearSeason

                if (start.isEmpty() && end.isEmpty() && selectedSeasons.isEmpty()) {
                    return@updateFilters filters.copy(seasonSet = emptySet())
                }

                if ((start.isNotEmpty() && start.length != 4) || (end.isNotEmpty() && end.length != 4)) {
                    return@updateFilters filters
                }

                val yearStart = if (start.isEmpty()) 1900 else start.toIntOrNull()
                val yearEnd = if (end.isEmpty()) 2100 else end.toIntOrNull()
                if (yearStart == null || yearEnd == null || yearStart <= 0 || yearEnd < yearStart) {
                    return@updateFilters filters
                }

                val seasons = buildSet {
                    if (selectedSeasons.isNotEmpty() && selectedSeasons.all { it.startsWith("!") }) {
                        add(if (yearStart == yearEnd) yearStart.toString() else "${yearStart}_$yearEnd")
                    }

                    for (year in yearStart..yearEnd) {
                        if (selectedSeasons.isEmpty()) {
                            add(year.toString())
                        } else {
                            for (season in selectedSeasons) {
                                add("${season.replace("autumn", "fall")}_$year")
                            }
                        }
                    }
                }

                filters.copy(seasonSet = seasons)
            }

            is FilterEvent.SetScore -> updateFilters {
                it.copy(score = event.score)
            }

            is FilterEvent.SetDuration -> updateFilters {
                it.copy(duration = it.duration.toggleFilter(event.duration))
            }

            is FilterEvent.SetRating -> updateFilters {
                it.copy(rating = it.rating.toggleFilter(event.rating))
            }

            is FilterEvent.SetGenre -> updateFilters {
                it.copy(genres = it.genres.toggleFilter(event.genre))
            }

            is FilterEvent.SetStudio -> updateFilters { it.copy(studio = event.studio) }
            is FilterEvent.SetPublisher -> updateFilters { it.copy(publisher = event.publisher) }
            is FilterEvent.SetFranchise -> {}
            is FilterEvent.SetCensored -> {}
            is FilterEvent.SetMyList -> {}
            is FilterEvent.SetRole -> updateFilters {
                it.copy(roles = it.roles.toggle(event.item))
            }

            is FilterEvent.SetTitle -> {
                _state.update { it.copy(search = event.title) }

                updateFilters { it.copy(title = event.title) }
            }
        }
    }

    private fun updateFilters(block: (FiltersState) -> FiltersState) {
        val currentMenu = _state.value.menu

        _filters.update { map ->
            val current = map.getValue(currentMenu)
            map + (currentMenu to block(current))
        }
    }
}
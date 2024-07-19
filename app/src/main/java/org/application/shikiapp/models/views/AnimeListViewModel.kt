package org.application.shikiapp.models.views

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.AnimeGenresQuery
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.paging.AnimePaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ORDERS
import retrofit2.HttpException
import java.time.LocalDate

class AnimeListViewModel : ViewModel() {
    private val _filters = MutableStateFlow(QueryMap())
    val filters = _filters.asStateFlow()

    private val _genres = MutableStateFlow<List<AnimeGenresQuery.Genre>>(mutableListOf())
    val genres = _genres.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val list = _filters.debounce(300).flatMapLatest { value ->
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { AnimePaging(value) }
        ).flow.map { list ->
            val set = mutableSetOf<String>()
            list.filter { if (it.id in set) false else set.add(it.id) }
        }.cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }
    }

    init {
        getGenres()
    }

    fun onEvent(event: QueryEvent) {
        when (event) {
            is QueryEvent.SetOrder -> _filters.update {
                it.copy(order = event.order.key, orderName = event.order.value)
            }

            is QueryEvent.SetStatus -> _filters.update {
                it.copy(status = it.status.apply {
                    if (event.status in it.status) remove(event.status) else add(event.status)
                })
            }

            is QueryEvent.SetKind -> _filters.update {
                it.copy(kind = it.kind.apply {
                    if (event.kind in it.kind) remove(event.kind) else add(event.kind)
                })
            }

            is QueryEvent.SetSeasonYS ->
                if (event.year.length <= 4) _filters.update { it.copy(seasonYS = event.year) }

            is QueryEvent.SetSeasonYF ->
                if (event.year.length <= 4) _filters.update { it.copy(seasonYF = event.year) }

            is QueryEvent.SetSeasonS -> _filters.update {
                it.copy(season = it.seasonS.apply {
                    if (event.season in it.seasonS) remove(event.season) else add(event.season)
                })
            }

            is QueryEvent.SetSeason -> {
                val yearS = try {
                    _filters.value.seasonYS.toInt()
                } catch (e: NumberFormatException) {
                    1900
                }

                val yearF = try {
                    _filters.value.seasonYF.toInt()
                } catch (e: NumberFormatException) {
                    LocalDate.now().year + 5
                }

                val seasons = SnapshotStateList<String>()

                for (year in yearS..yearF) {
                    _filters.value.seasonS.apply {
                        if (isNotEmpty()) forEach { season -> seasons.add("${season}_$year") }
                        else seasons.add("$year")
                    }
                }

                _filters.update { it.copy(season = seasons) }
            }

            is QueryEvent.SetScore -> _filters.update { it.copy(score = event.score) }

            is QueryEvent.SetDuration -> _filters.update {
                it.copy(duration = it.duration.apply {
                    if (event.duration in it.duration) remove(event.duration) else add(event.duration)
                })
            }

            is QueryEvent.SetRating -> _filters.update {
                it.copy(rating = it.rating.apply {
                    if (event.rating in it.rating) remove(event.rating) else add(event.rating)
                })
            }

            is QueryEvent.SetGenre -> _filters.update {
                it.copy(genre = it.genre.apply {
                    if (event.genre in it.genre) remove(event.genre) else add(event.genre)
                })
            }

            is QueryEvent.SetStudio -> {}
            is QueryEvent.SetFranchise -> {}
            is QueryEvent.SetCensored -> {}
            is QueryEvent.SetMyList -> {}
            is QueryEvent.SetTitle -> event.title.let { title ->
                _filters.update { it.copy(title = title.ifEmpty { null }) }
            }
        }
    }

    private fun getGenres() {
        viewModelScope.launch {
            try {
                _genres.emit(ApolloClient.getAnimeGenres())
            } catch (e: Throwable) {
                LoadState.Error(e)
            }
        }
    }
}

data class QueryMap(
    val order: String = ORDERS.keys.elementAt(2),
    val orderName: String = ORDERS.values.elementAt(2),
    val kind: SnapshotStateList<String> = mutableStateListOf(),
    val status: SnapshotStateList<String> = mutableStateListOf(),
    val seasonYS: String = BLANK,
    val seasonYF: String = BLANK,
    val seasonS: SnapshotStateList<String> = mutableStateListOf(),
    val season: SnapshotStateList<String> = mutableStateListOf(),
    val score: Float = 6f,
    val duration: SnapshotStateList<String> = mutableStateListOf(),
    val rating: SnapshotStateList<String> = mutableStateListOf(),
    val genre: SnapshotStateList<String> = mutableStateListOf(),
    val studio: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val title: String? = null
)

sealed interface QueryEvent {
    data class SetOrder(val order: Map.Entry<String, String>) : QueryEvent
    data class SetStatus(val status: String) : QueryEvent
    data class SetKind(val kind: String) : QueryEvent
    data class SetSeasonYS(val year: String) : QueryEvent
    data class SetSeasonYF(val year: String) : QueryEvent
    data class SetSeasonS(val season: String) : QueryEvent
    data object SetSeason : QueryEvent
    data class SetScore(val score: Float) : QueryEvent
    data class SetDuration(val duration: String) : QueryEvent
    data class SetRating(val rating: String) : QueryEvent
    data class SetGenre(val genre: String) : QueryEvent
    data class SetStudio(val studio: String) : QueryEvent
    data class SetFranchise(val franchise: String) : QueryEvent
    data class SetCensored(val censored: Boolean) : QueryEvent
    data class SetMyList(val myList: String) : QueryEvent
    data class SetTitle(val title: String) : QueryEvent
}
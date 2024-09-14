package org.application.shikiapp.models.views

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import org.application.fragment.GenresF
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetCensored
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetDuration
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetFranchise
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetGenre
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetKind
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetMyList
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetOrder
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetPublisher
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetRating
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetRole
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetScore
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetSeason
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetSeasonS
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetSeasonYF
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetSeasonYS
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetStatus
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetStudio
import org.application.shikiapp.models.views.FiltersViewModel.FilterEvent.SetTitle
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.PeopleFilterItems
import org.application.shikiapp.utils.PeopleFilterItems.Mangaka
import org.application.shikiapp.utils.PeopleFilterItems.Producer
import org.application.shikiapp.utils.PeopleFilterItems.Seyu
import java.time.LocalDate

open class FiltersViewModel<T : Any> : ViewModel() {
    private val _filters = MutableStateFlow(CatalogFilters())
    val filters = _filters.asStateFlow()

    open val list = emptyFlow<PagingData<T>>()
    val genres = mutableListOf<GenresF>()

    fun onEvent(event: FilterEvent) {
        when (event) {
            is SetOrder -> _filters.update {
                it.copy(order = event.order.key, orderName = event.order.value)
            }

            is SetStatus -> _filters.update {
                it.copy(status = it.status.apply {
                    if (event.status in it.status) remove(event.status) else add(event.status)
                })
            }

            is SetKind -> _filters.update {
                it.copy(kind = it.kind.apply {
                    if (event.kind in it.kind) remove(event.kind) else add(event.kind)
                })
            }

            is SetSeasonYS ->
                if (event.year.length <= 4) _filters.update { it.copy(seasonYS = event.year) }

            is SetSeasonYF ->
                if (event.year.length <= 4) _filters.update { it.copy(seasonYF = event.year) }

            is SetSeasonS -> _filters.update {
                it.copy(season = it.seasonS.apply {
                    if (event.season in it.seasonS) remove(event.season) else add(event.season)
                })
            }

            is SetSeason -> {
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

                (yearS..yearF).forEach { year ->
                    _filters.value.seasonS.apply {
                        if (isNotEmpty()) forEach { season -> seasons.add("${season}_$year") }
                        else seasons.add("$year")
                    }
                }

                _filters.update { it.copy(season = seasons) }
            }

            is SetScore -> _filters.update { it.copy(score = event.score) }

            is SetDuration -> _filters.update {
                it.copy(duration = it.duration.apply {
                    if (event.duration in it.duration) remove(event.duration) else add(event.duration)
                })
            }

            is SetRating -> _filters.update {
                it.copy(rating = it.rating.apply {
                    if (event.rating in it.rating) remove(event.rating) else add(event.rating)
                })
            }

            is SetGenre -> _filters.update {
                it.copy(genres = it.genres.apply {
                    if (event.genre in it.genres) remove(event.genre) else add(event.genre)
                })
            }

            is SetStudio -> {}
            is SetPublisher -> {}
            is SetFranchise -> {}
            is SetCensored -> {}
            is SetMyList -> {}
            is SetRole -> _filters.update {
                when (event.item) {
                    Seyu -> it.copy(
                        isSeyu = event.flag,
                        roles = it.roles.apply { if (event.flag) add(Seyu) else remove(Seyu) }
                    )

                    Producer -> it.copy(
                        isProducer = event.flag,
                        roles = it.roles.apply { if (event.flag) add(Producer) else remove(Producer) }
                    )

                    Mangaka -> it.copy(
                        isMangaka = event.flag,
                        roles = it.roles.apply { if (event.flag) add(Mangaka) else remove(Mangaka) }
                    )
                }
            }

            is SetTitle -> _filters.update { it.copy(title = event.title) }
        }
    }


    sealed interface FilterEvent {
        data class SetOrder(val order: Map.Entry<String, String>) : FilterEvent
        data class SetStatus(val status: String) : FilterEvent
        data class SetKind(val kind: String) : FilterEvent
        data class SetSeasonYS(val year: String) : FilterEvent
        data class SetSeasonYF(val year: String) : FilterEvent
        data class SetSeasonS(val season: String) : FilterEvent
        data object SetSeason : FilterEvent
        data class SetScore(val score: Float) : FilterEvent
        data class SetDuration(val duration: String) : FilterEvent
        data class SetRating(val rating: String) : FilterEvent
        data class SetGenre(val genre: String) : FilterEvent
        data class SetStudio(val studio: String) : FilterEvent
        data class SetPublisher(val studio: String) : FilterEvent
        data class SetFranchise(val franchise: String) : FilterEvent
        data class SetCensored(val censored: Boolean) : FilterEvent
        data class SetMyList(val myList: String) : FilterEvent
        data class SetRole(val flag: Boolean, val item: PeopleFilterItems): FilterEvent
        data class SetTitle(val title: String) : FilterEvent
    }
}

@Stable
data class CatalogFilters(
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
    val genres: SnapshotStateList<String> = mutableStateListOf(),
    val studio: String? = null,
    val publisher: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val isSeyu: Boolean? = null,
    val isProducer: Boolean? = null,
    val isMangaka: Boolean? = null,
    val roles: MutableList<PeopleFilterItems> = mutableListOf(),
    val title: String = BLANK
)
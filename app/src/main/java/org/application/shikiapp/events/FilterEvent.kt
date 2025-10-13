package org.application.shikiapp.events

import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.enums.PeopleFilterItem

sealed interface FilterEvent {
    data object ClearFilters : FilterEvent

    sealed interface SetSeason : FilterEvent {
        data class SetStartYear(val year: String) : SetSeason
        data class SetFinalYear(val year: String) : SetSeason
        data class ToggleSeasonYear(val yearSeason: String) : SetSeason
    }

    data class SetOrder(val order: Order) : FilterEvent
    data class SetStatus(val status: String) : FilterEvent
    data class SetKind(val kind: String) : FilterEvent
    data class SetScore(val score: Float) : FilterEvent
    data class SetDuration(val duration: String) : FilterEvent
    data class SetRating(val rating: String) : FilterEvent
    data class SetGenre(val genre: String) : FilterEvent
    data class SetStudio(val studio: String) : FilterEvent
    data class SetPublisher(val publisher: String) : FilterEvent
    data class SetFranchise(val franchise: String) : FilterEvent
    data class SetCensored(val censored: Boolean) : FilterEvent
    data class SetMyList(val myList: String) : FilterEvent
    data class SetRole(val item: PeopleFilterItem) : FilterEvent
    data class SetTitle(val title: String) : FilterEvent
}
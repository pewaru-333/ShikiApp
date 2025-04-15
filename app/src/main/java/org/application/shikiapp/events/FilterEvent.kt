package org.application.shikiapp.events

import org.application.shikiapp.utils.enums.PeopleFilterItems

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
    data class SetRole(val flag: Boolean, val item: PeopleFilterItems) : FilterEvent
    data class SetTitle(val title: String) : FilterEvent
}
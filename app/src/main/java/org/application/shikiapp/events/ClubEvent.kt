package org.application.shikiapp.events

import org.application.shikiapp.utils.enums.ClubMenu

sealed interface ClubEvent {
    data object ShowClubs : ClubEvent
    data object ShowComments : ClubEvent

    data object ShowBottomSheet : ClubEvent

    data object JoinClub : ClubEvent
    data object LeaveClub : ClubEvent

    data class PickItem(val item: ClubMenu? = null) : ClubEvent
    data class ShowFullImage(val url: String? = null) : ClubEvent
}
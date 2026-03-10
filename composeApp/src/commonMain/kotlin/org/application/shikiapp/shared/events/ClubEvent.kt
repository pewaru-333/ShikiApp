package org.application.shikiapp.shared.events

import org.application.shikiapp.shared.utils.enums.ClubMenu

sealed interface ClubEvent {
    data object ShowClubs : ClubEvent
    data object ShowComments : ClubEvent

    data object ShowBottomSheet : ClubEvent

    data object JoinClub : ClubEvent
    data object LeaveClub : ClubEvent

    data class PickItem(val item: ClubMenu? = null) : ClubEvent
    data class ShowFullImage(val url: String? = null) : ClubEvent
}
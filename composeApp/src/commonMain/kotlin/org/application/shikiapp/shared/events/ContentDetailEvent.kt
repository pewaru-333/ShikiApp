package org.application.shikiapp.shared.events

import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.utils.enums.Kind

sealed interface ContentDetailEvent {
    data object OpenLink : ContentDetailEvent

    data class ToggleDialog(val dialogState: BaseDialogState?) : ContentDetailEvent

    data class SendComment(val text: String, val isOfftopic: Boolean) : ContentDetailEvent

    sealed interface Media : ContentDetailEvent {
        data object ChangeRate : Media

        sealed interface Anime : ContentDetailEvent {
            data object ToggleFavourite : Anime
        }

        sealed interface Manga : ContentDetailEvent {
            data class ToggleFavourite(val type: Kind?) : Manga
        }
    }

    sealed interface Character : ContentDetailEvent {
        data object ToggleFavourite : Character
    }

    sealed interface Person : ContentDetailEvent {
        data class ToggleFavourite(val kind: String) : Person
    }

    sealed interface User : ContentDetailEvent {
        data object ToggleFriend : User
    }

    sealed interface Club : ContentDetailEvent {
        data object JoinClub : Club
        data object LeaveClub : Club
    }
}
package org.application.shikiapp.events

import org.application.shikiapp.models.states.UserDialogState
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.UserMenu

sealed interface ContentDetailEvent {
    data object OpenLink : ContentDetailEvent
    data object ShowComments : ContentDetailEvent
    data object ShowSheet : ContentDetailEvent

    sealed interface Media : ContentDetailEvent {
        data object ShowAuthors : Media
        data object ShowCharacters : Media
        data object ShowRelated : Media
        data object ShowSimilar : Media
        data object ShowStats : Media
        data object ShowLinks : Media
        data object ShowFansubbers : Media
        data object ShowFandubbers : Media
        data object ShowRate : Media
        data object ChangeRate : Media

        data class ShowImage(val index: Int = 0) : Media
        data class SetImage(val index: Int) : Media

        sealed interface Anime : ContentDetailEvent {
            data object ShowScreenshots : Anime
            data object ShowVideo : Anime

            data object ToggleFavourite : Anime
        }

        sealed interface Manga : ContentDetailEvent {
            data class ToggleFavourite(val type: Kind?) : Manga
        }
    }

    sealed interface Character : ContentDetailEvent {
        data object ShowSeyu : Character

        data object ToggleFavourite : Character
    }

    sealed interface Person : ContentDetailEvent {
        data object ShowWorks : Person

        data class ToggleFavourite(val kind: String) : Person
    }

    sealed interface User : ContentDetailEvent {
        data class PickMenu(val menu: UserMenu? = null) : User

        data class ToggleDialog(val dialog: UserDialogState?) : User

        data object ToggleFriend : User
    }
}
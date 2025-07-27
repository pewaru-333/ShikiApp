package org.application.shikiapp.events

import org.application.shikiapp.generated.type.MangaKindEnum
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.UserMenu

sealed interface ContentDetailEvent {
    data object ShowComments : ContentDetailEvent
    data object ShowSheet : ContentDetailEvent

    sealed interface Media : ContentDetailEvent {
        data object Reload : Media
        data object ShowAuthors : Media
        data object ShowCharacters : Media
        data object ShowRelated : Media
        data object ShowSimilar : Media
        data object ShowStats : Media
        data object ShowLinks : Media
        data object ShowRate : Media

        data class ShowImage(val index: Int = 0) : Media
        data class SetImage(val index: Int) : Media

        sealed interface Anime : ContentDetailEvent {
            data object ShowScreenshots : Anime
            data object ShowVideo : Anime

            data class ToggleFavourite(val favoured: Boolean) : Anime
        }

        sealed interface Manga : ContentDetailEvent {
            data class ToggleFavourite(val type: MangaKindEnum?, val favoured: Boolean) : Manga
        }
    }

    sealed interface Character : ContentDetailEvent {
        data object ShowAnime : Character
        data object ShowManga : Character
        data object ShowSeyu : Character
        data object HideAll : Character

        data class ToggleFavourite(val favoured: Boolean) : Character
    }

    sealed interface Person : ContentDetailEvent {
        data class ToggleFavourite(val kind: String, val favoured: Boolean) : Person
    }

    sealed interface User : ContentDetailEvent {
        data object ToggleFriend : User

        data object ShowSettings : User

        data object ShowDialogs : User
        data object ShowDialogToggleFriend : User

        data class PickMenu(val menu: UserMenu? = null) : User
        data class PickFavouriteTab(val tab: FavouriteItem) : User
    }
}
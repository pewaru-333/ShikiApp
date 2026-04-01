package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.models.ui.list.ContentViewType
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_clubs
import shikiapp.composeapp.generated.resources.text_favourite
import shikiapp.composeapp.generated.resources.text_friends
import shikiapp.composeapp.generated.resources.text_history
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_members
import shikiapp.composeapp.generated.resources.text_pictures
import shikiapp.composeapp.generated.resources.text_ranobe

interface BaseState<S> {
    val isSendingComment: Boolean
    val dialogState: BaseDialogState?

    fun updateSendingState(isSending: Boolean): S
    fun updateDialogState(dialogState: BaseDialogState?): S
}

sealed interface BaseDialogState {
    data object Comments : BaseDialogState
    data object Poster : BaseDialogState
    data object Sheet : BaseDialogState

    sealed interface Media : BaseDialogState {
        data object Authors : Media
        data object Characters : Media
        data object Links : Media
        data object Rate : Media
        data object Related : Media
        data object Similar : Media
        data object Stats : Media

        data class Image(val index: Int = 0, val parentDialog: BaseDialogState? = null) : Media
    }

    sealed interface Anime : Media {
        data object Fandubbers : Anime
        data object Fansubbers : Anime
        data object Screenshot : Anime
        data object Screenshots : Anime
        data object Video : Anime
    }

    sealed interface Club : BaseDialogState {
        data class Image(val url: String?) : Club

        enum class Menu(val title: StringResource) : Club {
            ANIME(Res.string.text_anime) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
                override fun navigateTo(contentId: String) = Screen.Anime(contentId)
            },
            CHARACTERS(Res.string.text_characters) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
                override fun navigateTo(contentId: String) = Screen.Character(contentId)
            },
            MANGA(Res.string.text_manga) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
                override fun navigateTo(contentId: String) = Screen.Manga(contentId)
            },
            MEMBERS(Res.string.text_members) {
                override val viewType = ContentViewType.GRID_ITEM_SMALL
                override fun navigateTo(contentId: String) = Screen.User(contentId.toLong())
            },
            RANOBE(Res.string.text_ranobe) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
                override fun navigateTo(contentId: String) = Screen.Manga(contentId)
            },
            IMAGES(Res.string.text_pictures) {
                override val viewType = ContentViewType.STAGGERED_GRID_ITEM_IMAGES
                override fun navigateTo(contentId: String) = Screen.News // заглушка
            },
            CLUBS(Res.string.text_clubs) {
                override val viewType = ContentViewType.LIST_ITEM
                override fun navigateTo(contentId: String) = Screen.Club(contentId.toLong())
            };

            abstract val viewType: ContentViewType
            abstract fun navigateTo(contentId: String): Screen

            companion object {
                val items = entries.dropLast(1)
            }
        }
    }

    sealed interface User : BaseDialogState {
        data object Logout : User
        data object Settings : User
        data object ToggleFriend : User
        data object DialogAll : User
        data class DialogUser(val userId: Long) : User

        enum class Menu(val title: StringResource) : User {
            FRIENDS(Res.string.text_friends) {
                override val viewType = ContentViewType.GRID_ITEM_SMALL
            },
            FAVOURITE(Res.string.text_favourite) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
            },
            CLUBS(Res.string.text_clubs) {
                override val viewType = ContentViewType.LIST_ITEM
            },
            HISTORY(Res.string.text_history) {
                override val viewType = ContentViewType.ADAPTIVE_ITEM
            };

            abstract val viewType: ContentViewType
        }
    }
}
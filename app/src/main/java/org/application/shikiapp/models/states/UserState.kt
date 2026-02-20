package org.application.shikiapp.models.states

import org.application.shikiapp.utils.enums.UserMenu

data class UserState(
    val menu: UserMenu? = null,
    val isFriend: Boolean = false,
    val showDeleteUserDialog: Boolean = false,
    val unreadMessages: UnreadMessages = UnreadMessages(),
    val dialogState: UserDialogState? = null
) {
    data class UnreadMessages(
        val messages: Int = 0,
        val news: Int = 0,
        val notifications: Int = 0
    ) {
        val total: Int
            get() = messages + news + notifications
    }
}

sealed interface UserDialogState {
    data object Logout : UserDialogState
    data object Settings : UserDialogState
    data object Comments : UserDialogState
    data object ToggleFriend : UserDialogState

    data object DialogAll : UserDialogState
    data class DialogUser(val userId: Long) : UserDialogState
}

val UserState.showDialogs: Boolean
    get() = dialogState is UserDialogState.DialogAll || dialogState is UserDialogState.DialogUser

val UserState.showDialogUser: Boolean
    get() = dialogState is UserDialogState.DialogUser

val UserState.showFriends: Boolean
    get() = menu == UserMenu.FRIENDS

val UserState.showClubs: Boolean
    get() = menu == UserMenu.CLUBS

val UserState.showFavourite: Boolean
    get() = menu == UserMenu.FAVOURITE

val UserState.showHistory: Boolean
    get() = menu == UserMenu.HISTORY
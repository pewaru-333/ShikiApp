package org.application.shikiapp.models.states

import org.application.shikiapp.utils.enums.UserMenu

data class UserState(
    val menu: UserMenu? = null,
    val isFriend: Boolean = false,
    val showSettings: Boolean = false,
    val showComments: Boolean = false,
    val showDialogs: Boolean = false,
    val showDialogToggleFriend: Boolean = false
)

val UserState.showFriends: Boolean
    get() = menu == UserMenu.FRIENDS

val UserState.showClubs: Boolean
    get() = menu == UserMenu.CLUBS

val UserState.showFavourite: Boolean
    get() = menu == UserMenu.FAVOURITE

val UserState.showHistory: Boolean
    get() = menu == UserMenu.HISTORY
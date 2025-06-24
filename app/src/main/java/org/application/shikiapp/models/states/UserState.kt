@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.material3.ExperimentalMaterial3Api
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.UserMenu

data class UserState(
    val menu: UserMenu? = null,
    val favouriteTab: FavouriteItem = FavouriteItem.ANIME,
    val isFriend: Boolean = false,
    val showComments: Boolean = false,
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
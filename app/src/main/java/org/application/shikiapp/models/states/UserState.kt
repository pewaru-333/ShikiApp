@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import org.application.shikiapp.utils.enums.FavouriteItems
import org.application.shikiapp.utils.enums.ProfileMenus

data class UserState(
    val menu: ProfileMenus = ProfileMenus.FRIENDS,
    val favouriteTab: FavouriteItems = FavouriteItems.ANIME,
    val showComments: Boolean = false,
    val showDialog: Boolean = false,
    val showSheet: Boolean = false,
    val showFavourite: Boolean = false,
    val showHistory: Boolean = false,
    val stateF: LazyListState = LazyListState(),
    val stateC: LazyListState = LazyListState(),
    val sheetState: SheetState = SheetState(false, Density(1f))
)
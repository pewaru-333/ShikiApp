package org.application.shikiapp.events

import org.application.shikiapp.utils.enums.FavouriteItems
import org.application.shikiapp.utils.enums.ProfileMenus

sealed interface UserDetailEvent : ContentDetailEvent {
    data object ShowComments : UserDetailEvent
    data object ShowFavourite : UserDetailEvent
    data object ShowSheet : UserDetailEvent
    data object ShowHistory : UserDetailEvent

    data class PickMenu(val menu: ProfileMenus = ProfileMenus.FRIENDS) : UserDetailEvent
    data class PickFavouriteTab(val tab: FavouriteItems) : UserDetailEvent
}
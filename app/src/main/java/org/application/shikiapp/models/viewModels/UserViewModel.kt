@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.UserDetailEvent
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.network.Response
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.UserFriendsPaging
import org.application.shikiapp.network.paging.UserHistoryPaging
import org.application.shikiapp.utils.navigation.Screen

open class UserViewModel(private val saved: SavedStateHandle) : ContentDetailViewModel<User, UserState, UserDetailEvent>() {
    open val userId: Long
        get() = saved.toRoute<Screen.User>().id

    val friends = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { UserFriendsPaging(userId) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    val history = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { UserHistoryPaging(userId) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    override fun initState() = UserState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val user = NetworkClient.user.getUser(userId)
                val clubs = NetworkClient.user.getClubs(userId)
                val comments = getComments(user.id, "User")
                val favourites = NetworkClient.user.getFavourites(userId)

                emit(Response.Success(User(user, clubs, comments, friends, history, favourites)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: UserDetailEvent) {
        when (event) {
            is UserDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            is UserDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            is UserDetailEvent.ShowFavourite -> updateState {
                it.copy(
                    showFavourite = !it.showFavourite,
                    showSheet = !it.showSheet
                )
            }

            is UserDetailEvent.ShowHistory -> updateState {
                it.copy(
                    showHistory = !it.showHistory,
                    showSheet = !it.showSheet
                )
            }

            is UserDetailEvent.PickMenu -> updateState {
                it.copy(
                    menu = event.menu,
                    showDialog = !it.showDialog
                )
            }

            is UserDetailEvent.PickFavouriteTab -> updateState { it.copy(favouriteTab = event.tab) }
        }
    }
}
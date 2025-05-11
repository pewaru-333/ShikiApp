@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.UserDetailEvent
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.navigation.Screen

open class UserViewModel(private val saved: SavedStateHandle) : ContentDetailViewModel<User, UserState, UserDetailEvent>() {
    open val userId: Long
        get() = saved.toRoute<Screen.User>().id

    protected val friends = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            CommonPaging<UserBasic>(UserBasic::id) { page, params ->
                Network.user.getFriends(userId, page, params.loadSize)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 5 }

    protected val history = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            CommonPaging<History>(History::id) { page, params ->
                Network.user.getHistory(userId, page, params.loadSize)
                    .map(org.application.shikiapp.models.data.History::mapper)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 5 }

    protected val user: Deferred<org.application.shikiapp.models.data.User>
        get() = asyncLoad { Network.user.getUser(userId) }

    protected val clubs: Deferred<List<ClubBasic>>
        get() = asyncLoad { Network.user.getClubs(userId) }

    protected val favourites: Deferred<Favourites>
        get() = asyncLoad { Network.user.getFavourites(userId) }

    protected val comments: Flow<PagingData<Comment>>
        get() = getComments(userId, "User")

    override fun initState() = UserState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                emit(
                    Response.Success(
                        User(
                            user = user.await(),
                            clubs = clubs.await(),
                            comments = comments,
                            friends = friends,
                            history = history,
                            favourites = favourites.await()
                        )
                    )
                )
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
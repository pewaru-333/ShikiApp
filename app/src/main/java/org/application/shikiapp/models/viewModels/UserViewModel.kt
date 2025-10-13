package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.models.ui.mappers.toBasicContentMap
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.navigation.Screen

open class UserViewModel(private val saved: SavedStateHandle) : ContentDetailViewModel<User, UserState>() {
    open val userId: Long
        get() = saved.toRoute<Screen.User>().id

    protected val friends = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(UserBasic::id) { page, params ->
                Network.user.getFriends(userId, page, params.loadSize)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 5 }

    protected val history = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(History::id) { page, params ->
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

    protected val favourites: Deferred<Map<FavouriteItem, List<BasicContent>>>
        get() = asyncLoad { Network.user.getFavourites(userId).toBasicContentMap() }

    override fun initState() = UserState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val userLoaded = user.await()

                setCommentParams(userId, "User")

                updateState {
                    it.copy(
                        isFriend = userLoaded.inFriends == true
                    )
                }

                emit(
                    Response.Success(
                        userLoaded.mapper(
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

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            is ContentDetailEvent.User -> when (event) {
                ContentDetailEvent.User.ToggleFriend -> toggleFriend()

                ContentDetailEvent.User.ShowSettings -> updateState { it.copy(showSettings = !it.showSettings) }

                ContentDetailEvent.User.ShowDialogs -> updateState { it.copy(showDialogs = !it.showDialogs) }

                ContentDetailEvent.User.ShowDialogToggleFriend -> updateState { it.copy(showDialogToggleFriend = !it.showDialogToggleFriend) }

                is ContentDetailEvent.User.PickMenu -> updateState { it.copy(menu = event.menu) }
            }

            else -> Unit
        }
    }

    fun toggleFriend() {
        updateState { it.copy(showDialogToggleFriend = false) }
        viewModelScope.launch {
            try {
                if (state.value.isFriend) Network.user.removeFriend(userId)
                else Network.user.addFriend(userId)
            } catch (_: Throwable) {

            } finally {
                loadData()
            }
        }
    }
}
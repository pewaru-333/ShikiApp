@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.R.string.blank
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.UserFriendsPaging
import org.application.shikiapp.network.paging.UserHistoryPaging
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ProfileMenus.ACHIEVEMENTS
import org.application.shikiapp.utils.ProfileMenus.CLUBS
import org.application.shikiapp.utils.ProfileMenus.FRIENDS

open class UserViewModel(saved: SavedStateHandle?) : ViewModel() {
    private var userId = saved?.toRoute<org.application.shikiapp.utils.User>()?.id ?: Preferences.getUserId()

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    val friends = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { UserFriendsPaging(userId) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    val history = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { UserHistoryPaging(userId) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    init {
        if (Preferences.getUserId() != userId) viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val user = NetworkClient.user.getUser(userId)
                val clubs = NetworkClient.user.getClubs(userId)
                val comments = Comments.getComments(user.id, viewModelScope)
                val favourites = NetworkClient.user.getFavourites(userId)

                _response.emit(Response.Success(user, clubs, comments, favourites))
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun updateOnLogin(id: Long) {
        userId = id
    }

    fun close() = _state.update { it.copy(showDialog = false) }

    fun setMenu(menu: Int) = _state.update { it.copy(menu = menu, showDialog = true) }
    fun setTab(tab: Int) = _state.update { it.copy(tab = tab) }

    fun showSheet() = _state.update { it.copy(showSheet = true) }
    fun hideSheet() = _state.update { it.copy(showSheet = false) }

    fun showFavourite() = _state.update { it.copy(showFavourite = true) }
    fun hideFavourite() = _state.update { it.copy(showFavourite = false) }

    fun showHistory() = _state.update { it.copy(showHistory = true) }
    fun hideHistory() = _state.update { it.copy(showHistory = false) }

    fun getTitle() = when (_state.value.menu) {
        0 -> FRIENDS.title
        1 -> CLUBS.title
        2 -> ACHIEVEMENTS.title
        else -> blank
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val user: User,
            val clubs: List<ClubBasic>,
            val comments: Flow<PagingData<Comment>>,
            val favourites: Favourites
        ) : Response
    }
}

data class UserState(
    val menu: Int = 0,
    val tab: Int = 0,
    val showDialog: Boolean = false,
    val showSheet: Boolean = false,
    val showFavourite: Boolean = false,
    val showHistory: Boolean = false,
    val stateF: LazyListState = LazyListState(),
    val stateC: LazyListState = LazyListState(),
    val sheetState: SheetState = SheetState(false, Density(1f))
)
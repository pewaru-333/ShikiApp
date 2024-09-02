@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.R.string.blank
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.network.paging.UserFriendsPaging
import org.application.shikiapp.network.paging.UserHistoryPaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ProfileMenus.Achievements
import org.application.shikiapp.utils.ProfileMenus.Clubs
import org.application.shikiapp.utils.ProfileMenus.Friends
import org.application.shikiapp.utils.TokenManager

class ProfileViewModel : ViewModel() {
    private val _login = MutableStateFlow<LoginState>(LoginState.NotLogged)
    val login = _login.asStateFlow()

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val friends = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { UserFriendsPaging(Preferences.getUserId()) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 3 }

    val history = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { UserHistoryPaging(Preferences.getUserId()) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    init {
        if (Preferences.isTokenExists()) setProfile()
    }

    fun login(code: String) {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                TokenManager.getToken(code)
                val user = TokenManager.getUser()
                val clubs = NetworkClient.user.getClubs(user.id)
                val favourites = NetworkClient.user.getFavourites(user.id)

                _login.emit(LoginState.Logged(user, clubs, favourites))
            } catch (e: Throwable) {
                _login.emit(LoginState.NotLogged)
            }
        }
    }

    private fun setProfile() {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                val user = NetworkClient.user.getUser(Preferences.getUserId())
                val clubs = NetworkClient.user.getClubs(user.id)
                val favourites = NetworkClient.user.getFavourites(user.id)

                _login.emit(LoginState.Logged(user, clubs, favourites))
            } catch (e: Throwable) {
                _login.emit(LoginState.NotLogged)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                NetworkClient.profile.signOut()
                Preferences.saveToken(Token(BLANK, BLANK, 0L, BLANK, BLANK, 0L))
                Preferences.setUserId(0L)

                _login.emit(LoginState.NotLogged)
            } catch (e: Throwable) {
                _login.emit(LoginState.Logging)
            }
        }
    }

    fun setMenu(menu: Int) {
        viewModelScope.launch { _state.update { it.copy(menu = menu, showDialog = true) } }
    }

    fun setTab(tab: Int) {
        viewModelScope.launch { _state.update { it.copy(tab = tab) } }
    }

    fun close() {
        viewModelScope.launch { _state.update { it.copy(showDialog = false) } }
    }

    fun showSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = true) } }
    }

    fun hideSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = false) } }
    }

    fun showFavourite() {
        viewModelScope.launch { _state.update { it.copy(showFavourite = true) } }
    }

    fun hideFavourite() {
        viewModelScope.launch { _state.update { it.copy(showFavourite = false) } }
    }

    fun showHistory() {
        viewModelScope.launch { _state.update { it.copy(showHistory = true) } }
    }

    fun hideHistory() {
        viewModelScope.launch { _state.update { it.copy(showHistory = false) } }
    }

    fun getTitle() = when (_state.value.menu) {
        0 -> Friends.title
        1 -> Clubs.title
        2 -> Achievements.title
        else -> blank
    }

    sealed interface LoginState {
        data object NotLogged : LoginState
        data object Logging : LoginState
        data class Logged(
            val user: User,
            val clubs: List<Club>,
            val favourites: Favourites
        ) : LoginState
    }
}

data class ProfileState(
    val menu: Int = 0,
    val tab: Int = 0,
    val showDialog: Boolean = false,
    val showSheet: Boolean = false,
    val showFavourite: Boolean = false,
    val showHistory: Boolean = false,
    val status: String = BLANK,
    val stateF: LazyListState = LazyListState(),
    val stateC: LazyListState = LazyListState(),
    val bottomState: SheetState = SheetState(false, Density(1f))
)
package org.application.shikiapp.models.views

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.TokenManager
import java.net.UnknownHostException

class ProfileViewModel : UserViewModel(null) {
    private val _login = MutableStateFlow<LoginState>(LoginState.NotLogged)
    val login = _login.asStateFlow()
        .onStart { if (Preferences.isTokenExists()) getProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), LoginState.NotLogged)

    fun getProfile() {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                val user = NetworkClient.user.getUser(Preferences.getUserId())
                val clubs = NetworkClient.user.getClubs(user.id)
                val comments = Comments.getComments(user.id, viewModelScope)
                val favourites = NetworkClient.user.getFavourites(user.id)

                _login.emit(LoginState.Logged(user, clubs, comments, favourites))
            } catch (e: Throwable) {
                e.printStackTrace()
                when (e) {
                    is UnknownHostException -> _login.emit(LoginState.NoNetwork)
                    else -> _login.emit(LoginState.NotLogged)
                }
            }
        }
    }

    fun login(code: String) {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                TokenManager.getToken(code)
                val user = TokenManager.getUser()
                val clubs = NetworkClient.user.getClubs(user.id)
                val comments = Comments.getComments(user.id, viewModelScope)
                val favourites = NetworkClient.user.getFavourites(user.id)

                _login.emit(LoginState.Logged(user, clubs, comments, favourites))
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

    sealed interface LoginState {
        data object NoNetwork : LoginState
        data object NotLogged : LoginState
        data object Logging : LoginState
        data class Logged(val user: User, val clubs: List<Club>, val comments: Flow<PagingData<Comment>>,
                          val favourites: Favourites) :
            LoginState
    }
}
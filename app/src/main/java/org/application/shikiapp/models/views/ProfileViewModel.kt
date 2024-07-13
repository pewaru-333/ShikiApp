package org.application.shikiapp.models.views

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
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.network.paging.UserFriendsPaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
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

    init {
        if (Preferences.tokenExists()) setProfile()
    }

    fun login(code: String) {
        viewModelScope.launch {
            _login.emit(LoginState.Logging)

            try {
                TokenManager.getToken(code)
                val user = TokenManager.getUser()
                val clubs = NetworkClient.user.getClubs(user.id)

                _login.emit(LoginState.Logged(user, clubs))
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

                _login.emit(LoginState.Logged(user, clubs))
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
        viewModelScope.launch { _state.update { it.copy(menu = menu, show = true) } }
    }

    fun close() {
        viewModelScope.launch { _state.update { it.copy(show = false) } }
    }

    fun getTitle(): String = when (_state.value.menu) {
        0 -> "Друзья"
        1 -> "Клубы"
        2 -> "Достижения"
        else -> BLANK
    }
}

data class ProfileState(
    val menu: Int = 0,
    val status: String = BLANK,
    val show: Boolean = false
)

enum class ProfileMenus(val title: String) {
    Friends("Друзья"), Clubs("Клубы"), Achievements("Достижения")
}

sealed interface LoginState {
    data class Logged(val user: User, val clubs: List<Club>) : LoginState
    data object Logging : LoginState
    data object NotLogged : LoginState
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.network.LoginResponse
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.TokenManager
import org.application.shikiapp.utils.navigation.Screen.Login
import java.net.UnknownHostException

class ProfileViewModel(saved: SavedStateHandle) : UserViewModel(saved) {
    val args = saved.toRoute<Login>()

    override val userId: Long
        get() = Preferences.getUserId()

    override fun loadData() {
        viewModelScope.launch { emit(LoginResponse.NotLogged) }

        if (Preferences.isTokenExists()) getProfile()

        args.code?.let(::login)
    }

    private fun getProfile() {
        viewModelScope.launch {
            emit(LoginResponse.Logging)

            try {
                val user = NetworkClient.user.getUser(Preferences.getUserId())
                val clubs = NetworkClient.user.getClubs(user.id)
                val comments = getComments(user.id)
                val favourites = NetworkClient.user.getFavourites(user.id)

                emit(LoginResponse.Logged(User(user, clubs, comments, friends, history, favourites)))
            } catch (e: Throwable) {
                when (e) {
                    is UnknownHostException -> emit(LoginResponse.NetworkError)
                    else -> emit(LoginResponse.NotLogged)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            emit(LoginResponse.Logging)

            try {
                NetworkClient.profile.signOut()
                Preferences.saveToken(Token(BLANK, BLANK, 0L, BLANK, BLANK, 0L))
                Preferences.setUserId(0L)

                emit(LoginResponse.NotLogged)
            } catch (e: Throwable) {
                Preferences.saveToken(Token(BLANK, BLANK, 0L, BLANK, BLANK, 0L))
                Preferences.setUserId(0L)

                emit(LoginResponse.NotLogged)
            }
        }
    }

    private fun login(code: String) {
        if (response.value == LoginResponse.Logging)
            return

        viewModelScope.launch(Dispatchers.IO) {
            emit(LoginResponse.Logging)

            try {
                TokenManager.getToken(code)
                val user = TokenManager.getUser()
                val clubs = NetworkClient.user.getClubs(user.id)
                val comments = getComments(user.id)
                val favourites = NetworkClient.user.getFavourites(user.id)

                emit(LoginResponse.Logged(User(user, clubs, comments, friends, history, favourites)))
            } catch (e: Throwable) {
                emit(LoginResponse.NotLogged)
            }
        }
    }
}
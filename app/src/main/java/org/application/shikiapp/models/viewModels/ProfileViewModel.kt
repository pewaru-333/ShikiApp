package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.LoginResponse
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.navigation.Screen.Login
import java.net.UnknownHostException

class ProfileViewModel(saved: SavedStateHandle) : UserViewModel(saved) {
    private val mutex = Mutex()
    val args = saved.toRoute<Login>()

    override val userId: Long
        get() = Preferences.userId

    override fun loadData() {
        when {
            Preferences.token != null -> getProfile()
            args.code != null -> login(args.code)
            else -> viewModelScope.launch { emit(LoginResponse.NotLogged) }
        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            if (response.value !is LoginResponse.Logged) {
                emit(LoginResponse.Logging)
            }

            try {
                emit(
                    LoginResponse.Logged(
                        user.await().mapper(
                            clubs = clubs.await(),
                            comments = comments,
                            friends = friends,
                            history = history,
                            favourites = favourites.await()
                        )
                    )
                )
            } catch (e: Throwable) {
                when (e) {
                    is UnknownHostException -> emit(LoginResponse.NetworkError)
                    else -> emit(LoginResponse.NotLogged)
                }
            }
        }
    }

    private fun login(code: String) {
        viewModelScope.launch {
            emit(LoginResponse.Logging)

            try {
                mutex.withLock(this@ProfileViewModel) {
                    try {
                        val token = Network.profile.getToken(code = code)
                        Preferences.saveToken(token)

                        val whoAmI = Network.profile.whoAmI()
                        Preferences.setUserId(whoAmI.id)

                        emit(
                            LoginResponse.Logged(
                                user.await().mapper(
                                    clubs = clubs.await(),
                                    comments = comments,
                                    friends = friends,
                                    history = history,
                                    favourites = favourites.await()
                                )
                            )
                        )
                    } catch (_: Throwable) {
                        Preferences.saveToken(Token.empty)
                        Preferences.setUserId(0L)

                        emit(LoginResponse.NotLogged)
                    }
                }
            } catch (_: IllegalStateException) {
                // Already logging
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            emit(LoginResponse.Logging)

            try {
                val request = Network.profile.signOut()

                if (request.status == HttpStatusCode.OK) {
                    Preferences.saveToken(Token.empty)
                    Preferences.setUserId(0L)

                    emit(LoginResponse.NotLogged)
                } else {
                    emit(LoginResponse.NetworkError)
                }
            } catch (_: Throwable) {
                emit(LoginResponse.NetworkError)
            }
        }
    }
}
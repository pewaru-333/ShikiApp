package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.auth.clearAuthTokens
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.Token
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.LoginResponse
import org.application.shikiapp.shared.utils.navigation.Screen.Login
import shikiapp.composeapp.generated.resources.*

class ProfileViewModel(private val saved: SavedStateHandle) : UserViewModel(saved) {
    private var loadJob: Job? = null
    val args = saved.toRoute<Login>()

    override val userId: Long
        get() = Preferences.userId.value

    override fun loadData() {
        if (loadJob?.isActive == true) return

        val handled = saved.get<Boolean>("loginHandled") == true
        val code = if (handled) null else args.code
        val error = if (handled) null else args.error

        if (code != null || error != null) {
            saved["loginHandled"] = true
        }

        if (error != null) {
            tryEmit(
                LoginResponse.Error(
                    message = if (error == "access_denied") Res.string.text_login_cancelled else Res.string.text_error_login,
                    canRetry = false
                )
            )
            return
        }

        if (code == null && Preferences.token == null) {
            if (_response.value !is LoginResponse.Error) {
                tryEmit(LoginResponse.NotLogged)
            }
            return
        }

        loadJob = viewModelScope.launch {
            if (code != null || _response.value !is LoginResponse.Logged) {
                emit(LoginResponse.Logging)
            }

            if (code != null && !login(code)) return@launch

            try {
                getProfile()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    clearSession()
                    emit(LoginResponse.Error(Res.string.text_login_expired, false))
                } else {
                    emit(LoginResponse.Error(Res.string.text_error_profile, true))
                }
            } catch (_: Exception) {
                emit(LoginResponse.Error(Res.string.text_error_profile, true))
            }
        }
    }

    private suspend fun getProfile() {
        val id = Network.profile.whoAmI().id
        currentCoroutineContext().ensureActive()

        if (id <= 0L) {
            emit(LoginResponse.Error(Res.string.text_error_profile, true))
            return
        }

        if (userId != id) {
            resetUserData()
        }
        Preferences.userId.value = id
        setUserParams(id)

        val profile = loadUser(id)

        currentCoroutineContext().ensureActive()
        emit(LoginResponse.Logged(profile))
        mailManager.getUnreadMessages()
    }

    private suspend fun login(code: String): Boolean {
        if (code.isBlank()) {
            emit(LoginResponse.Error(Res.string.text_error_login, false))
            return false
        }

        return try {
            val token = Network.profile.getToken(code)
            currentCoroutineContext().ensureActive()

            Preferences.saveToken(token)
            Preferences.userId.value = 0L
            Network.client.clearAuthTokens()
            resetUserData()
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            emit(LoginResponse.Error(Res.string.text_error_login, false))
            false
        }
    }

    private fun clearSession() {
        Preferences.saveToken(Token.empty)
        Preferences.userId.value = 0L
        Network.client.clearAuthTokens()
        resetUserData()
    }

    fun signOut() {
        loadJob?.cancel()
        loadJob = null

        val token = Preferences.token
        clearSession()
        tryEmit(LoginResponse.NotLogged)

        if (token != null) {
            viewModelScope.launch {
                try {
                    Network.profile.signOut(token.accessToken)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {

                }
            }
        }
    }

    fun onShowSignOut() = updateState { it.copy(dialogState = BaseDialogState.User.Logout) }
}
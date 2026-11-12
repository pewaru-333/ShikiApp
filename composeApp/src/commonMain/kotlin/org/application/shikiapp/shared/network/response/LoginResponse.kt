package org.application.shikiapp.shared.network.response

import org.application.shikiapp.shared.models.ui.User
import org.jetbrains.compose.resources.StringResource

sealed interface LoginResponse : Response<User, Exception> {
    data object NotLogged : LoginResponse
    data object Logging : LoginResponse
    data class Error(val message: StringResource, val canRetry: Boolean) : LoginResponse

    data class Logged(val user: User) : LoginResponse
}
package org.application.shikiapp.shared.network.response

import org.application.shikiapp.shared.models.ui.User

sealed interface LoginResponse : Response<User, Exception> {
    data object NotLogged : LoginResponse
    data object Logging : LoginResponse
    data object NetworkError : LoginResponse

    data class Logged(val user: User) : LoginResponse
}
package org.application.shikiapp.network

import org.application.shikiapp.models.ui.User

sealed interface LoginResponse : Response<User, Throwable> {
    data object NotLogged : LoginResponse
    data object Logging : LoginResponse
    data object NetworkError : LoginResponse

    data class Logged(val user: User) : LoginResponse
}
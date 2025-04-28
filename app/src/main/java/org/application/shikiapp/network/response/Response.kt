package org.application.shikiapp.network.response

sealed interface Response<out D, out E> {
    data class Success<out D>(val data: D) : Response<D, Nothing>
    data class Error<out D, out E>(val error: E) : Response<D, E>
    data object Loading : Response<Nothing, Nothing>
}
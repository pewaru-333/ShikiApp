package org.application.shikiapp.network.response

import org.application.shikiapp.models.ui.UserRate

sealed interface RatesResponse {
    data object Error : RatesResponse
    data object Loading : RatesResponse
    data object Unlogged : RatesResponse
    data object NoAccess : RatesResponse
    data class Success(val rates: List<UserRate>) : RatesResponse
}
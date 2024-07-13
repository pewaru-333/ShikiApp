package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.network.NetworkClient
import retrofit2.HttpException

class UserRatesViewModel(private val userId: Long) : ViewModel() {
    private val _response = MutableStateFlow<UserRateState>(UserRateState.Loading)
    val response = _response.asStateFlow()

    init {
        getUserRates()
    }

    fun getUserRates() {
        viewModelScope.launch {
            _response.emit(UserRateState.Loading)

            try {
                val rates = ArrayList<AnimeRate>()
                var page = 1

                while (true) {
                    val response = NetworkClient.user.getAnimeRates(userId = userId, page = page)
                    rates.addAll(response)
                    page++
                    if (response.size < 5000) break
                }

                _response.emit(UserRateState.Success(rates))
            } catch (e: HttpException) {
                if(e.code() == 403) _response.emit(UserRateState.NoAccess)
                else _response.emit(UserRateState.Error)
            }
        }
    }
}

sealed interface UserRateState {
    data object NoAccess: UserRateState
    data object Error : UserRateState
    data object Loading : UserRateState
    data class Success(val rates: List<AnimeRate>) : UserRateState
}
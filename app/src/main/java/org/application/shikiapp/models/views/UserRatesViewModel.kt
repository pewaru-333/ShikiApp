package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.network.NetworkClient
import retrofit2.HttpException

class UserRatesViewModel(private val userId: Long) : ViewModel() {
    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    val listState by mutableStateOf(LazyListState())
    var tab by mutableIntStateOf(0)

    init {
        getUserRates()
    }

    fun getUserRates() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val rates = mutableListOf<AnimeRate>()
                var page = 1

                while (true) {
                    val response = NetworkClient.user.getAnimeRates(userId = userId, page = page)
                    rates.addAll(response)
                    page++
                    if (response.size < 5000) break
                }

                _response.emit(Response.Success(rates))
            } catch (e: HttpException) {
                if (e.code() == 403) _response.emit(Response.NoAccess)
                else _response.emit(Response.Error)
            }
        }
    }

    fun reload() {
        viewModelScope.launch {
            delay(300)
            getUserRates()
        }
    }

    sealed interface Response {
        data object NoAccess : Response
        data object Error : Response
        data object Loading : Response
        data class Success(val rates: List<AnimeRate>) : Response
    }
}

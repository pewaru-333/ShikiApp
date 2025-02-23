package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.MangaRate
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.MangaRates

class MangaRatesViewModel(saved: SavedStateHandle) : ViewModel() {
    val userId = saved.toRoute<MangaRates>().id

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    val listState by mutableStateOf(LazyListState())
    var tab by mutableIntStateOf(0)

    init {
        getRates()
    }

    fun getRates() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val rates = mutableListOf<MangaRate>()
                var page = 1

                while (true) {
                    val response = NetworkClient.user.getMangaRates(id = userId, page = page)
                    rates.addAll(response)
                    page++
                    if (response.size < 5000) break
                }

                _response.emit(Response.Success(rates))
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 403) _response.emit(Response.NoAccess)
                else _response.emit(Response.Error)
            }
        }
    }

    fun reload() {
        viewModelScope.launch {
            delay(300)
            getRates()
        }
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data object NoAccess : Response
        data class Success(val rates: List<MangaRate>) : Response
    }
}
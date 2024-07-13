package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Calendar
import org.application.shikiapp.network.NetworkClient
import retrofit2.HttpException

class CalendarViewModel : ViewModel() {
    private val _state = MutableStateFlow<CalendarResponse>(CalendarResponse.Loading)
    val state = _state.asStateFlow()

    init {
        getCalendar()
    }

    fun getCalendar() {
        viewModelScope.launch {
            _state.emit(CalendarResponse.Loading)

            try {
                val calendar = NetworkClient.client.getCalendar()

                _state.emit(CalendarResponse.Success(calendar))
            } catch (e: HttpException) {
                _state.emit(CalendarResponse.Error)
            }
        }
    }
}

sealed interface CalendarResponse {
    data object Error : CalendarResponse
    data object Loading : CalendarResponse
    data class Success(val calendar: List<Calendar>) : CalendarResponse
}
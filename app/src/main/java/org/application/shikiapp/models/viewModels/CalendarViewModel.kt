package org.application.shikiapp.models.viewModels

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Calendar
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.fromISODate
import org.application.shikiapp.utils.toCalendarDate

class CalendarViewModel : ViewModel() {
    private val _state = MutableStateFlow<Response>(Response.Loading)
    val state = _state
        .onStart { getCalendar() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Response.Loading)

    fun getCalendar() = viewModelScope.launch {
        _state.emit(Response.Loading)

        try {
            val calendar = NetworkClient.content.getCalendar()
                .groupBy { fromISODate(it.nextEpisodeAt) }
                .map { AnimeSchedule(toCalendarDate(it.key), it.value) }

            _state.emit(Response.Success(calendar))
        } catch (e: Throwable) {
            _state.emit(Response.Error)
        }
    }


    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(val calendar: List<AnimeSchedule>) : Response
    }
}

@Stable
data class AnimeSchedule(
    val date: String,
    val list: List<Calendar>
)
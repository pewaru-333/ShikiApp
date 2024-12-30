package org.application.shikiapp.models.views

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Calendar
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.fromISODate
import retrofit2.HttpException
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val _state = MutableStateFlow<Response>(Response.Loading)
    val state = _state.asStateFlow()
        .onStart { getCalendar() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Response.Loading)

    fun getCalendar() = viewModelScope.launch {
        _state.emit(Response.Loading)

        try {
            val calendar = NetworkClient.client.getCalendar()
                .groupBy { fromISODate(it.nextEpisodeAt) }
                .map { AnimeSchedule(it.key, it.value) }

            _state.emit(Response.Success(calendar))
        } catch (e: HttpException) {
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
    val date: LocalDate,
    val list: List<Calendar>
)
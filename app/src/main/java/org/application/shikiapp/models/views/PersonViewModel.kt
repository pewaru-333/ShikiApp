package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.network.NetworkClient

class PersonViewModel(private val personId: Long) : ViewModel() {
    private val _response = MutableStateFlow<PersonResponse>(PersonResponse.Loading)
    val response = _response.asStateFlow()

    init {
        viewModelScope.launch {
            _response.emit(PersonResponse.Loading)

            try {
                val person = NetworkClient.client.getPerson(personId)

                _response.emit(PersonResponse.Success(person))
            } catch (e: Throwable) {
                _response.emit(PersonResponse.Error)
            }
        }
    }
}

sealed interface PersonResponse {
    data object Error : PersonResponse
    data object Loading : PersonResponse
    data class Success(val person: Person) : PersonResponse
}
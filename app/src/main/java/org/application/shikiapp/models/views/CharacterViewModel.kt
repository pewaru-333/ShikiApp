package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.CharacterQuery
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE

class CharacterViewModel(private val id: String) : ViewModel() {
    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(CharacterState())
    val state = _state.asStateFlow()

    init {
        getCharacter()
    }

    fun getCharacter() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val character = NetworkClient.client.getCharacter(id.toLong())
                val image = ApolloClient.getCharacter(id)

                _response.emit(Response.Success(character, image))
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun changeFavourite(flag: Boolean) {
        viewModelScope.launch {
            try {
                if (flag) NetworkClient.profile.deleteFavourite(LINKED_TYPE[4], id.toLong())
                else NetworkClient.profile.addFavourite(LINKED_TYPE[4], id.toLong())
                getCharacter()
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun showAnime() {
        viewModelScope.launch { _state.update { it.copy(showAnime = true) } }
    }

    fun hideAnime() {
        viewModelScope.launch { _state.update { it.copy(showAnime = false) } }
    }

    fun showSeyu() {
        viewModelScope.launch { _state.update { it.copy(showSeyu = true) } }
    }

    fun hideSeyu() {
        viewModelScope.launch { _state.update { it.copy(showSeyu = false) } }
    }

    fun showComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = true) } }
    }

    fun hideComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = false) } }
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val character: Character,
            val image: CharacterQuery.Data.Character
        ) : Response
    }
}

data class CharacterState(
    val showAnime: Boolean = false,
    val showSeyu: Boolean = false,
    val showComments: Boolean = false
)
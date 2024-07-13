package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.CharacterQuery
import org.application.shikiapp.models.data.AnimeWork
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient

class CharacterViewModel(private val characterId: String) : ViewModel() {
    private val _response = MutableStateFlow<CharacterResponse>(CharacterResponse.Loading)
    val response = _response.asStateFlow()

    init {
        viewModelScope.launch {
            _response.emit(CharacterResponse.Loading)

            try {
                val character = ApolloClient.getCharacter(characterId).first()
                val seyu = NetworkClient.client.getCharacter(characterId.toLong()).seyu
                val anime = NetworkClient.client.getCharacter(characterId.toLong()).animes

                _response.emit(CharacterResponse.Success(MapCharacter(character, anime, seyu)))
            } catch (e: Throwable) {
                e.printStackTrace()
                _response.emit(CharacterResponse.Error)
            }
        }
    }
}

data class MapCharacter(
    val character: CharacterQuery.Character,
    val anime: List<AnimeWork>,
    val seyu: List<Person>
)

sealed interface CharacterResponse {
    data object Error : CharacterResponse
    data object Loading : CharacterResponse
    data class Success(val character: MapCharacter) : CharacterResponse
}
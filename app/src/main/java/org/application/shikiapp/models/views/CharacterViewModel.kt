package org.application.shikiapp.models.views

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.CharacterQuery
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE

class CharacterViewModel(saved: SavedStateHandle) : ViewModel() {
    private val id = saved.toRoute<org.application.shikiapp.utils.Character>().id

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
                val character = NetworkClient.content.getCharacter(id.toLong())
                val image = ApolloClient.getCharacter(id)
                val comments = Comments.getComments(character.topicId, viewModelScope)

                _response.emit(Response.Success(character, image, comments))
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

    fun showAnime() = _state.update { it.copy(showAnime = true) }
    fun showManga() = _state.update { it.copy(showManga = true) }
    fun hide() = _state.update { it.copy(showAnime = false, showManga = false) }

    fun showSeyu() = _state.update { it.copy(showSeyu = true) }
    fun hideSeyu() = _state.update { it.copy(showSeyu = false) }

    fun showComments() = _state.update { it.copy(showComments = true) }
    fun hideComments() = _state.update { it.copy(showComments = false) }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val character: Character,
            val image: CharacterQuery.Data.Character,
            val comments: Flow<PagingData<Comment>>
        ) : Response
    }
}

data class CharacterState(
    val showAnime: Boolean = false,
    val showManga: Boolean = false,
    val showSeyu: Boolean = false,
    val showComments: Boolean = false
)
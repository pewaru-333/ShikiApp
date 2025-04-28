package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.CharacterDetailEvent
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class CharacterViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Character, CharacterState, CharacterDetailEvent>(){
    private val id = saved.toRoute<Screen.Character>().id

    override fun initState() = CharacterState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val character = asyncLoad { NetworkClient.content.getCharacter(id) }
                val characterLoaded = character.await()
                val image = asyncLoad { ApolloClient.getCharacter(id) }
                val comments = getComments(characterLoaded.topicId)

                emit(Response.Success(characterLoaded.mapper(image.await(), comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: CharacterDetailEvent) {
        when(event) {
            CharacterDetailEvent.ShowAnime -> updateState { it.copy(showAnime = !it.showAnime) }
            CharacterDetailEvent.ShowManga -> updateState { it.copy(showManga = !it.showManga) }
            CharacterDetailEvent.ShowSeyu -> updateState { it.copy(showSeyu = !it.showSeyu) }
            CharacterDetailEvent.HideAll -> updateState { it.copy(showAnime = false, showManga = false) }

            is ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            is CharacterDetailEvent.ToggleFavourite -> toggleFavourite(
                id = id,
                type = LinkedType.CHARACTER,
                favoured = event.favoured
            )
        }
    }
}
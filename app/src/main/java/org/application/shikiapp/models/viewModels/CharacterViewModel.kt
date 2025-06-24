package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class CharacterViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Character, CharacterState>() {
    private val id = saved.toRoute<Screen.Character>().id

    override fun initState() = CharacterState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val character = asyncLoad { Network.content.getCharacter(id) }
                val image = asyncLoad { GraphQL.getCharacter(id) }

                val characterLoaded = character.await()
                val comments = getComments(characterLoaded.topicId)

                emit(Response.Success(characterLoaded.mapper(image.await(), comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            is ContentDetailEvent.Character -> when (event) {
                ContentDetailEvent.Character.ShowAnime -> updateState { it.copy(showAnime = !it.showAnime) }
                ContentDetailEvent.Character.ShowManga -> updateState { it.copy(showManga = !it.showManga) }
                ContentDetailEvent.Character.ShowSeyu -> updateState { it.copy(showSeyu = !it.showSeyu) }
                ContentDetailEvent.Character.HideAll -> updateState {
                    it.copy(
                        showAnime = false,
                        showManga = false
                    )
                }

                is ContentDetailEvent.Character.ToggleFavourite -> toggleFavourite(
                    id = id,
                    type = LinkedType.CHARACTER,
                    favoured = event.favoured
                )
            }

            else -> Unit
        }
    }
}
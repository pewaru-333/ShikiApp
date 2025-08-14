@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.CharacterState
import org.application.shikiapp.models.ui.Character
import org.application.shikiapp.models.ui.mappers.CharacterMapper
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
                val character = async { Network.content.getCharacter(id) }
                val image = async { GraphQL.getCharacter(id) }
                val topic = async { GraphQL.getCharacterTopic(id) }

                val comments = getComments(topic.await().topic?.id?.toLong())

                emit(
                    Response.Success(
                        CharacterMapper.create(
                            character = character.await(),
                            image = image.await(),
                            comments = comments
                        )
                    )
                )
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }

            is ContentDetailEvent.Character -> when (event) {
                ContentDetailEvent.Character.ShowSeyu -> updateState { it.copy(showSeyu = !it.showSeyu) }

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
package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.CharacterState
import org.application.shikiapp.shared.models.ui.Character
import org.application.shikiapp.shared.models.ui.mappers.CharacterMapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen

class CharacterViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Character, CharacterState>() {
    override val contentId = saved.toRoute<Screen.Character>()
        .id.filter(Char::isDigit)

    override fun initState() = CharacterState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val (main, extra) = coroutineScope {
                    val character = async { Network.content.getCharacter(contentId) }
                    val extra = async { Network.characterRepository.getCharacter(contentId) }

                    Pair(character.await(), extra.await())
                }

                setCommentParams(extra.topicId?.toLong())

                emit(
                    Response.Success(
                        CharacterMapper.create(
                            character = main,
                            image = extra.poster,
                            comments = comments
                        )
                    )
                )

            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            ContentDetailEvent.Media.ShowPoster -> updateState { it.copy(showPoster = !it.showPoster) }
            ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }

            is ContentDetailEvent.Character -> when (event) {
                ContentDetailEvent.Character.ShowSeyu -> updateState { it.copy(showSeyu = !it.showSeyu) }

                is ContentDetailEvent.Character.ToggleFavourite -> with(response.value) {
                    if (this !is Response.Success) return

                    val isFavoured = data.favoured.getValue() ?: return
                    val newData = data.copy(favoured = AsyncData.Loading)


                    tryEmit(Response.Success(newData))
                    toggleFavourite(
                        id = contentId,
                        type = LinkedType.CHARACTER,
                        favoured = isFavoured
                    )
                }
            }

            else -> Unit
        }
    }
}
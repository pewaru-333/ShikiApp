package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.PersonState
import org.application.shikiapp.models.ui.Person
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class PersonViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Person, PersonState>() {
    override val contentId = saved.toRoute<Screen.Person>().id

    override fun initState() = PersonState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val person = Network.content.getPerson(contentId)
                setCommentParams(person.topicId)

                emit(Response.Success(person.mapper(comments)))
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }

            ContentDetailEvent.Media.ShowPoster -> updateState { it.copy(showPoster = !it.showPoster) }
            ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }

            ContentDetailEvent.Person.ShowWorks -> updateState { it.copy(showWorks = !it.showWorks) }

            is ContentDetailEvent.Person.ToggleFavourite -> with(response.value) {
                if (this !is Response.Success) return

                val isFavoured = data.favoured.getValue() ?: return
                val newData = data.copy(favoured = AsyncData.Loading)

                tryEmit(Response.Success(newData))
                toggleFavourite(
                    id = contentId,
                    type = LinkedType.PERSON,
                    favoured = isFavoured,
                    kind = event.kind
                )
            }

            else -> Unit
        }
    }
}
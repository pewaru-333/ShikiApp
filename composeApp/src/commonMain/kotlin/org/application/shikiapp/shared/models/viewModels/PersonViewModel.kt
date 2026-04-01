package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.PersonState
import org.application.shikiapp.shared.models.ui.Person
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen

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
                setCommentParams(person.topicId, CommentableType.PERSON)

                emit(Response.Success(person.mapper(comments)))
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        if (event is ContentDetailEvent.Person.ToggleFavourite) {
            with(response.value) {
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
        }
    }
}
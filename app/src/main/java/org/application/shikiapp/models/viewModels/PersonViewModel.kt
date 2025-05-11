@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.PersonDetailEvent
import org.application.shikiapp.models.states.PersonState
import org.application.shikiapp.models.ui.Person
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class PersonViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Person, PersonState, PersonDetailEvent>() {
    private val id = saved.toRoute<Screen.Person>().id

    override fun initState() = PersonState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val person = asyncLoad { Network.content.getPerson(id) }
                val personLoaded = person.await()
                val comments = getComments(personLoaded.topicId)

                emit(Response.Success(personLoaded.mapper(comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: PersonDetailEvent) {
        when (event) {
            is PersonDetailEvent.ShowRoles -> updateState { it.copy(showRoles = !it.showRoles) }
            is PersonDetailEvent.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }

            is PersonDetailEvent.ToggleFavourite -> toggleFavourite(
                id = id,
                type = LinkedType.PERSON,
                favoured = event.favoured,
                kind = event.kind
            )

            is ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            is ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
        }
    }
}
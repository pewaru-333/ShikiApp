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
import org.application.shikiapp.network.Response
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.navigation.Screen

class PersonViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Person, PersonState, PersonDetailEvent>() {
    private val id = saved.toRoute<Screen.Person>().id

    override fun initState() = PersonState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val person = NetworkClient.content.getPerson(id)
                val comments = getComments(person.topicId)

                emit(Response.Success(person.mapper(comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: PersonDetailEvent) {
        when (event) {
            is PersonDetailEvent.ShowRoles -> updateState { it.copy(showRoles = !it.showRoles) }
            is PersonDetailEvent.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }

            is PersonDetailEvent.ToggleFavourite -> toggleFavourite(id, LINKED_TYPE[3], event.favoured, event.kind)

            is ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            is ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
        }
    }
}
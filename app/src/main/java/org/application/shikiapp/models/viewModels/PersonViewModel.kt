@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.PersonState
import org.application.shikiapp.models.ui.Person
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class PersonViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Person, PersonState>() {
    private val id = saved.toRoute<Screen.Person>().id

    override fun initState() = PersonState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val person = Network.content.getPerson(id)
                val comments = getComments(person.topicId)

                emit(Response.Success(person.mapper(comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }

            ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }

            is ContentDetailEvent.Person.ToggleFavourite -> toggleFavourite(
                id = id,
                type = LinkedType.PERSON,
                favoured = event.favoured,
                kind = event.kind
            )

            else -> Unit
        }
    }
}
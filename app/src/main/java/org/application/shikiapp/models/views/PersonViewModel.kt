@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
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
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.LINKED_KIND
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.isPersonFavoured

class PersonViewModel(saved: SavedStateHandle) : ViewModel() {
    private val id = saved.toRoute<org.application.shikiapp.utils.Person>().id

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(PersonState())
    val state = _state.asStateFlow()

    init {
        getPerson()
    }

    fun getPerson() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val person = NetworkClient.client.getPerson(id)
                val comments = Comments.getComments(person.topicId, viewModelScope)

                _response.emit(
                    Response.Success(
                        person, comments
                    )
                )
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun changeFavourite(person: Person) {
        viewModelScope.launch {
            try {
                val kind = when {
                    person.seyu -> LINKED_KIND[1]
                    person.mangaka -> LINKED_KIND[2]
                    person.producer -> LINKED_KIND[3]
                    else -> LINKED_KIND[4]
                }

                if (isPersonFavoured(person)) NetworkClient.profile.deleteFavourite(LINKED_TYPE[3], id)
                else NetworkClient.profile.addFavourite(LINKED_TYPE[3], id, kind)
                getPerson()
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun showSheet() = _state.update { it.copy(showSheet = true) }
    fun hideSheet() = _state.update { it.copy(showSheet = false) }

    fun showComments() = _state.update { it.copy(showComments = true) }
    fun hideComments() = _state.update { it.copy(showComments = false) }

    fun showRoles() = _state.update { it.copy(showRoles = true) }
    fun hideRoles() = _state.update { it.copy(showRoles = false) }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(val person: Person, val comments: Flow<PagingData<Comment>>) : Response
    }
}

data class PersonState(
    val showSheet: Boolean = false,
    val showComments: Boolean = false,
    val showRoles: Boolean = false,
    val sheetState: SheetState = SheetState(false, Density(1f))
)
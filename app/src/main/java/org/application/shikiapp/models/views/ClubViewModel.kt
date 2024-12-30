package org.application.shikiapp.models.views

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.network.paging.ClubAnimePaging
import org.application.shikiapp.network.paging.ClubCharactersPaging
import org.application.shikiapp.network.paging.ClubImagesPaging
import org.application.shikiapp.network.paging.ClubMembersPaging
import org.application.shikiapp.network.paging.CommentsPaging
import retrofit2.HttpException

class ClubViewModel(saved: SavedStateHandle) : ViewModel() {
    private val clubId = saved.toRoute<org.application.shikiapp.utils.Club>().id

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(ClubState())
    val state = _state.asStateFlow()

    val members = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false))
    { ClubMembersPaging(clubId) }.flow.map { member ->
        val set = mutableSetOf<Long>()
        member.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    val characters = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false))
    { ClubCharactersPaging(clubId) }.flow.map { character ->
        val set = mutableSetOf<Long>()
        character.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    val anime = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false))
    { ClubAnimePaging(clubId) }.flow.map { anime ->
        val set = mutableSetOf<Long>()
        anime.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    val comments = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false))
    { CommentsPaging(_state.value.topicId) }.flow.map { comment ->
        val set = mutableSetOf<Long>()
        comment.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    val images = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false))
    { ClubImagesPaging(clubId) }.flow.map { image ->
        val set = mutableSetOf<Long>()
        image.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    init {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val club = NetworkClient.clubs.getClub(clubId)
                _state.update { it.copy(topicId = club.topicId ?: 0) }

                _response.emit(Response.Success(club))
            } catch (e: HttpException) {
                _response.emit(Response.Error)
            }
        }
    }

    fun onEvent(event: UIEvent) = when (event) {
        is UIEvent.SetMenu -> _state.update { it.copy(menu = event.menu) }
        is UIEvent.SetShow -> _state.update { it.copy(show = event.show) }
    }

    fun getTitle(): String = Menus.entries[_state.value.menu].title
}

data class ClubState(
    val menu: Int = 0,
    val show: Boolean = false,
    val topicId: Long = 0L
)

internal enum class Menus(val title: String) {
    Admins("Администрация"), Anime("Аниме"),
    Members("Участники"), Manga("Манга"),
    Characters("Персонажи"), Images("Картинки")
}

sealed interface Response {
    data object Error : Response
    data object Loading : Response
    data class Success(val club: Club) : Response
}

sealed interface UIEvent {
    data class SetMenu(val menu: Int) : UIEvent
    data class SetShow(val show: Boolean) : UIEvent
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.utils.navigation.Screen

class ClubViewModel(saved: SavedStateHandle) : ViewModel() {
    private val clubId = saved.toRoute<Screen.Club>().id

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(ClubState())
    val state = _state.asStateFlow()

    val members = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<UserBasic>(UserBasic::id) { page, params ->
                Network.clubs.getMembers(clubId, page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val characters = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<BasicInfo>(BasicInfo::id) { page, params ->
                Network.clubs.getCharacters(clubId, page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val anime = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<AnimeBasic>(AnimeBasic::id) { page, params ->
                Network.clubs.getAnime(clubId, page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val comments = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<Comment>(Comment::id) { page, params ->
                Network.topics.getComments(_state.value.topicId, "Topic", page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val images = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<ClubImages>(ClubImages::id) { page, params ->
                Network.clubs.getImages(clubId, page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    init {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val club = Network.clubs.getClub(clubId)
                _state.update { it.copy(topicId = club.topicId) }

                _response.emit(Response.Success(club))
            } catch (e: Throwable) {
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
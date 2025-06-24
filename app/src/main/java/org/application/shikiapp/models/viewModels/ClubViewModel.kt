@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.events.ClubEvent
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.states.ClubState
import org.application.shikiapp.models.ui.Club
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.models.ui.mappers.toContent
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.ClubMenu
import org.application.shikiapp.utils.navigation.Screen

class ClubViewModel(saved: SavedStateHandle) : BaseViewModel<Club, ClubState, ClubEvent>() {
    private val clubId = saved.toRoute<Screen.Club>().id

    private val _joinChannel = Channel<ResourceText>()
    val joinChannel = _joinChannel.receiveAsFlow()

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
        .map(PagingData<BasicInfo>::toContent)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val animes = Pager(
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
        .map(PagingData<AnimeBasic>::toContent)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val manga = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<MangaBasic>(MangaBasic::id) { page, params ->
                Network.clubs.getManga(clubId, page, params.loadSize)
            }
        }
    ).flow
        .map(PagingData<MangaBasic>::toContent)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val ranobe = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<MangaBasic>(MangaBasic::id) { page, params ->
                Network.clubs.getRanobe(clubId, page, params.loadSize)
            }
        }
    ).flow
        .map(PagingData<MangaBasic>::toContent)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    val clubs = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<ClubBasic>(ClubBasic::id) { page, params ->
                Network.clubs.getClubClubs(clubId, page, params.loadSize)
            }
        }
    ).flow
        .map(PagingData<ClubBasic>::toContent)
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
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    @OptIn(ExperimentalCoroutinesApi::class)
    val content = state.flatMapLatest { state ->
        when (state.menu) {
            ClubMenu.ANIME -> animes
            ClubMenu.MANGA -> manga
            ClubMenu.RANOBE -> ranobe
            ClubMenu.CHARACTERS -> characters
            else -> emptyFlow()
        }
    }

    override fun initState() = ClubState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val club = asyncLoad { Network.clubs.getClub(clubId) }
                val clubLoaded = club.await()

                val comments = getComments(clubLoaded.topicId)

                updateState {
                    it.copy(
                        isMember = clubLoaded.userRole == "member"
                    )
                }

                emit(
                    Response.Success(
                        clubLoaded.mapper(
                            images = images,
                            members = members,
                            animes = animes,
                            mangas = manga,
                            ranobe = ranobe,
                            characters = characters,
                            clubs = clubs,
                            comments = comments
                        )
                    )
                )
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ClubEvent) {
        when (event) {
            ClubEvent.ShowBottomSheet -> updateState { it.copy(showBottomSheet = !it.showBottomSheet) }

            ClubEvent.ShowClubs -> updateState {
                it.copy(
                    showBottomSheet = !it.showBottomSheet,
                    showClubs = !it.showClubs
                )
            }

            ClubEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            ClubEvent.JoinClub -> joinClub()
            ClubEvent.LeaveClub -> leaveClub()

            is ClubEvent.PickItem -> updateState { it.copy(menu = event.item) }

            is ClubEvent.ShowFullImage -> updateState {
                it.copy(
                    showFullImage = !it.showFullImage,
                    image = event.url
                )
            }
        }
    }

    private fun getComments(id: Long) = Pager(
        config = PagingConfig(
            pageSize = 15,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging<Comment>(Comment::id) { page, params ->
                Network.topics.getComments(id, "Topic", page, params.loadSize)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    private fun joinClub() {
        viewModelScope.launch {
            try {
                val response = Network.clubs.joinClub(clubId)

                if (response.status == HttpStatusCode.OK) {
                    _joinChannel.send(ResourceText.StringResource(R.string.text_successfully_joined_club))
                } else {
                    _joinChannel.send(ResourceText.StringResource(R.string.text_unsuccessfully_joined_club))
                }
            } catch (e: Throwable) {
                _joinChannel.send(ResourceText.StaticString(e.stackTraceToString()))
            } finally {
                loadData()
            }
        }
    }

    private fun leaveClub() {
        viewModelScope.launch {
            try {
                val response = Network.clubs.leaveClub(clubId)

                if (response.status == HttpStatusCode.OK) {
                    _joinChannel.send(ResourceText.StringResource(R.string.text_successfully_leave_club))
                } else {
                    _joinChannel.send(ResourceText.StringResource(R.string.text_unsuccessfully_leave_club))
                }
            } catch (e: Throwable) {
                _joinChannel.send(ResourceText.StaticString(e.stackTraceToString()))
            } finally {
                loadData()
            }
        }
    }
}
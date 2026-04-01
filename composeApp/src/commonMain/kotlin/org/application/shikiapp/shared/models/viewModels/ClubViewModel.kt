@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.BasicInfo
import org.application.shikiapp.shared.models.data.ClubBasic
import org.application.shikiapp.shared.models.data.ClubImages
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.data.UserBasic
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.ClubState
import org.application.shikiapp.shared.models.ui.Club
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.models.ui.mappers.toContent
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.navigation.Screen
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_successfully_joined_club
import shikiapp.composeapp.generated.resources.text_successfully_leave_club
import shikiapp.composeapp.generated.resources.text_unsuccessfully_joined_club
import shikiapp.composeapp.generated.resources.text_unsuccessfully_leave_club

class ClubViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Club, ClubState>() {
    override val contentId = saved.toRoute<Screen.Club>().id

    private val _joinChannel = Channel<ResourceText>()
    val joinChannel = _joinChannel.receiveAsFlow()

    val members by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(UserBasic::id) { page, params ->
                    Network.clubs.getMembers(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map { it.map(UserBasic::toContent) }
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val characters by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(BasicInfo::id) { page, params ->
                    Network.clubs.getCharacters(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map(PagingData<BasicInfo>::toContent)
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val animes by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(AnimeBasic::id) { page, params ->
                    Network.clubs.getAnime(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map(PagingData<AnimeBasic>::toContent)
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val manga by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(MangaBasic::id) { page, params ->
                    Network.clubs.getManga(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map(PagingData<MangaBasic>::toContent)
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val ranobe by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(MangaBasic::id) { page, params ->
                    Network.clubs.getRanobe(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map(PagingData<MangaBasic>::toContent)
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val clubs by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging<ClubBasic>(ClubBasic::id) { page, params ->
                    Network.clubs.getClubClubs(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map(PagingData<ClubBasic>::toContent)
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val images by lazy {
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(ClubImages::id) { page, params ->
                    Network.clubs.getImages(contentId, page, params.loadSize)
                }
            }
        ).flow
            .map { it.map(ClubImages::toContent) }
            .cachedIn(viewModelScope)
            .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
    }

    val content = state
        .map { it.dialogState }
        .distinctUntilChanged()
        .flatMapLatest { dialogState ->
            when (dialogState) {
                BaseDialogState.Club.Menu.ANIME -> animes
                BaseDialogState.Club.Menu.MANGA -> manga
                BaseDialogState.Club.Menu.RANOBE -> ranobe
                BaseDialogState.Club.Menu.CHARACTERS -> characters
                BaseDialogState.Club.Menu.MEMBERS -> members
                BaseDialogState.Club.Menu.CLUBS -> clubs
                BaseDialogState.Club.Menu.IMAGES -> images
                else -> emptyFlow()
            }
        }

    override fun initState() = ClubState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val club = async { Network.clubs.getClub(contentId) }
                val clubLoaded = club.await()

                setCommentParams(clubLoaded.topicId, CommentableType.CLUB)

                updateState {
                    it.copy(
                        isMember = clubLoaded.userRole == "member" || clubLoaded.userRole == "admin"
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
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            ContentDetailEvent.Club.JoinClub -> joinClub()
            ContentDetailEvent.Club.LeaveClub -> leaveClub()

            is ContentDetailEvent.ToggleDialog if event.dialogState is BaseDialogState.Club.Image ->
                updateState {
                    it.copy(image = event.dialogState.url)
                }

            else -> Unit
        }
    }

    private fun joinClub() {
        viewModelScope.launch {
            try {
                val response = Network.clubs.joinClub(contentId)

                if (response.status == HttpStatusCode.OK) {
                    _joinChannel.send(ResourceText.StringResource(Res.string.text_successfully_joined_club))
                } else {
                    _joinChannel.send(ResourceText.StringResource(Res.string.text_unsuccessfully_joined_club))
                }
            } catch (e: Exception) {
                _joinChannel.send(ResourceText.StaticString(e.stackTraceToString()))
            } finally {
                loadData()
            }
        }
    }

    private fun leaveClub() {
        viewModelScope.launch {
            try {
                val response = Network.clubs.leaveClub(contentId)

                if (response.status == HttpStatusCode.OK) {
                    _joinChannel.send(ResourceText.StringResource(Res.string.text_successfully_leave_club))
                } else {
                    _joinChannel.send(ResourceText.StringResource(Res.string.text_unsuccessfully_leave_club))
                }
            } catch (e: Exception) {
                _joinChannel.send(ResourceText.StaticString(e.stackTraceToString()))
            } finally {
                loadData()
            }
        }
    }
}
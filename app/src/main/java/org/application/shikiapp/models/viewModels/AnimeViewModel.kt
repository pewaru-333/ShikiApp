package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.mappers.AnimeMapper
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class AnimeViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Anime, AnimeState>() {
    private val animeId = saved.toRoute<Screen.Anime>().id

    override fun initState() = AnimeState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
               emit(Response.Loading)
            }

            try {
                coroutineScope {
                    val topic = async { GraphQL.getAnimeTopic(animeId) }
                    val main = async { GraphQL.getAnimeMain(animeId) }
                    val extra = async { GraphQL.getAnimeExtra(animeId) }

                    val franchise = async { Network.anime.getFranchise(animeId) }
                    val similar = async { Network.anime.getSimilar(animeId) }
                    val favoured = async { Network.anime.getAnime(animeId).favoured }

                    setCommentParams(topic.await().topic?.id?.toLong())

                    emit(
                        Response.Success(
                            AnimeMapper.create(
                                main = main.await(),
                                extra = extra.await(),
                                franchise = franchise.await(),
                                similar = similar.await(),
                                comments = comments,
                                favoured = favoured.await()
                            )
                        )
                    )
                }
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

            is ContentDetailEvent.Media -> when (event) {
                ContentDetailEvent.Media.ChangeRate -> with(response.value) {
                    if (this !is Response.Success) return

                    val newData = data.copy(userRate = AsyncData.Loading)

                    updateState {
                        it.copy(
                            showRate = !it.showRate,
                            showSheet = !it.showSheet
                        )
                    }

                    viewModelScope.launch {
                        emit(Response.Success(newData))
                        loadData()
                    }
                }

                ContentDetailEvent.Media.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
                ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
                ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
                ContentDetailEvent.Media.ShowSimilar -> updateState { it.copy(showSimilar = !it.showSimilar) }
                ContentDetailEvent.Media.ShowStats -> updateState { it.copy(showStats = !it.showStats) }
                ContentDetailEvent.Media.ShowFansubbers -> updateState { it.copy(showFansubbers = !it.showFansubbers) }
                ContentDetailEvent.Media.ShowFandubbers -> updateState { it.copy(showFandubbers = !it.showFandubbers) }

                ContentDetailEvent.Media.ShowLinks -> updateState {
                    it.copy(
                        showSheet = !it.showSheet,
                        showLinks = !it.showLinks
                    )
                }

                ContentDetailEvent.Media.ShowRate -> updateState {
                    it.copy(
                        showRate = !it.showRate,
                        showSheet = !it.showSheet
                    )
                }

                is ContentDetailEvent.Media.ShowImage -> updateState {
                    it.copy(
                        showScreenshot = !it.showScreenshot,
                        screenshot = event.index
                    )
                }

                is ContentDetailEvent.Media.SetImage -> updateState {
                    it.copy(
                        screenshot = event.index
                    )
                }
            }

            is ContentDetailEvent.Media.Anime -> when (event) {
                ContentDetailEvent.Media.Anime.ShowScreenshots -> updateState {
                    it.copy(showScreenshots = !it.showScreenshots)
                }

                ContentDetailEvent.Media.Anime.ShowVideo -> updateState {
                    it.copy(showVideo = !it.showVideo)
                }

                is ContentDetailEvent.Media.Anime.ToggleFavourite -> with(response.value) {
                    if (this !is Response.Success) return

                    val isFavoured = data.favoured.getValue() ?: return
                    val newData = data.copy(favoured = AsyncData.Loading)

                    viewModelScope.launch {
                        emit(Response.Success(newData))
                    }

                    toggleFavourite(
                        id = animeId,
                        type = LinkedType.ANIME,
                        favoured = isFavoured
                    )
                }
            }

            else -> Unit
        }
    }
}
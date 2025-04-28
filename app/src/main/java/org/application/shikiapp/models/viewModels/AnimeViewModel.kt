@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.AnimeDetailEvent
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class AnimeViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Anime, AnimeState, AnimeDetailEvent>() {
    private val animeId = saved.toRoute<Screen.Anime>().id

    override fun initState() = AnimeState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val anime = asyncLoad { ApolloClient.getAnime(animeId) }
                val animeLoaded = anime.await()
                val similar = asyncLoad { NetworkClient.anime.getSimilar(animeId) }
                val links = asyncLoad { NetworkClient.anime.getLinks(animeId) }
                val stats = asyncLoad { ApolloClient.getAnimeStats(animeId) }
                val favoured = NetworkClient.anime.getAnime(animeId).favoured
                val comments = getComments(animeLoaded.topic?.id?.toLong())

                emit(
                    Response.Success(
                        animeLoaded.mapper(
                            similar = similar.await(),
                            links = links.await(),
                            stats = stats.await(),
                            comments = comments,
                            favoured = favoured
                        )
                    )
                )
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: AnimeDetailEvent) {
        when (event) {
            AnimeDetailEvent.Reload -> viewModelScope.launch {
                updateState {
                    it.copy(
                        showRate = !it.showRate,
                        showSheet = !it.showSheet
                    )
                }
                loadData()
            }

            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            ContentDetailEvent.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
            ContentDetailEvent.ShowSimilar -> updateState {
                it.copy(
                    showSimilar = !it.showSimilar,
                    showSheet = !it.showSheet
                )
            }

            AnimeDetailEvent.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
            AnimeDetailEvent.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
            AnimeDetailEvent.ShowScreenshots -> updateState { it.copy(showScreenshots = !it.showScreenshots) }
            AnimeDetailEvent.ShowVideo -> updateState { it.copy(showVideo = !it.showVideo) }
            AnimeDetailEvent.ShowRate, ContentDetailEvent.ShowRate -> updateState {
                it.copy(
                    showRate = !it.showRate,
                    showSheet = !it.showSheet
                )
            }


            ContentDetailEvent.ShowStats -> updateState {
                it.copy(
                    showStats = !it.showStats,
                    showSheet = !it.showSheet
                )
            }

            ContentDetailEvent.ShowLinks -> updateState {
                it.copy(
                    showSheet = !it.showSheet,
                    showLinks = !it.showLinks
                )
            }

            is AnimeDetailEvent.ShowScreenshot -> updateState {
                it.copy(
                    showScreenshot = !it.showScreenshot,
                    screenshot = event.index
                )
            }

            is AnimeDetailEvent.SetScreenshot -> updateState { it.copy(screenshot = event.index) }

            is AnimeDetailEvent.ToggleFavourite -> toggleFavourite(
                id = animeId,
                type = LinkedType.ANIME,
                favoured = event.favoured
            )
        }
    }
}
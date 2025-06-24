@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class AnimeViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Anime, AnimeState>() {
    private val animeId = saved.toRoute<Screen.Anime>().id

    override fun initState() = AnimeState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val anime = asyncLoad { GraphQL.getAnime(animeId) }
                val similar = asyncLoad { Network.anime.getSimilar(animeId) }
                val links = asyncLoad { Network.anime.getLinks(animeId) }
                val stats = asyncLoad { GraphQL.getAnimeStats(animeId) }
                val favoured = Network.anime.getAnime(animeId).favoured

                val animeLoaded = anime.await()
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

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }

            is ContentDetailEvent.Media -> when (event) {
                ContentDetailEvent.Media.Reload -> viewModelScope.launch {
                    updateState {
                        it.copy(
                            showRate = !it.showRate,
                            showSheet = !it.showSheet
                        )
                    }
                    loadData()
                }

                ContentDetailEvent.Media.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
                ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
                ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
                ContentDetailEvent.Media.ShowSimilar -> updateState {
                    it.copy(
                        showSimilar = !it.showSimilar,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowStats -> updateState {
                    it.copy(
                        showStats = !it.showStats,
                        showSheet = !it.showSheet
                    )
                }

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

                is ContentDetailEvent.Media.Anime.ToggleFavourite -> toggleFavourite(
                    id = animeId,
                    type = LinkedType.ANIME,
                    favoured = event.favoured
                )
            }

            else -> Unit
        }
    }
}
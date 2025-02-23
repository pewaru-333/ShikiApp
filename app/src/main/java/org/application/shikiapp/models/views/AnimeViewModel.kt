@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.AnimeQuery.Data.Anime
import org.application.AnimeStatsQuery
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE

class AnimeViewModel(saved: SavedStateHandle) : ViewModel() {
    private val animeId = saved.toRoute<org.application.shikiapp.utils.Anime>().id

    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()
        .onStart { getAnime() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    private val _state = MutableStateFlow(AnimeState())
    val state = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AnimeState())


    fun getAnime() = viewModelScope.launch {
        _response.emit(Response.Loading)

        try {
            val anime = ApolloClient.getAnime(animeId)
            val similar = NetworkClient.anime.getSimilar(animeId.toLong())
            val links = NetworkClient.anime.getLinks(animeId.toLong())
            val comments = Comments.getComments(anime.topic?.id?.toLong(), viewModelScope)
            val stats = ApolloClient.getAnimeStats(animeId)
            val favoured = NetworkClient.anime.getAnime(animeId.toLong()).favoured

            _response.emit(Response.Success(anime, similar, links, comments, stats, favoured))
        } catch (e: Exception) {
            _response.emit(Response.Error)
        }
    }


    fun changeFavourite(flag: Boolean) {
        viewModelScope.launch {
            try {
                if (flag) NetworkClient.profile.deleteFavourite(LINKED_TYPE[0], animeId.toLong())
                else NetworkClient.profile.addFavourite(LINKED_TYPE[0], animeId.toLong())
                getAnime()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun reload() = viewModelScope.launch { showRate(); delay(300); getAnime() }

    fun showComments() = _state.update { it.copy(showComments = !it.showComments) }
    fun showSheet() = _state.update { it.copy(showSheet = !it.showSheet) }
    fun showRelated() = _state.update { it.copy(showRelated = !it.showRelated) }
    fun showCharacters() = _state.update { it.copy(showCharacters = !it.showCharacters) }
    fun showAuthors() = _state.update { it.copy(showAuthors = !it.showAuthors) }
    fun showScreenshots() = _state.update { it.copy(showScreenshots = !it.showScreenshots) }

    fun showScreenshot(index: Int = 0) = _state.update { it.copy(showScreenshot = !it.showScreenshot, screenshot = index) }
    fun setScreenshot(index: Int) = _state.update { it.copy(screenshot = index) }

    fun showVideo() = _state.update { it.copy(showVideo = !it.showVideo) }
    fun showRate() = _state.update { it.copy(showRate = !it.showRate, showSheet = !it.showSheet) }
    fun showSimilar() = _state.update { it.copy(showSimilar = !it.showSimilar, showSheet = !it.showSheet) }
    fun showStats() = _state.update { it.copy(showStats = !it.showStats, showSheet = !it.showSheet) }
    fun showLinks() = _state.update { it.copy(showSheet = !it.showSheet, showLinks = !it.showLinks) }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val anime: Anime,
            val similar: List<AnimeBasic>,
            val links: List<ExternalLink>,
            val comments: Flow<PagingData<Comment>>,
            val stats: AnimeStatsQuery.Data.Anime,
            val favoured: Boolean
        ) : Response
    }
}
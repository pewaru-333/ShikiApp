@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.AnimeQuery.Anime
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Related
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient


class AnimeViewModel(private val animeId: String) : ViewModel() {
    private val _response = MutableStateFlow<AnimeResponse>(AnimeResponse.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(AnimeState())
    val state = _state.asStateFlow()

    init {
        getAnime()
    }

    fun getAnime() {
        viewModelScope.launch {
            _response.emit(AnimeResponse.Loading)

            try {
                val anime = ApolloClient.getAnime(animeId)
                val related = NetworkClient.anime.getRelated(animeId.toLong())
                val links = NetworkClient.anime.getLinks(animeId.toLong())
                val favoured = NetworkClient.anime.getAnime(animeId.toLong()).favoured

                _response.emit(AnimeResponse.Success(anime, links, related, favoured))
            } catch (e: Throwable) {
                e.printStackTrace()
                _response.emit(AnimeResponse.Error)
            }
        }
    }

    fun changeFavourite(flag: Boolean) {
        viewModelScope.launch {
            try {
                if (flag) NetworkClient.profile.deleteFavourite("Anime", animeId.toLong())
                else NetworkClient.profile.addFavourite("Anime", animeId.toLong())
                getAnime()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun reload() {
        viewModelScope.launch { hideRate(); delay(300); getAnime() }
    }

    fun showFull() {
        viewModelScope.launch { _state.update { it.copy(showFull = true, showSheet = false) } }
    }

    fun hideFull() {
        viewModelScope.launch { _state.update { it.copy(showFull = false, showSheet = true) } }
    }

    fun showSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = true) } }
    }

    fun hideSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = false) } }
    }

    fun showRate() {
        viewModelScope.launch { _state.update { it.copy(showRate = true, showSheet = false) } }
    }

    fun hideRate() {
        viewModelScope.launch { _state.update { it.copy(showRate = false) } }
    }

    fun showRelated() {
        viewModelScope.launch { _state.update { it.copy(showRelated = true) } }
    }

    fun hideRelated() {
        viewModelScope.launch { _state.update { it.copy(showRelated = false) } }
    }

    fun showLinks() {
        viewModelScope.launch { _state.update { it.copy(showSheet = false, showLinks = true) } }
    }

    fun hideLinks() {
        viewModelScope.launch { _state.update { it.copy(showSheet = true, showLinks = false) } }
    }

    fun showCharacters() {
        viewModelScope.launch { _state.update { it.copy(showCharacters = true) } }
    }

    fun hideCharacters() {
        viewModelScope.launch { _state.update { it.copy(showCharacters = false) } }
    }

    fun showAuthors() {
        viewModelScope.launch { _state.update { it.copy(showAuthors = true) } }
    }

    fun hideAuthors() {
        viewModelScope.launch { _state.update { it.copy(showAuthors = false) } }
    }

    fun showScreenshot(index: Int) {
        viewModelScope.launch {
            _state.update { it.copy(showScreenshot = true, screenshot = index) }
        }
    }

    fun hideScreenshot() {
        viewModelScope.launch {
            _state.update { it.copy(showScreenshot = false, screenshot = 0) }
        }
    }

    fun showScreenshots() {
        viewModelScope.launch { _state.update { it.copy(showScreenshots = true) } }
    }

    fun hideScreenshots() {
        viewModelScope.launch { _state.update { it.copy(showScreenshots = false) } }
    }

    fun showVideo() {
        viewModelScope.launch { _state.update { it.copy(showVideo = true) } }
    }

    fun hideVideo() {
        viewModelScope.launch { _state.update { it.copy(showVideo = false) } }
    }
}

data class AnimeState(
    val showFull: Boolean = false,
    val showSheet: Boolean = false,
    val showRate: Boolean = false,
    val showRelated: Boolean = false,
    val showLinks: Boolean = false,
    val showCharacters: Boolean = false,
    val showAuthors: Boolean = false,
    val showScreenshot: Boolean = false,
    val showScreenshots: Boolean = false,
    val showVideo: Boolean = false,
    val screenshot: Int = 0,
    val sheetState: SheetState = SheetState(false, Density(1f)),
    val linksState: SheetState = SheetState(false, Density(1f)),
    val characterLazyState: LazyListState = LazyListState(),
    val authorsLazyState: LazyListState = LazyListState()
)

sealed interface AnimeResponse {
    data object Error : AnimeResponse
    data object Loading : AnimeResponse
    data class Success(
        val anime: Anime,
        val links: List<ExternalLink>,
        val related: List<Related>,
        val favoured: Boolean
    ) : AnimeResponse
}
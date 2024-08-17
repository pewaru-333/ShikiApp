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
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE


class AnimeViewModel(private val animeId: String) : ViewModel() {
    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(AnimeState())
    val state = _state.asStateFlow()

    init {
        getAnime()
    }

    fun getAnime() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val anime = ApolloClient.getAnime(animeId)
                val similar = NetworkClient.anime.getSimilar(animeId.toLong())
                val links = NetworkClient.anime.getLinks(animeId.toLong())
                val favoured = NetworkClient.anime.getAnime(animeId.toLong()).favoured

                _response.emit(Response.Success(anime, similar, links, favoured))
            } catch (e: Throwable) {
                e.printStackTrace()
                _response.emit(Response.Error)
            }
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

    fun reload() {
        viewModelScope.launch { hideRate(); delay(300); getAnime() }
    }

    fun showComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = true) } }
    }

    fun hideComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = false) } }
    }

    fun showSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = true) } }
    }

    fun hideSheet() {
        viewModelScope.launch { _state.update { it.copy(showSheet = false) } }
    }

    fun showRelated() {
        viewModelScope.launch { _state.update { it.copy(showRelated = true) } }
    }

    fun hideRelated() {
        viewModelScope.launch { _state.update { it.copy(showRelated = false) } }
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

    fun showScreenshots() {
        viewModelScope.launch { _state.update { it.copy(showScreenshots = true) } }
    }

    fun hideScreenshots() {
        viewModelScope.launch { _state.update { it.copy(showScreenshots = false) } }
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

    fun setScreenshot(index: Int) {
        viewModelScope.launch { _state.update { it.copy(screenshot = index) } }
    }

    fun showVideo() {
        viewModelScope.launch { _state.update { it.copy(showVideo = true) } }
    }

    fun hideVideo() {
        viewModelScope.launch { _state.update { it.copy(showVideo = false) } }
    }

    fun showRate() {
        viewModelScope.launch { _state.update { it.copy(showRate = true, showSheet = false) } }
    }

    fun hideRate() {
        viewModelScope.launch { _state.update { it.copy(showRate = false) } }
    }

    fun showSimilar() {
        viewModelScope.launch { _state.update { it.copy(showSimilar = true, showSheet = false) } }
    }

    fun hideSimilar() {
        viewModelScope.launch { _state.update { it.copy(showSimilar = false, showSheet = true) } }
    }

    fun showStats() {
        viewModelScope.launch { _state.update { it.copy(showStats = true, showSheet = false) } }
    }

    fun hideStats() {
        viewModelScope.launch { _state.update { it.copy(showStats = false, showSheet = true) } }
    }

    fun showLinks() {
        viewModelScope.launch { _state.update { it.copy(showSheet = false, showLinks = true) } }
    }

    fun hideLinks() {
        viewModelScope.launch { _state.update { it.copy(showSheet = true, showLinks = false) } }
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val anime: Anime,
            val similar: List<AnimeShort>,
            val links: List<ExternalLink>,
            val favoured: Boolean
        ) : Response
    }
}

data class AnimeState(
    val showComments: Boolean = false,
    val showSheet: Boolean = false,
    val showRelated: Boolean = false,
    val showCharacters: Boolean = false,
    val showAuthors: Boolean = false,
    val showScreenshots: Boolean = false,
    val showScreenshot: Boolean = false,
    val showVideo: Boolean = false,
    val showRate: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false,
    val showLinks: Boolean = false,
    val screenshot: Int = 0,
    val sheetBottom: SheetState = SheetState(false, Density(1f)),
    val sheetLinks: SheetState = SheetState(false, Density(1f)),
    val lazyCharacters: LazyListState = LazyListState(),
    val lazyAuthors: LazyListState = LazyListState(),
    val lazySimilar: LazyListState = LazyListState()
)
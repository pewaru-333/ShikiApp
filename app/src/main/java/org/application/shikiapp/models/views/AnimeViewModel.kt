package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.AnimeQuery.Anime
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient
import retrofit2.HttpException


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
                val anime = ApolloClient.getAnime(animeId).first()
                val favoured = NetworkClient.anime.getAnime(animeId.toLong()).favoured

                _response.emit(AnimeResponse.Success(anime, favoured))
            } catch (e: HttpException) {
                _response.emit(AnimeResponse.Error)
            }
        }
    }

    fun addFavourite() {
        viewModelScope.launch {
            try {
                NetworkClient.profile.addFavourite("Anime", animeId.toLong())
                getAnime()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    fun deleteFavourite() {
        viewModelScope.launch {
            try {
                NetworkClient.profile.deleteFavourite("Anime", animeId.toLong())
                getAnime()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    fun reload() { viewModelScope.launch { hideRate(); delay(300); getAnime() } }

    fun showFull() { viewModelScope.launch { _state.update { it.copy(showFull = true) } } }

    fun hideFull() { viewModelScope.launch { _state.update { it.copy(showFull = false) } } }

    fun showRate() { viewModelScope.launch { _state.update { it.copy(showRate = true) } } }

    fun hideRate() { viewModelScope.launch { _state.update { it.copy(showRate = false) } } }

    fun showCharacters() { viewModelScope.launch { _state.update { it.copy(showCharacters = true) } } }

    fun hideCharacters() { viewModelScope.launch { _state.update { it.copy(showCharacters = false) } } }

    fun showAuthors() { viewModelScope.launch { _state.update { it.copy(showAuthors = true) } } }

    fun hideAuthors() { viewModelScope.launch { _state.update { it.copy(showAuthors = false) } } }

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

    fun showScreenshots() { viewModelScope.launch { _state.update { it.copy(showScreenshots = true) } } }

    fun hideScreenshots() { viewModelScope.launch { _state.update { it.copy(showScreenshots = false) } } }

    fun showVideo() { viewModelScope.launch { _state.update { it.copy(showVideo = true) } } }

    fun hideVideo() { viewModelScope.launch { _state.update { it.copy(showVideo = false) } } }
}

data class AnimeState(
    val showFull: Boolean = false,
    val showRate: Boolean = false,
    val showCharacters: Boolean = false,
    val showAuthors: Boolean = false,
    val showScreenshot: Boolean = false,
    val showScreenshots: Boolean = false,
    val showVideo: Boolean = false,
    val screenshot: Int = 0,
    val characterLazyState: LazyListState = LazyListState(),
    val authorsLazyState: LazyListState = LazyListState()
)

sealed interface AnimeResponse {
    data object Error : AnimeResponse
    data object Loading : AnimeResponse
    data class Success(val anime: Anime, val favoured: Boolean) : AnimeResponse
}
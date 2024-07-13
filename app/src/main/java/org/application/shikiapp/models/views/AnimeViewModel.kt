package org.application.shikiapp.models.views

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
            } catch (e: Throwable) {
                _response.emit(AnimeResponse.Error)
            }
        }
    }

    fun addFavourite() {
        viewModelScope.launch {
            try {
                NetworkClient.profile.addFavourite("Anime", animeId.toLong())
                getAnime()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun deleteFavourite() {
        viewModelScope.launch {
            try {
                NetworkClient.profile.deleteFavourite("Anime", animeId.toLong())
                getAnime()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun reload() { viewModelScope.launch { closeRate(); delay(300); getAnime() } }

    fun showFull() { viewModelScope.launch { _state.update { it.copy(showFull = true) } } }

    fun closeFull() { viewModelScope.launch { _state.update { it.copy(showFull = false) } } }

    fun showRate() { viewModelScope.launch { _state.update { it.copy(showRate = true) } } }

    fun closeRate() { viewModelScope.launch { _state.update { it.copy(showRate = false) } } }
}

data class AnimeState(
    val showFull: Boolean = false,
    val showRate: Boolean = false
)

sealed interface AnimeResponse {
    data object Error : AnimeResponse
    data object Loading : AnimeResponse
    data class Success(val anime: Anime, val favoured: Boolean) : AnimeResponse
}
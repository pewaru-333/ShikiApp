package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.models.states.WatchState
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.mappers.toVideoVoice
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.parser.KodikParser
import org.application.shikiapp.shared.network.parser.KodikResultItem
import org.application.shikiapp.shared.utils.navigation.Screen

class WatchViewModel(saved: SavedStateHandle) : ViewModel() {
    private val contentId = saved.toRoute<Screen.Watch>().contentId

    private val _state = MutableStateFlow(WatchState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), WatchState())

    private fun loadData() {
        if (currentState.voices.isNotEmpty() || currentState.isLoading) return

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val parser = KodikParser.getInstance(Network.watchClient)
                val voices = parser
                    .searchByShikimoriId(contentId)
                    .map(KodikResultItem::toVideoVoice)

                _state.update {
                    it.copy(
                        isLoading = false,
                        voices = voices,
                    )
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun selectVoice(id: Int) = _state.update {
        it.copy(
            currentVoice = it.voices.find { voice -> voice.id == id }
        )
    }

    fun clearVoice() = _state.update {
        it.copy(
            isWatching = false,
            videoUrl = null,
            currentVoice = null,
            currentEpisode = null
        )
    }

    fun selectEpisode(episodeNumber: Int) {
        val selectedVoice = currentState.currentVoice ?: return
        val episodeToLoad = selectedVoice.episodes.find { it.number == episodeNumber }

        if (episodeToLoad != null) {
            loadVideo(episodeToLoad)
        }
    }

    fun loadVideo(episode: EpisodeModel) {
        _state.update {
            it.copy(
                currentEpisode = episode.number,
                isVideoLoading = true,
                isWatching = true
            )
        }

        viewModelScope.launch {
            try {
                val parser = KodikParser.getInstance(Network.watchClient)
                val result = parser.getPlaylistLink(
                    episodeLink = episode.link,
                    quality = currentState.currentQuality
                )

                _state.update {
                    it.copy(
                        isVideoLoading = false,
                        qualityList = result.qualityList,
                        videoUrl = result.url
                    )
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(isVideoLoading = false)
                }
            }
        }
    }

    fun changeQuality(newQuality: Int) {
        val selectedVoice = currentState.currentVoice ?: return
        val episodeNumber = currentState.currentEpisode ?: return
        val episode = selectedVoice.episodes.find { it.number == episodeNumber } ?: return

        _state.update {
            it.copy(
                isVideoLoading = true,
                currentQuality = newQuality
            )
        }

        viewModelScope.launch {
            try {
                val parser = KodikParser.getInstance(Network.watchClient)
                val result = parser.getPlaylistLink(
                    episodeLink = episode.link,
                    quality = newQuality
                )

                _state.update {
                    it.copy(
                        isVideoLoading = false,
                        videoUrl = result.url
                    )
                }
            } catch (_: Exception) {
                stopWatching()
            }
        }
    }

    fun stopWatching() = _state.update {
        it.copy(
            isWatching = false,
            currentEpisode = null,
            videoUrl = null
        )
    }

    private val currentState: WatchState
        get() = _state.value
}
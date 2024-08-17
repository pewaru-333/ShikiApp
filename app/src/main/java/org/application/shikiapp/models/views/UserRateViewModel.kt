package org.application.shikiapp.models.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences

class UserRateViewModel : ViewModel() {
    private val _newRate = MutableStateFlow(NewRate())
    val newRate = _newRate.asStateFlow()

    var show by mutableStateOf(false)
        private set

    fun onEvent(event: NewRateEvent) = when (event) {
        is NewRateEvent.SetRateId -> _newRate.update { it.copy(id = event.rateId) }

        is NewRateEvent.SetStatus -> _newRate.update {
            it.copy(status = event.status.key, statusName = event.status.value)
        }

        is NewRateEvent.SetScore -> _newRate.update {
            it.copy(score = event.score.key, scoreName = event.score.value)
        }
        is NewRateEvent.SetChapters -> _newRate.update { it.copy(chapters = event.chapters) }

        is NewRateEvent.SetEpisodes -> event.episodes.let { episodes ->
            if (episodes.isDigitsOnly()) _newRate.update { it.copy(episodes = episodes) }
        }

        is NewRateEvent.SetVolumes -> _newRate.update { it.copy(volumes = event.volumes) }

        is NewRateEvent.SetRewatches -> event.rewatches.let { rewatches ->
            if (rewatches.isDigitsOnly()) _newRate.update { it.copy(rewatches = rewatches) }
        }

        is NewRateEvent.SetText -> _newRate.update { it.copy(text = event.text ?: BLANK) }
    }

    fun createRate(animeId: String) {
        viewModelScope.launch {
            try {
                NetworkClient.rates.createRate(
                    org.application.shikiapp.models.data.NewRate(
                        userId = Preferences.getUserId(),
                        targetId = animeId.toLong(),
                        targetType = "Anime",
                        status = _newRate.value.status,
                        score = _newRate.value.score.toString(),
                        chapters = _newRate.value.chapters,
                        episodes = _newRate.value.episodes,
                        volumes = _newRate.value.volumes,
                        rewatches = _newRate.value.rewatches,
                        text = _newRate.value.text
                    )
                )
            } catch (e: Throwable) {
                LoadState.Error(e)
            }
        }
    }

    fun updateRate(rateId: String) {
        viewModelScope.launch {
            try {
                NetworkClient.rates.updateRate(
                    id = rateId.toLong(),
                    newRate = org.application.shikiapp.models.data.NewRate(
                        userId = Preferences.getUserId(),
                        status = _newRate.value.status,
                        score = _newRate.value.score.toString(),
                        chapters = _newRate.value.chapters,
                        episodes = _newRate.value.episodes,
                        volumes = _newRate.value.volumes,
                        rewatches = _newRate.value.rewatches,
                        text = _newRate.value.text
                    )
                )
            } catch (e: Throwable) {
                LoadState.Error(e)
            }
        }
    }

    fun deleteRate(rateId: String) {
        viewModelScope.launch {
            try {
                NetworkClient.rates.delete(rateId.toLong())
                _newRate.emit(NewRate())
            } catch (e: Throwable) {
                LoadState.Error(e)
            }
        }
    }

    fun increment(rateId: Long) {
        viewModelScope.launch {
            try {
                NetworkClient.rates.increment(rateId)
            } catch (e: Throwable) {
                LoadState.Error(e)
            }
        }
    }

    fun reload(model: UserRatesViewModel) {
        viewModelScope.launch {
            close()
            model.reload()
        }
    }

    fun open() { show = true  }

    fun close() { show = false }
}

data class NewRate(
    val id: String = BLANK,
    val userId: Long = 0L,
    val targetId: Long = 0L,
    val targetType: String = "Anime",
    val status: String? = null,
    val statusName: String = BLANK,
    val score: Int? = null,
    val scoreName: String? = null,
    val chapters: String? = null,
    val episodes: String? = null,
    val volumes: String? = null,
    val rewatches: String? = null,
    val text: String? = null
)

sealed interface NewRateEvent {
    data class SetRateId(val rateId: String) : NewRateEvent
    data class SetStatus(val status: Map.Entry<String, String>) : NewRateEvent
    data class SetScore(val score: Map.Entry<Int, String>) : NewRateEvent
    data class SetChapters(val chapters: String) : NewRateEvent
    data class SetEpisodes(val episodes: String) : NewRateEvent
    data class SetVolumes(val volumes: String) : NewRateEvent
    data class SetRewatches(val rewatches: String) : NewRateEvent
    data class SetText(val text: String?) : NewRateEvent
}
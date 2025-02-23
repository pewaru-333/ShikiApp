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
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetChapters
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetEpisodes
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetRateId
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetRewatches
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetScore
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetStatus
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetText
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetVolumes
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences

class UserRateViewModel : ViewModel() {
    private val _newRate = MutableStateFlow(NewRate())
    val newRate = _newRate.asStateFlow()

    var show by mutableStateOf(false)
        private set

    fun onEvent(event: RateEvent) = when (event) {
        is SetRateId -> _newRate.update { it.copy(id = event.rateId) }

        is SetStatus -> _newRate.update {
            it.copy(status = event.status.key, statusName = event.status.value)
        }

        is SetScore -> _newRate.update {
            it.copy(score = event.score.key, scoreName = event.score.value)
        }
        is SetChapters -> _newRate.update { it.copy(chapters = event.chapters) }

        is SetEpisodes -> event.episodes.let { episodes ->
            if (episodes != null && episodes.isDigitsOnly())
                _newRate.update { it.copy(episodes = episodes) }
        }

        is SetVolumes -> _newRate.update { it.copy(volumes = event.volumes) }

        is SetRewatches -> event.rewatches.let { rewatches ->
            if (rewatches != null && rewatches.isDigitsOnly())
                _newRate.update { it.copy(rewatches = rewatches) }
        }

        is SetText -> _newRate.update { it.copy(text = event.text ?: BLANK) }
    }

    fun create(id: String, targetType: String) {
        viewModelScope.launch {
            try {
                NetworkClient.rates.createRate(
                    org.application.shikiapp.models.data.NewRate(
                        userId = Preferences.getUserId(),
                        targetId = id.toLong(),
                        targetType = targetType,
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

    fun update(rateId: String) {
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

    fun delete(rateId: String) {
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

    fun reload(anime: AnimeRatesViewModel? = null, manga: MangaRatesViewModel? = null) {
        viewModelScope.launch {
            close()
            anime?.reload()
            manga?.reload()
        }
    }

    fun open() { show = true  }

    fun close() { show = false }

    sealed interface RateEvent {
        data class SetRateId(val rateId: String) : RateEvent
        data class SetStatus(val status: Map.Entry<String, String>) : RateEvent
        data class SetScore(val score: Map.Entry<Int, String>) : RateEvent
        data class SetChapters(val chapters: String?) : RateEvent
        data class SetEpisodes(val episodes: String?) : RateEvent
        data class SetVolumes(val volumes: String?) : RateEvent
        data class SetRewatches(val rewatches: String?) : RateEvent
        data class SetText(val text: String?) : RateEvent
    }
}

data class NewRate(
    val id: String = BLANK,
    val userId: Long = Preferences.getUserId(),
    val targetId: Long = 0L,
    val targetType: String = BLANK,
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
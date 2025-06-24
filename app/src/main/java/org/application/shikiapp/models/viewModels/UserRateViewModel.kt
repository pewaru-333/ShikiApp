package org.application.shikiapp.models.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.events.RateEvent.SetChapters
import org.application.shikiapp.events.RateEvent.SetEpisodes
import org.application.shikiapp.events.RateEvent.SetRateId
import org.application.shikiapp.events.RateEvent.SetRewatches
import org.application.shikiapp.events.RateEvent.SetScore
import org.application.shikiapp.events.RateEvent.SetStatus
import org.application.shikiapp.events.RateEvent.SetText
import org.application.shikiapp.events.RateEvent.SetVolumes
import org.application.shikiapp.models.data.BaseRate
import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.models.states.RatesState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.RatesResponse
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.navigation.Screen

class UserRateViewModel(saved: SavedStateHandle) : ViewModel() {
    val args = saved.toRoute<Screen.UserRates>()
    val type = args.type ?: LinkedType.ANIME
    val userId = args.id ?: 0L
    val editable = userId == Preferences.userId

    private val _response = MutableStateFlow<RatesResponse>(RatesResponse.Loading)
    val response = _response.asStateFlow()
        .onStart { loadRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), RatesResponse.Loading)

    private val _state = MutableStateFlow(RatesState())
    val state = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), RatesState())

    private val _newRate = MutableStateFlow(NewRateState())
    val newRate = _newRate.asStateFlow()

    private val _increment = Channel<Boolean>()
    val increment = _increment.receiveAsFlow()

    val rates = combine(_response, _state) { response, state ->
        if (response !is RatesResponse.Success) emptyList()
        else response.rates
            .map<BaseRate, UserRate>(BaseRate::mapper)
            .filter { state.tab.safeEquals(it.status) }
            .sortedBy(UserRate::title)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun loadRates() {
        viewModelScope.launch {
            _response.emit(RatesResponse.Loading)

            try {
                val rates = mutableListOf<BaseRate>()
                var page = 0
                var moreDataAvailable = true

                while (moreDataAvailable) {
                    val calls = (1..5).map {
                        viewModelScope.async {
                            try {
                                if (type == LinkedType.ANIME) Network.user.getAnimeRates(id = userId, page = page + it)
                                else Network.user.getMangaRates(id = userId, page = page + it)
                            } catch (_: Throwable) {
                                emptyList()
                            }
                        }
                    }

                    val results = calls.awaitAll()
                    rates.addAll(results.flatten())

                    moreDataAvailable = results.any { it.size >= 100 }
                    page += 5
                }

                _response.emit(RatesResponse.Success(rates.distinctBy(BaseRate::id)))
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 403) _response.emit(RatesResponse.NoAccess)
                else _response.emit(RatesResponse.Error)
            }
        }
    }

    fun setTab(status: WatchStatus) = _state.update { it.copy(tab = status) }
    fun toggleDialog() = _state.update { it.copy(showEditRate = !it.showEditRate) }

    fun create(id: String, targetType: LinkedType, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                Network.rates.createRate(
                    NewRate(
                        userId = Preferences.userId,
                        targetId = id.toLong(),
                        targetType = targetType.name.lowercase().replaceFirstChar(Char::uppercase),
                        status = _newRate.value.status.toString().lowercase(),
                        score = _newRate.value.score?.score.toString(),
                        chapters = _newRate.value.chapters,
                        episodes = _newRate.value.episodes,
                        volumes = _newRate.value.volumes,
                        rewatches = _newRate.value.rewatches,
                        text = _newRate.value.text
                    )
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                reload()
            }
        }
    }

    fun update(rateId: String, reload: () -> Unit = ::loadRates) {
        viewModelScope.launch {
            toggleDialog()
            _response.emit(RatesResponse.Loading)

            try {
                val request = Network.rates.updateRate(
                    id = rateId.toLong(),
                    newRate = NewRate(
                        userId = Preferences.userId,
                        status = _newRate.value.status.toString().lowercase(),
                        score = _newRate.value.score?.score.toString(),
                        chapters = _newRate.value.chapters,
                        episodes = _newRate.value.episodes,
                        volumes = _newRate.value.volumes,
                        rewatches = _newRate.value.rewatches,
                        text = _newRate.value.text
                    )
                )

                _increment.trySend(request.status == HttpStatusCode.OK)
            } catch (_: Throwable) {
                _increment.trySend(false)
            } finally {
                reload()
            }
        }
    }

    fun delete(rateId: String, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = Network.rates.delete(rateId.toLong())

                _increment.trySend(request.status == HttpStatusCode.NoContent)
                _newRate.emit(NewRateState())
            } catch (_: Throwable) {
                _increment.trySend(false)
            } finally {
                reload()
            }
        }
    }

    fun increment(rateId: Long) {
        viewModelScope.launch {
            _response.emit(RatesResponse.Loading)

            try {
                val request = Network.rates.increment(rateId)

                _increment.send(request.status == HttpStatusCode.Created)
            } catch (_: Exception) {
                _increment.send(false)
            } finally {
                loadRates()
            }
        }
    }

    fun onEvent(event: RateEvent) = when (event) {
        is SetRateId -> _newRate.update { it.copy(id = event.rateId) }

        is SetStatus -> _newRate.update {
            it.copy(
                status = event.status.name,
                statusName = if (event.type == LinkedType.ANIME) event.status.titleAnime
                else event.status.titleManga
            )
        }

        is SetScore -> _newRate.update {
            it.copy(score = event.score)
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
}
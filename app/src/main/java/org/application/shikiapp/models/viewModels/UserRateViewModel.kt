package org.application.shikiapp.models.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.application.shikiapp.generated.type.UserRateTargetTypeEnum
import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.models.states.SortingState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.RatesResponse
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.OrderDirection
import org.application.shikiapp.utils.enums.OrderRates
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.navigation.Screen

class UserRateViewModel(saved: SavedStateHandle) : ViewModel() {
    private val args = runCatching { saved.toRoute<Screen.UserRates>() }.getOrNull()

    var type = args?.type ?: LinkedType.ANIME

    val editable = args?.editable ?: false
    val userId = if (editable) Preferences.userId else args?.id

    private val _response = MutableStateFlow<RatesResponse>(RatesResponse.Loading)
    val response = _response.asStateFlow()
        .onStart { loadRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), RatesResponse.Loading)

    private val _orderState = MutableStateFlow(SortingState())
    val orderState = _orderState.asStateFlow()

    private val _newRate = MutableStateFlow(NewRateState())
    val newRate = _newRate.asStateFlow()

    private val _changed = Channel<Boolean>()
    val changed = _changed.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val rates = combine(_response, _orderState) { response, state ->
        if (response !is RatesResponse.Success) emptyMap()
        else response.rates
            .groupBy { Enum.safeValueOf<WatchStatus>(it.status) }
            .mapValues { (key, value) ->
                if (state.direction == OrderDirection.ASCENDING) {
                    when (state.order) {
                        OrderRates.TITLE -> value.sortedBy(UserRate::title)
                        OrderRates.SCORE -> value.sortedBy(UserRate::score)
                        OrderRates.EPISODES -> value.sortedBy(UserRate::episodes)
                        OrderRates.KIND -> value.sortedBy(UserRate::kind)
                        OrderRates.CREATED_AT -> value.sortedBy(UserRate::createdAt)
                        OrderRates.UPDATE_AT -> value.sortedBy(UserRate::updatedAt)
                    }
                } else {
                    when (state.order) {
                        OrderRates.TITLE -> value.sortedByDescending(UserRate::title)
                        OrderRates.SCORE -> value.sortedByDescending(UserRate::score)
                        OrderRates.EPISODES -> value.sortedByDescending(UserRate::episodes)
                        OrderRates.KIND -> value.sortedByDescending(UserRate::kind)
                        OrderRates.CREATED_AT -> value.sortedByDescending(UserRate::createdAt)
                        OrderRates.UPDATE_AT -> value.sortedByDescending(UserRate::updatedAt)
                    }
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyMap()
    )

    var showEditDialog by mutableStateOf(false)
        private set

    fun loadRates() {
        if (userId == null) {
            return
        }

        viewModelScope.launch {
            if (Preferences.token == null && editable) {
                _response.emit(RatesResponse.Unlogged)
                return@launch
            }

            _response.emit(RatesResponse.Loading)

            val rates = mutableListOf<UserRate>()
            var page = 0
            var moreDataAvailable = true

            try {
                while (moreDataAvailable) {
                    val calls = (1..5).map {
                        async {
                            try {
                                GraphQL.getUserRates(
                                    userId = userId,
                                    page = page + it,
                                    limit = 50,
                                    type = Enum.safeValueOf<UserRateTargetTypeEnum>(type.name)
                                )
                            } catch (_: Throwable) {
                                emptyList()
                            }
                        }
                    }

                    val results = calls.awaitAll()
                    rates.addAll(results.flatten())

                    moreDataAvailable = results.any { it.size == 50 }
                    page += 5
                }

                _response.emit(RatesResponse.Success(rates))
            } catch (e: ResponseException) {
                if (e.response.status.value == 403) _response.emit(RatesResponse.NoAccess)
                else _response.emit(RatesResponse.Error)
            }
        }
    }

    fun onSortChanged(orderType: OrderRates) {
        viewModelScope.launch {
            _orderState.value.let { value ->
                if (value.order == orderType) {
                    if (value.direction == OrderDirection.ASCENDING) {
                        _orderState.emit(SortingState(orderType, OrderDirection.DESCENDING))
                    } else {
                        _orderState.emit(SortingState())
                    }
                } else {
                    _orderState.emit(SortingState(orderType, OrderDirection.ASCENDING))
                }
            }
        }
    }

    fun toggleDialog() {
        showEditDialog = !showEditDialog
    }

    fun setLinkedType(type: LinkedType) {
        if (type == this.type) {
            return
        }

        this.type = type

        loadRates()
    }

    fun getRate(rate: UserRate) {
        onEvent(SetRateId(rate.id.toString()))
        onEvent(SetStatus(Enum.safeValueOf<WatchStatus>(rate.status), type))
        onEvent(SetScore(Score.entries.first { it.score == rate.score }))
        onEvent(SetChapters(rate.chapters.toString()))
        onEvent(SetEpisodes(rate.episodes.toString()))
        onEvent(SetVolumes(rate.volumes.toString()))
        onEvent(SetRewatches(rate.rewatches.toString()))
        onEvent(SetText(rate.text))

        showEditDialog = true
    }

    fun create(id: String, targetType: LinkedType, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                val newRate = _newRate.value

                Network.rates.createRate(
                    NewRate(
                        userId = Preferences.userId,
                        targetId = id.toLong(),
                        targetType = targetType.name.lowercase().replaceFirstChar(Char::uppercase),
                        status = newRate.status.toString().lowercase(),
                        score = newRate.score?.score.toString(),
                        chapters = newRate.chapters,
                        episodes = newRate.episodes,
                        volumes = newRate.volumes,
                        rewatches = newRate.rewatches,
                        text = newRate.text
                    )
                )
            } catch (_: Throwable) {

            } finally {
                reload()
            }
        }
    }

    fun update(rateId: String) {
        viewModelScope.launch {
            val currentState =  _response.value as? RatesResponse.Success ?: return@launch
            val newRate = _newRate.value

            showEditDialog = false

            try {
                val request = Network.rates.updateRate(
                    id = rateId.toLong(),
                    newRate = NewRate(
                        userId = Preferences.userId,
                        status = newRate.status.toString().lowercase(),
                        score = newRate.score?.score.toString(),
                        chapters = newRate.chapters,
                        episodes = newRate.episodes,
                        volumes = newRate.volumes,
                        rewatches = newRate.rewatches,
                        text = newRate.text
                    )
                )

                if (request.status == HttpStatusCode.OK) {
                    val oldRates = currentState.rates
                    val index = oldRates.indexOfFirst { it.id.toString() == rateId }

                    if (index != -1) {
                        val updatedRate = oldRates[index].copy(
                            status = newRate.status.toString(),
                            score = newRate.score?.score ?: 1,
                            scoreString = newRate.score?.score.toString(),
                            chapters = newRate.chapters?.toInt() ?: 0,
                            episodes = newRate.episodes?.toInt() ?: 0,
                            volumes = newRate.volumes?.toInt() ?: 0,
                            rewatches = newRate.rewatches?.toInt() ?: 0,
                            text = newRate.text
                        )

                        val newRates = oldRates.toMutableList().apply { this[index] = updatedRate }
                        _response.emit(RatesResponse.Success(newRates))
                    }

                    _changed.trySend(true)
                } else {
                    _changed.trySend(false)
                }
            } catch (_: Throwable) {
                _changed.trySend(false)
            }
        }
    }

    fun update(rateId: String, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                val newRate = _newRate.value

                Network.rates.updateRate(
                    id = rateId.toLong(),
                    newRate = NewRate(
                        userId = Preferences.userId,
                        status = newRate.status.toString().lowercase(),
                        score = newRate.score?.score.toString(),
                        chapters = newRate.chapters,
                        episodes = newRate.episodes,
                        volumes = newRate.volumes,
                        rewatches = newRate.rewatches,
                        text = newRate.text
                    )
                )
            } catch (_: Throwable) {

            } finally {
                reload()
            }
        }
    }

    fun delete(rateId: String) {
        viewModelScope.launch {
            try {
                val request = Network.rates.delete(rateId.toLong())

                if (request.status == HttpStatusCode.NoContent) {
                    val currentResponse = _response.value
                    if (currentResponse is RatesResponse.Success) {
                        val updatedRates = currentResponse.rates.filterNot { it.id.toString() == rateId }

                        showEditDialog = false
                        _response.emit(RatesResponse.Success(updatedRates))
                        _changed.trySend(true)
                    }
                } else {
                    _changed.trySend(false)
                }
            } catch (_: Throwable) {
                _changed.trySend(false)
            }
        }
    }

    fun delete(rateId: String, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                Network.rates.delete(rateId.toLong())
                _newRate.emit(NewRateState())
            } catch (_: Throwable) {

            } finally {
                reload()
            }
        }
    }

    fun increment(rateId: Long) {
        viewModelScope.launch {
            val currentState = _response.value as? RatesResponse.Success ?: return@launch

            try {
                val request = Network.rates.increment(rateId)

                if (request.status != HttpStatusCode.Created) {
                    _changed.send(false)
                    return@launch
                }

                val newRates = currentState.rates.map { rate ->
                    if (rate.id == rateId) {
                        rate.copy(episodes = rate.episodes + 1)
                    } else {
                        rate
                    }
                }

                _response.emit(RatesResponse.Success(newRates))
                _changed.send(true)
            } catch (_: Exception) {
                _changed.send(false)
            }
        }
    }

    fun onEvent(event: RateEvent) = when (event) {
        is SetRateId -> _newRate.update { it.copy(id = event.rateId) }

        is SetStatus -> _newRate.update {
            it.copy(
                status = event.status.name,
                statusName = event.type.getWatchStatusTitle(event.status)
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
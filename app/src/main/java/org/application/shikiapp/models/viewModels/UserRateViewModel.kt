package org.application.shikiapp.models.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.di.Preferences
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
import org.application.shikiapp.models.states.SortingState
import org.application.shikiapp.models.states.UserRateUiEvent
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.RatesResponse
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.OrderDirection
import org.application.shikiapp.utils.enums.OrderRates
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.navigation.Screen

class UserRateViewModel(saved: SavedStateHandle) : ViewModel() {
    private val args = runCatching { saved.toRoute<Screen.UserRates>() }.getOrNull()

    private val _type = MutableStateFlow(args?.type ?: LinkedType.ANIME)
    val type = _type.asStateFlow()

    val editable = args?.editable ?: false
    val userId = if (editable) Preferences.userId else args?.id?.toLongOrNull()

    private val _response = MutableStateFlow<RatesResponse>(RatesResponse.Loading)
    val response = _response.asStateFlow()
        .onStart { loadRates() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), RatesResponse.Loading)

    private val _orderState = MutableStateFlow(SortingState())
    val orderState = _orderState.asStateFlow()

    private val _newRate = MutableStateFlow(NewRateState())
    val newRate = _newRate.asStateFlow()

    private val _rateUiEvent = Channel<UserRateUiEvent>()
    val rateUiEvent = _rateUiEvent.receiveAsFlow()

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
                        OrderRates.EPISODES -> value.sortedBy(UserRate::episodesSorting)
                        OrderRates.KIND -> value.sortedBy(UserRate::kind)
                        OrderRates.CREATED_AT -> value.sortedBy(UserRate::createdAt)
                        OrderRates.UPDATED_AT -> value.sortedBy(UserRate::updatedAt)
                    }
                } else {
                    when (state.order) {
                        OrderRates.TITLE -> value.sortedByDescending(UserRate::title)
                        OrderRates.SCORE -> value.sortedByDescending(UserRate::score)
                        OrderRates.EPISODES -> value.sortedByDescending(UserRate::episodesSorting)
                        OrderRates.KIND -> value.sortedByDescending(UserRate::kind)
                        OrderRates.CREATED_AT -> value.sortedByDescending(UserRate::createdAt)
                        OrderRates.UPDATED_AT -> value.sortedByDescending(UserRate::updatedAt)
                    }
                }
            }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyMap())

    var showEditDialog by mutableStateOf(false)
        private set

    fun loadRates(type: LinkedType = _type.value) {
        if (userId == null) {
            return
        }

        viewModelScope.launch {
            if (Preferences.token == null && editable) {
                _response.emit(RatesResponse.Unlogged)
                return@launch
            }

            if (_response.value !is RatesResponse.Success) {
                _response.emit(RatesResponse.Loading)
            }

            val allRates = mutableListOf<UserRate>()
            val threads = 5
            var currentPage = 0
            var moreDataAvailable = true

            try {
                while (moreDataAvailable) {
                    val rates = coroutineScope {
                        (1..threads).map { thread ->
                            async {
                                if (type == LinkedType.ANIME) {
                                    Network.rates.getAnimeRates(userId, currentPage + thread, 250)
                                } else {
                                    Network.rates.getMangaRates(userId, currentPage + thread, 250)
                                }
                            }
                        }
                    }

                    val ratesNotNull = rates.awaitAll().filterNotNull()
                    allRates.addAll(ratesNotNull.flatten().map(BaseRate::mapper))

                    if (ratesNotNull.size < threads || ratesNotNull.any { it.size < 250 }) {
                        moreDataAvailable = false
                    } else {
                        currentPage += threads
                    }
                }

                _response.emit(RatesResponse.Success(allRates.distinctBy(UserRate::id)))
            } catch (e: ClientRequestException) {
                _response.emit(
                    if (e.response.status.value == 403) RatesResponse.NoAccess
                    else RatesResponse.Error
                )
            } catch (_: Exception) {
                _response.emit(RatesResponse.Error)
            }
        }
    }

    fun onSortChanged(orderType: OrderRates) {
        _orderState.update {
            if (orderType == it.order) {
                if (it.direction == OrderDirection.ASCENDING) {
                    it.copy(direction = OrderDirection.DESCENDING)
                } else {
                    it.copy(direction = OrderDirection.ASCENDING)
                }
            } else {
                SortingState(orderType, OrderDirection.ASCENDING)
            }
        }
    }

    fun toggleDialog() {
        showEditDialog = !showEditDialog
    }

    fun setLinkedType(type: LinkedType) {
        _type.update { type }

        viewModelScope.launch {
            _response.emit(RatesResponse.Loading)

            loadRates(type)
        }
    }

    fun getRate(rate: UserRate, linkedType: LinkedType = type.value) {
        onEvent(SetRateId(rate.id.toString()))
        onEvent(SetStatus(Enum.safeValueOf<WatchStatus>(rate.status), linkedType))
        onEvent(SetScore(Score.entries.first { it.score == rate.score }))
        onEvent(SetChapters(rate.chapters.takeIf { it > 0 }?.toString()))
        onEvent(SetEpisodes(rate.episodes.takeIf { it > 0 }?.toString()))
        onEvent(SetVolumes(rate.volumes.takeIf { it > 0 }?.toString()))
        onEvent(SetRewatches(rate.rewatches.takeIf { it > 0 }?.toString()))
        onEvent(SetText(rate.text))

        showEditDialog = true
    }

    fun create(id: String, targetType: LinkedType, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                with(_newRate.value) {
                    Network.rates.createRate(
                        NewRate(
                            userId = Preferences.userId,
                            targetId = id.toLong(),
                            targetType = targetType.name.lowercase().replaceFirstChar(Char::uppercase),
                            status = status.toString().lowercase(),
                            score = score?.score.toString(),
                            chapters = chapters,
                            episodes = episodes,
                            volumes = volumes,
                            rewatches = rewatches,
                            text = text
                        )
                    )
                }
            } catch (_: Exception) {

            } finally {
                reload()
            }
        }
    }

    fun update(rateId: String) {
        viewModelScope.launch {
            val currentState =  _response.value as? RatesResponse.Success ?: return@launch

            _rateUiEvent.send(UserRateUiEvent.UpdateStart(rateId.toLong()))
            showEditDialog = false

            try {
                val request = with(_newRate.value) {
                    Network.rates.updateRate(
                        id = rateId.toLong(),
                        newRate = NewRate(
                            userId = Preferences.userId,
                            status = status.toString().lowercase(),
                            score = score?.score.toString(),
                            chapters = chapters,
                            episodes = episodes,
                            volumes = volumes,
                            rewatches = rewatches,
                            text = text
                        )
                    )
                }

                if (request.status == HttpStatusCode.OK) {
                    val responseRate = request.body<NewRate>()
                    val oldRates = currentState.rates
                    val index = oldRates.indexOfFirst { it.id.toString() == rateId }

                    if (index != -1) {
                        val updatedRate = oldRates[index].copy(
                            status = responseRate.status.toString(),
                            score = responseRate.score?.toIntOrNull() ?: 0,
                            scoreString = responseRate.score.let { if (it?.toIntOrNull() != 0) it else '-' }.toString(),
                            chapters = responseRate.chapters?.toIntOrNull() ?: 0,
                            episodes = responseRate.episodes?.toIntOrNull() ?: 0,
                            volumes = responseRate.volumes?.toIntOrNull() ?: 0,
                            rewatches = responseRate.rewatches?.toIntOrNull() ?: 0,
                            text = responseRate.text
                        )

                        val newRates = oldRates.toMutableList().apply { this[index] = updatedRate }

                        _response.emit(RatesResponse.Success(newRates))
                        _rateUiEvent.send(UserRateUiEvent.UpdateFinish)
                    }
                } else {
                    _rateUiEvent.send(UserRateUiEvent.Error)
                }
            } catch (_: Exception) {
                _rateUiEvent.send(UserRateUiEvent.Error)
            }
        }
    }

    fun update(rateId: String, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                with(_newRate.value) {
                    Network.rates.updateRate(
                        id = rateId.toLong(),
                        newRate = NewRate(
                            userId = Preferences.userId,
                            status = status.toString().lowercase(),
                            score = score?.score.toString(),
                            chapters = chapters,
                            episodes = episodes,
                            volumes = volumes,
                            rewatches = rewatches,
                            text = text
                        )
                    )
                }
            } catch (_: Exception) {

            } finally {
                reload()
            }
        }
    }

    fun delete(rateId: String) {
        viewModelScope.launch {
            _rateUiEvent.send(UserRateUiEvent.DeleteStart(rateId.toLong()))
            showEditDialog = false

            try {
                val request = Network.rates.delete(rateId.toLong())

                if (request.status == HttpStatusCode.NoContent) {
                    with(_response.value) {
                        if (this !is RatesResponse.Success) return@launch

                        val updatedRates = rates.filterNot { it.id.toString() == rateId }

                        _response.emit(RatesResponse.Success(updatedRates))
                        _rateUiEvent.send(UserRateUiEvent.DeleteFinish)
                    }
                } else {
                    _rateUiEvent.send(UserRateUiEvent.Error)
                }
            } catch (_: Exception) {
                _rateUiEvent.send(UserRateUiEvent.Error)
            }
        }
    }

    fun delete(rateId: String, reload: () -> Unit) {
        viewModelScope.launch {
            try {
                Network.rates.delete(rateId.toLong())
                _newRate.emit(NewRateState())
            } catch (_: Exception) {

            } finally {
                reload()
            }
        }
    }

    fun increment(rateId: Long) {
        viewModelScope.launch {
            _rateUiEvent.send(UserRateUiEvent.IncrementStart(rateId))

            try {
                val request = Network.rates.increment(rateId)

                if (request.status != HttpStatusCode.Created) {
                    _rateUiEvent.send(UserRateUiEvent.Error)

                    return@launch
                }

                with(_response.value) {
                    if (this !is RatesResponse.Success) return@launch

                    val newRates = rates.map { rate ->
                        if (rate.id == rateId) {
                            if (_type.value == LinkedType.ANIME) {
                                rate.copy(episodes = rate.episodes + 1)
                            } else
                                rate.copy(chapters = rate.chapters + 1)
                        } else {
                            rate
                        }
                    }

                    _response.emit(RatesResponse.Success(newRates))
                    _rateUiEvent.send(UserRateUiEvent.IncrementFinish)
                }
            } catch (_: Exception) {
                _rateUiEvent.send(UserRateUiEvent.Error)
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

        is SetText -> _newRate.update { it.copy(text = event.text.orEmpty()) }
    }
}
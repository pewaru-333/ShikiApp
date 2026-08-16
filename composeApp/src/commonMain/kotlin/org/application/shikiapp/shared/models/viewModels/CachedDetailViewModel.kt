@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.application.shikiapp.shared.models.states.BaseState
import org.application.shikiapp.shared.network.response.Response
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class CachedDetailViewModel<T, D, S : BaseState<S>> : ContentDetailViewModel<D, S>() {
    private val trigger = MutableStateFlow<Response<D, Throwable>?>(null)

    override val response = trigger.flatMapLatest { trigger ->
        if (trigger != null) flowOf(trigger)
        else getSourceFlow(contentId).map { Response.Success(transformData(it)) }
    }
        .catch { e -> emit(Response.Error(e)) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    override fun tryEmit(state: Response<D, Throwable>) = trigger.tryEmit(state)

    override fun loadData() {
        viewModelScope.launch { trigger.emit(null) }
    }

    protected fun reloadData(timeout: Duration = 10.seconds, predicate: suspend (D) -> Boolean) {
        viewModelScope.launch {
            withTimeoutOrNull(timeout) {
                getSourceFlow(contentId).first { entity ->
                    predicate(transformData(entity))
                }
            }

            loadData()
        }
    }

    protected fun updateData(transform: (D) -> D) {
        val current = (response.value as? Response.Success)?.data ?: return
        trigger.tryEmit(Response.Success(transform(current)))
    }

    protected abstract fun getSourceFlow(id: Any): Flow<T>
    protected abstract suspend fun transformData(data: T): D
}
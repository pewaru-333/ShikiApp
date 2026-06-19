@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.application.shikiapp.shared.models.states.BaseState
import org.application.shikiapp.shared.network.response.Response

abstract class CachedDetailViewModel<T, D, S : BaseState<S>> : ContentDetailViewModel<D, S>() {
    private val trigger = MutableStateFlow<Response<D, Nothing>?>(null)

    override val response = trigger.flatMapLatest { trigger ->
        if (trigger != null) flowOf(trigger)
        else getSourceFlow(contentId).map { Response.Success(transformData(it)) }
    }
        .catch { e -> emit(Response.Error(e)) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    override fun loadData() = Unit

    protected abstract fun getSourceFlow(id: Any): Flow<T>
    protected abstract suspend fun transformData(data: T): D
}
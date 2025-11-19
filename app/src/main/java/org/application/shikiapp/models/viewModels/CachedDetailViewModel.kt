@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import org.application.shikiapp.network.response.Response

abstract class CachedDetailViewModel<T, D, S> : ContentDetailViewModel<D, S>() {
    private val _trigger = MutableStateFlow(false)

    override val response = combine(_response, _trigger) { response, trigger ->
        getSourceFlow(contentId)
            .transform { data ->
                emit(
                    value = if (trigger) response
                    else Response.Success(transformData(data))
                )
                _trigger.update { false }
            }
            .catch { e -> emit(Response.Error(e)) }
            .flowOn(Dispatchers.Default)
    }
        .flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    override fun loadData() = _trigger.update { true }

    protected abstract fun getSourceFlow(id: Any): Flow<T>
    protected abstract suspend fun transformData(data: T): D
}
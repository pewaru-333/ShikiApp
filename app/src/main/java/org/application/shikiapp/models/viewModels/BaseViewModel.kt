package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.application.shikiapp.network.response.Response

abstract class BaseViewModel<D, S, E>() : ViewModel() {
    protected val _response = MutableStateFlow<Response<D, Throwable>>(Response.Loading)
    open val response = _response
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    private val _state = MutableStateFlow(initState())
    open val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), initState())

    protected suspend fun emit(state: Response<D, Throwable>) = _response.emit(state)
    protected fun tryEmit(state: Response<D, Throwable>) = _response.tryEmit(state)
    protected fun updateState(update: (S) -> S) = _state.update(update)

    abstract val contentId: Any
    abstract fun initState(): S
    abstract fun loadData()
    abstract fun onEvent(event: E)
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.application.shikiapp.network.response.Response

abstract class BaseViewModel<D, S, E>() : ViewModel() {
    private val _response = MutableStateFlow<Response<D, Throwable>>(Response.Loading)
    open val response = _response
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

    private val _state = MutableStateFlow(initState())
    open val state = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), initState())

    protected suspend fun emit(state: Response<D, Throwable>) = _response.emit(state)
    protected fun updateState(update: (S) -> S) = _state.update(update)
    protected fun <T> asyncLoad(block: suspend CoroutineScope.() -> T) = viewModelScope.async(
        context = Dispatchers.IO,
        block = block
    )

    abstract fun initState(): S
    abstract fun loadData()
    abstract fun onEvent(event: E)
}
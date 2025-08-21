package org.application.shikiapp.network.response

sealed interface AsyncData<out T> {
    data class Success<out T>(val data: T) : AsyncData<T> {
        override fun getValue() = data
    }

    data object Loading : AsyncData<Nothing> {
        override fun getValue() = null
    }

    fun getValue(): T?
}
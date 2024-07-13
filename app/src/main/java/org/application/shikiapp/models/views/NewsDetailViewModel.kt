package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.News
import org.application.shikiapp.network.NetworkClient


class NewsDetailViewModel(private val newsId: Long) : ViewModel() {
    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)
    val state = _state.asStateFlow()

    init {
        getNews()
    }

    fun getNews() {
        viewModelScope.launch {
            _state.emit(NewsState.Loading)

            try {
                val news = NetworkClient.client.getTopicById(newsId)

                _state.emit(NewsState.Success(news))
            } catch (e: Exception) {
                _state.emit(NewsState.Error)
            }
        }
    }
}

sealed interface NewsState {
    data object Error : NewsState
    data object Loading : NewsState
    data class Success(val news: News) : NewsState
}
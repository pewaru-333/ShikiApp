package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Error
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Loading
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Success
import org.application.shikiapp.network.NetworkClient


class NewsDetailViewModel(private val newsId: Long) : ViewModel() {
    private val _state = MutableStateFlow<Response>(Loading)
    val state = _state.asStateFlow()

    init {
        getNews()
    }

    fun getNews() {
        viewModelScope.launch {
            _state.emit(Loading)

            try {
                val news = NetworkClient.client.getTopicById(newsId)

                _state.emit(Success(news))
            } catch (e: Exception) {
                _state.emit(Error)
            }
        }
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(val news: News) : Response
    }
}
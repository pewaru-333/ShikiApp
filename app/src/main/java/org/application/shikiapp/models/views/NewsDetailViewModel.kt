package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Error
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Loading
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Success
import org.application.shikiapp.network.NetworkClient
import retrofit2.HttpException


class NewsDetailViewModel(private val newsId: Long) : ViewModel() {
    private val _response = MutableStateFlow<Response>(Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(NewsDetailState())
    val state = _state.asStateFlow()

    init {
        getNews()
    }

    fun getNews() {
        viewModelScope.launch {
            _response.emit(Loading)

            try {
                _response.emit(Success(NetworkClient.client.getTopicById(newsId)))
            } catch (e: HttpException) {
                _response.emit(Error)
            }
        }
    }

    fun showComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = true) } }
    }

    fun hideComments() {
        viewModelScope.launch { _state.update { it.copy(showComments = false) } }
    }

    fun showImage(index: Int) {
        viewModelScope.launch { _state.update { it.copy(showImage = true, image = index) } }
    }

    fun hideImage() {
        viewModelScope.launch { _state.update { it.copy(showImage = false) } }
    }

    fun setImage(index: Int) {
        viewModelScope.launch { _state.update { it.copy(image = index) } }
    }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(val news: News) : Response
    }
}

data class NewsDetailState(
    val showComments: Boolean = false,
    val showImage: Boolean = false,
    val image: Int = 0
)
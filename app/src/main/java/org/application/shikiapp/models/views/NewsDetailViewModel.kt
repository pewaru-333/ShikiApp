package org.application.shikiapp.models.views

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Error
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Loading
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Success
import org.application.shikiapp.network.Comments
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.NewsDetail
import retrofit2.HttpException


class NewsDetailViewModel(saved: SavedStateHandle) : ViewModel() {
    private val newsId = saved.toRoute<NewsDetail>().id

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
                _response.emit(
                    Success(
                        news = NetworkClient.client.getTopicById(newsId),
                        comments = Comments.getComments(newsId, viewModelScope)
                    )
                )
            } catch (e: HttpException) {
                _response.emit(Error)
            }
        }
    }

    fun showComments() = _state.update { it.copy(showComments = true) }
    fun hideComments() = _state.update { it.copy(showComments = false) }

    fun showImage(index: Int) = _state.update { it.copy(showImage = true, image = index) }
    fun hideImage() = _state.update { it.copy(showImage = false) }
    fun setImage(index: Int) = _state.update { it.copy(image = index) }

    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(val news: News, val comments: Flow<PagingData<Comment>>) : Response
    }
}

data class NewsDetailState(
    val showComments: Boolean = false,
    val showImage: Boolean = false,
    val image: Int = 0
)
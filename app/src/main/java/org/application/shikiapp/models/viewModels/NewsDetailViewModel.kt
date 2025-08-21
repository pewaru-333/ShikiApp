package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.NewsDetailState
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.navigation.Screen


class NewsDetailViewModel(saved: SavedStateHandle) : ContentDetailViewModel<NewsDetail, NewsDetailState>() {
    private val newsId = saved.toRoute<Screen.NewsDetail>().id

    override fun initState() = NewsDetailState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val news = async { Network.topics.getTopic(newsId) }
                setCommentParams(newsId)

                emit(Response.Success(news.await().mapper(comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }

            is ContentDetailEvent.Media.ShowImage -> updateState {
                it.copy(
                    showImage = !it.showImage,
                    image = event.index
                )
            }

            is ContentDetailEvent.Media.SetImage -> updateState {
                it.copy(
                    image = event.index
                )
            }

            else -> Unit
        }
    }
}


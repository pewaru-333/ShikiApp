package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.NewsDetailEvent
import org.application.shikiapp.models.states.NewsDetailState
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.Response
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.utils.navigation.Screen


class NewsDetailViewModel(saved: SavedStateHandle) : ContentDetailViewModel<NewsDetail, NewsDetailState, NewsDetailEvent>() {
    private val newsId = saved.toRoute<Screen.NewsDetail>().id

    override fun initState() = NewsDetailState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val news = NetworkClient.news.getTopic(newsId)
                val comments = getComments(newsId)

                emit(Response.Success(news.mapper(comments)))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: NewsDetailEvent) {
        when (event) {
            is ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            is NewsDetailEvent.ShowImage -> updateState {
                it.copy(
                    showImage = !it.showImage,
                    image = event.index
                )
            }

            else -> Unit
        }
    }
}


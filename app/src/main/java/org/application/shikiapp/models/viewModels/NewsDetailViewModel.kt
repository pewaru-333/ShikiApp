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
    override val contentId = saved.toRoute<Screen.NewsDetail>().id

    override fun initState() = NewsDetailState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val news = async { Network.topics.getTopic(contentId) }
                setCommentParams(contentId)

                emit(Response.Success(news.await().mapper(comments)))
            } catch (e: Exception) {
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

            else -> Unit
        }
    }
}


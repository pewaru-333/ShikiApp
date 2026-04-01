package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.NewsDetailState
import org.application.shikiapp.shared.models.ui.NewsDetail
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.navigation.Screen


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
                setCommentParams(contentId, CommentableType.TOPIC)

                emit(Response.Success(news.await().mapper(comments)))
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        if (event is ContentDetailEvent.ToggleDialog && event.dialogState is BaseDialogState.Media.Image) {
            updateState {
                it.copy(image = event.dialogState.index)
            }
        }
    }
}


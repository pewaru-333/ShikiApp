@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.data.Comment
import org.application.shikiapp.shared.models.data.CommentToCreate
import org.application.shikiapp.shared.models.states.BaseState
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.extensions.toValue

abstract class ContentDetailViewModel<D, S: BaseState<S>> : BaseViewModel<D, S, ContentDetailEvent>() {
    private val _openLink = Channel<Unit>()
    val openLink = _openLink.receiveAsFlow()

    private val _commentEvent = Channel<Unit>()
    val commentEvent = _commentEvent.receiveAsFlow()

    private val _commentParams = MutableStateFlow<CommentParams>(CommentParams())

    val comments = _commentParams.flatMapLatest { (topicId, _, type) ->
        if (topicId == null) flowOf(
            PagingData.empty(
                sourceLoadStates = LoadStates(
                    refresh = LoadState.NotLoading(endOfPaginationReached = true),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true)
                )
            )
        ) else Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(Comment::id) { page, params ->
                    Network.topics.getComments(topicId, type, page, params.loadSize)
                }.also { commentsPagingSource = it }
            }
        ).flow
    }.map { it.map(Comment::mapper) }
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    private var commentsPagingSource: CommonPaging<Comment>? = null

    protected fun setCommentParams(id: Long?, commentableType: CommentableType) =
        _commentParams.update { CommentParams(id, commentableType) }

    protected fun sendComment(text: String, isOfftopic: Boolean) {
        val targetId = with(_commentParams.value) {
            if (commentableType == CommentableType.USER) topicId
            else contentId
        }

        viewModelScope.launch {
            updateState { it.updateSendingState(true) }

            try {
                val newComment = CommentToCreate(
                    broadcast = "false",
                    comment = CommentToCreate.Comment(
                        body = text,
                        commentableId = targetId.toString(),
                        commentableType = _commentParams.value.commentableType.toString().lowercase().replaceFirstChar { it.uppercase() },
                        isOfftopic = isOfftopic.toString()
                    )
                )

                val request = Network.profile.createComment(newComment)

                if (request.status == HttpStatusCode.Created) {
                    _commentEvent.send(Unit)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                commentsPagingSource?.invalidate()
                updateState { it.updateSendingState(false) }
            }
        }
    }

    protected fun toggleFavourite(id: Any, type: LinkedType, favoured: Boolean, kind: String = BLANK) {
        viewModelScope.launch {
            try {
                if (favoured) Network.profile.deleteFavourite(type.toValue(), id)
                else Network.profile.addFavourite(type.toValue(), id, kind)
            } catch (_: Exception) {

            } finally {
                loadData()
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.OpenLink -> viewModelScope.launch { _openLink.send(Unit) }

            is ContentDetailEvent.ToggleDialog -> updateState {
                it.updateDialogState(
                    dialogState =  event.dialogState
                )
            }

            is ContentDetailEvent.SendComment -> sendComment(event.text, event.isOfftopic)

            else -> Unit
        }
    }

    private data class CommentParams(
        val topicId: Long? = null,
        val commentableType: CommentableType = CommentableType.USER,
        val type: String = if (commentableType == CommentableType.USER) "User" else "Topic"
    )
}
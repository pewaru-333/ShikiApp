package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.extensions.toValue

abstract class ContentDetailViewModel<D, S> : BaseViewModel<D, S, ContentDetailEvent>() {
    private val _openLink = Channel<Unit>()
    val openLink = _openLink.receiveAsFlow()

    private val _commentParams = MutableStateFlow<Pair<Long?, String>>(Pair(null, "Topic"))

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = _commentParams.filterNotNull().flatMapLatest { (id, type) ->
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(Comment::id) { page, params ->
                    Network.topics.getComments(id, type, page, params.loadSize)
                }
            }
        ).flow
    }.map { it.map(Comment::mapper) }
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    protected fun setCommentParams(id: Long?, type: String = "Topic") {
        _commentParams.update {
            Pair(id, type)
        }
    }

    protected fun toggleFavourite(id: Any, type: LinkedType, favoured: Boolean, kind: String = BLANK) {
        viewModelScope.launch {
            try {
                if (favoured) Network.profile.deleteFavourite(type.toValue(), id)
                else Network.profile.addFavourite(type.toValue(), id, kind)
            } catch (_: Throwable) {

            } finally {
                loadData()
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.OpenLink -> viewModelScope.launch { _openLink.send(Unit) }

            else -> Unit
        }
    }
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.CommentsPaging
import org.application.shikiapp.utils.BLANK

open class ContentDetailViewModel<D, S, E : ContentDetailEvent> : BaseViewModel<D, S, E>() {
    override fun initState() = Any() as S

    override fun loadData() = Unit

    override fun onEvent(event: E) = Unit

    protected fun getComments(id: Long?, type: String = "Topic") = if (id == null) emptyFlow()
    else Pager(
        config = PagingConfig(pageSize = 15, enablePlaceholders = false),
        pagingSourceFactory = { CommentsPaging(id, type) }
    ).flow
        .flowOn(Dispatchers.IO)
        .map { comment ->
            val set = mutableSetOf<Long>()
            comment.filter { if (set.contains(it.id)) false else set.add(it.id) }
        }
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    protected fun toggleFavourite(id: Any, type: String, favoured: Boolean, kind: String = BLANK) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (favoured) NetworkClient.profile.deleteFavourite(type, id)
                else NetworkClient.profile.addFavourite(type, id, kind)
            } catch (_: Throwable) {

            } finally {
                loadData()
            }
        }
    }
}
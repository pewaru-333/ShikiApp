package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.utils.BLANK

abstract class ContentDetailViewModel<D, S, E : ContentDetailEvent> : BaseViewModel<D, S, E>() {
    protected fun getComments(id: Long?, type: String = "Topic") = if (id == null) emptyFlow()
    else Pager(
        config = PagingConfig(pageSize = 15, enablePlaceholders = false),
        pagingSourceFactory = {
            CommonPaging<Comment>(Comment::id) { page, params ->
                NetworkClient.topics.getComments(id, type, page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
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
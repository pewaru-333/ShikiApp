package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.extensions.toValue

abstract class ContentDetailViewModel<D, S> : BaseViewModel<D, S, ContentDetailEvent>() {
    protected fun getComments(id: Long?, type: String = "Topic") = Pager(
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
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

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
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging

class NewsViewModel : ViewModel() {
    val newsList = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            CommonPaging<News>(News::id) { page, params ->
                Network.topics.getNewsList(page, params.loadSize)
            }
        }
    ).flow
        .map { list -> list.map(News::mapper) }
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
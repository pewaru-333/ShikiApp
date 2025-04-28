package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.models.data.News
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.CommonPaging

class NewsViewModel : ViewModel() {
    val newsList = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            CommonPaging<News>(News::id) { page, params ->
                NetworkClient.topics.getNewsList(page, params.loadSize)
            }
        }
    ).flow
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
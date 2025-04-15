package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.models.data.News
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.paging.CommonPaging

class NewsViewModel : ViewModel() {
    val newsList = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = {
            CommonPaging<News>(News::id) { page, params ->
                NetworkClient.topics.getNewsList(page, params.loadSize)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
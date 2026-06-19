package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.shared.models.ui.list.News
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging

class NewsViewModel : ViewModel() {
    val newsList = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(News::id) { page, params ->
                val news = Network.topics.getNewsList(page, params.loadSize)

                coroutineScope {
                    news.map { async { it.mapper() } }.awaitAll()
                }
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
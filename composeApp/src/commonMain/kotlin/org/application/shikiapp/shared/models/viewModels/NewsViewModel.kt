package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.application.shikiapp.shared.models.ui.list.News
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging

class NewsViewModel : ViewModel() {
    private val pageSize = 15

    val newsList = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(News::id) { page, _ ->
                val news = Network.topics.getNewsList(page, pageSize)

                coroutineScope {
                    news.map { async { it.mapper() } }.awaitAll()
                }
            }
        }
    ).flow.cachedIn(viewModelScope)
}
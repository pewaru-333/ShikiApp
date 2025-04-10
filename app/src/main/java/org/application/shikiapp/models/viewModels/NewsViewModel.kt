package org.application.shikiapp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.network.paging.NewsPaging

class NewsViewModel : ViewModel() {
    val newsList = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { NewsPaging() }
    ).flow.map { news ->
        val set = mutableSetOf<Long>()
        news.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
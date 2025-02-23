package org.application.shikiapp.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.network.paging.CommentsPaging

object Comments {
    fun getComments(id: Long?, scope: CoroutineScope) = if (id == null) emptyFlow()
    else Pager(
        config = PagingConfig(pageSize = 15, enablePlaceholders = false),
        pagingSourceFactory = { CommentsPaging(id) }
    ).flow.map { comment ->
        val set = mutableSetOf<Long>()
        comment.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(scope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
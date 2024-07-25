package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import org.application.shikiapp.network.paging.CommentsPaging
import retrofit2.HttpException

class CommentViewModel(id: Long) : ViewModel() {
    val comments = Pager(PagingConfig(pageSize = 15, enablePlaceholders = false))
    { CommentsPaging(id) }.flow.map { comment ->
        val set = mutableSetOf<Long>()
        comment.filter { if (set.contains(it.id)) false else set.add(it.id) }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }
}
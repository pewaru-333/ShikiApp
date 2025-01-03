package org.application.shikiapp.models.views

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import org.application.CharacterListQuery.Data.Character
import org.application.shikiapp.network.paging.CharactersPaging
import retrofit2.HttpException

class CharacterListViewModel : FiltersViewModel<Character>() {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CharactersPaging(value) }
        ).flow.map { list ->
            val set = mutableSetOf<String>()
            list.filter { if (it.id in set) false else set.add(it.id) }
        }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }
}
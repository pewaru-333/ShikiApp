package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.network.paging.PeoplePaging
import org.application.shikiapp.utils.PeopleFilterItems.Mangaka
import org.application.shikiapp.utils.PeopleFilterItems.Producer
import org.application.shikiapp.utils.PeopleFilterItems.Seyu
import retrofit2.HttpException

class PeopleViewModel : ViewModel() {
    private val _filters = MutableStateFlow(PeopleFilters())
    val filters = _filters.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val list = _filters.debounce(300).flatMapLatest { value ->
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { PeoplePaging(value) }
        ).flow.map { list ->
            val set = mutableSetOf<String>()
            list.filter { if (it.id in set) false else set.add(it.id) }
        }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is HttpException || attempt <= 3 }

    fun setSearch(text: String) {
        viewModelScope.launch { _filters.update { it.copy(search = text) } }
    }

    fun setFlag(flag: Boolean, index: Int) {
        viewModelScope.launch {
            _filters.update {
                when (index) {
                    0 -> it.copy(
                        isSeyu = flag,
                        query = it.query.apply { if (flag) add(Seyu.title) else remove(Seyu.title) }
                    )

                    1 -> it.copy(
                        isProducer = flag,
                        query = it.query.apply { if (flag) add(Producer.title) else remove(Producer.title) }
                    )

                    else -> it.copy(
                        isMangaka = flag,
                        query = it.query.apply { if (flag) add(Mangaka.title) else remove(Mangaka.title) }
                    )
                }
            }
        }
    }
}

data class PeopleFilters(
    val search: String? = null,
    val isSeyu: Boolean? = null,
    val isProducer: Boolean? = null,
    val isMangaka: Boolean? = null,
    val query: MutableList<String> = mutableListOf()
)
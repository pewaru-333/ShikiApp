package org.application.shikiapp.models.views

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import org.application.fragment.GenresF
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.paging.RanobePaging

class RanobeListViewModel : MangaListViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            )
        ) { RanobePaging(value) }.flow
    }.onStart {
        try {
            genres = ApolloClient.getMangaGenres()
        } catch (e: Throwable) {
            emptyList<GenresF>()
        }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
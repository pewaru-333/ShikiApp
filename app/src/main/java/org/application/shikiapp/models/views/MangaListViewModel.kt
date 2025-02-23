package org.application.shikiapp.models.views

import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
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
import org.application.MangaListQuery.Data.Manga
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.paging.MangaPaging

open class MangaListViewModel : FiltersViewModel<Manga>() {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            )
        ) { MangaPaging(value) }.flow
    }.onStart {
        try {
            genres = ApolloClient.getMangaGenres()
        } catch (e: Throwable) {
            LoadState.Error(e)
        }
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
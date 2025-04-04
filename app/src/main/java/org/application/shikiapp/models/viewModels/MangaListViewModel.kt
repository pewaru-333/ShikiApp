package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
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
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }

    override val genres = ApolloClient.getMangaGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}
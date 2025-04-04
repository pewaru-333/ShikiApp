package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import org.application.AnimeListQuery.Data.Anime
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.paging.AnimePaging

class AnimeListViewModel : FiltersViewModel<Anime>() {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            )
        ) { AnimePaging(value) }.flow
    }.cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 3 }

    override val genres = ApolloClient.getAnimeGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.retryWhen
import org.application.CharacterListQuery.Data.Character
import org.application.shikiapp.network.paging.CharactersPaging

class CharacterListViewModel : FiltersViewModel<Character>() {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            )
        ) { CharactersPaging(value) }.flow
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
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
import kotlinx.coroutines.flow.retryWhen
import org.application.PeopleQuery.Data.Person
import org.application.shikiapp.network.paging.PeoplePaging

class PeopleViewModel : FiltersViewModel<Person>() {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val list = filters.debounce(300).flatMapLatest { value ->
        Pager(
            PagingConfig(
                pageSize = 10,
                initialLoadSize = 10
            )
        ) { PeoplePaging(value) }.flow
    }.cachedIn(viewModelScope)
        .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
}
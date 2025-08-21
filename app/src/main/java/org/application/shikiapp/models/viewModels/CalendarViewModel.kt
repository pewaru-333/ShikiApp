package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.events.CalendarEvent
import org.application.shikiapp.models.data.Topic
import org.application.shikiapp.models.states.AnimeCalendarState
import org.application.shikiapp.models.ui.AnimeCalendar
import org.application.shikiapp.models.ui.mappers.toAnimeContent
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.Response

class CalendarViewModel : BaseViewModel<AnimeCalendar, AnimeCalendarState, CalendarEvent>() {
    override fun initState() = AnimeCalendarState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            } else {
                return@launch
            }

            try {
                val trending = async { GraphQL.getTrending() }
                val random = async { GraphQL.getRandom() }
                val topicsUpdates = getTopicsUpdates()

                emit(
                    Response.Success(
                        AnimeCalendar(
                            trending = trending.await(),
                            random = random.await(),
                            updates = topicsUpdates
                        )
                    )
                )
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: CalendarEvent) {
        when (event) {
            CalendarEvent.Reload -> viewModelScope.launch {
                emit(Response.Loading)

                loadData()
            }

            CalendarEvent.ShowFullUpdates -> updateState { it.copy(showFullUpdates = !it.showFullUpdates) }
        }
    }

    private fun getTopicsUpdates() = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(Topic::id) { page, params ->
                Network.topics.getTopicsUpdates(page, params.loadSize)
            }
        }
    ).flow
        .map(PagingData<Topic>::toAnimeContent)
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 3 }
}
package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Topic
import org.application.shikiapp.models.ui.AnimeCalendar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.ui.mappers.toAnimeContent
import org.application.shikiapp.models.ui.mappers.toSchedule
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.extensions.distinctBy

class CalendarViewModel : BaseViewModel<AnimeCalendar, Unit, Unit>() {
    override val contentId = Any()

    override fun initState() = Unit

    override fun onEvent(event: Unit) = Unit

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            } else {
                return@launch
            }

            try {
                val (trending, random, schedule) = coroutineScope {
                    val trending = async { GraphQL.getTrending() }
                    val random = async { GraphQL.getRandom() }
                    val schedule = async { Network.content.getCalendar().toSchedule() }

                    Triple(trending, random, schedule)
                }

                emit(
                    state = Response.Success(
                        data = AnimeCalendar(
                            trending = trending.await(),
                            random = random.await(),
                            schedule = schedule.await(),
                            updates = topicsUpdates
                        )
                    )
                )
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    fun reload() {
        tryEmit(Response.Loading)
        loadData()
    }

    private val topicsUpdates = Pager(
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
        .distinctBy(Content::id)
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 3 }
}
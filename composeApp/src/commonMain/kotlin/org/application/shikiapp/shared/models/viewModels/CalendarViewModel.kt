package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.models.data.Topic
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.ui.mappers.toAnimeContent
import org.application.shikiapp.shared.models.ui.mappers.toSchedule
import org.application.shikiapp.shared.network.client.GraphQL
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging
import org.application.shikiapp.shared.network.response.Response

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

                    Triple(trending.await(), random.await(), schedule.await())
                }

                emit(
                    state = Response.Success(
                        data = AnimeCalendar(
                            trending = trending,
                            random = random,
                            schedule = schedule,
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
            CommonPaging(Content::id) { page, params ->
                Network.topics.getTopicsUpdates(page, params.loadSize)
                    .map(Topic::toAnimeContent)
            }
        }
    ).flow.cachedIn(viewModelScope)
}
package org.application.shikiapp.models.ui

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.ui.list.Content

@Stable
data class AnimeCalendar(
    val trending: List<Content>,
    val random: List<Content>,
    val schedule: List<Schedule>,
    val updates: Flow<PagingData<Content>>
) {
    data class Schedule(
        val date: String,
        val animes: List<Content>
    )
}

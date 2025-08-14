package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.ui.list.Content

data class AnimeCalendar(
    val trending: List<Content>,
    val random: List<Content>,
    val updates: Flow<PagingData<Content>>
)

package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.ui.list.ShortContent

data class AnimeCalendar(
    val trending: List<ShortContent>,
    val updates: Flow<PagingData<Content>>
)

package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content

data class AnimeCalendar(
    val trending: List<BasicContent>,
    val updates: Flow<PagingData<Content>>
)

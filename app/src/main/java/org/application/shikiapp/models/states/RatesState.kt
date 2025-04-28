package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import org.application.shikiapp.utils.enums.WatchStatus

data class RatesState(
    val tab: WatchStatus = WatchStatus.PLANNED,
    val showEditRate: Boolean = false,
    val listStates: Map<WatchStatus, LazyListState> = WatchStatus.entries.associateWith { LazyListState() }
)

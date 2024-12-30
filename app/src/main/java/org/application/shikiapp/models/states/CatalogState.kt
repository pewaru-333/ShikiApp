package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CatalogItems

data class CatalogState(
    val menu: CatalogItems = CatalogItems.ANIME,
    val search: String = BLANK,
    val showFiltersA: Boolean = false,
    val showFiltersM: Boolean = false,
    val showFiltersR: Boolean = false,
    val showFiltersP: Boolean = false,
    val listA: LazyListState = LazyListState(),
    val listM: LazyListState = LazyListState(),
    val listR: LazyListState = LazyListState(),
    val listC: LazyListState = LazyListState(),
    val listP: LazyListState = LazyListState(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)
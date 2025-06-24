package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.CatalogItem

data class CatalogState(
    val menu: CatalogItem = CatalogItem.ANIME,
    val search: String = BLANK,
    val showFiltersA: Boolean = false,
    val showFiltersM: Boolean = false,
    val showFiltersR: Boolean = false,
    val showFiltersP: Boolean = false,
    val listStates: Map<CatalogItem, LazyListState> = CatalogItem.entries.associateWith { LazyListState() },
    val gridStates: Map<CatalogItem, LazyGridState> = CatalogItem.entries.associateWith { LazyGridState() },
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)
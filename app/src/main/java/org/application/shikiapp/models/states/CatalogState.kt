package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.CatalogItems

data class CatalogState(
    val menu: CatalogItems = CatalogItems.ANIME,
    val search: String = BLANK,
    val showFiltersA: Boolean = false,
    val showFiltersM: Boolean = false,
    val showFiltersR: Boolean = false,
    val showFiltersP: Boolean = false,
    val listStates: Map<CatalogItems, LazyListState> = CatalogItems.entries.associateWith { LazyListState() },
    val gridStates: Map<CatalogItems, LazyGridState> = CatalogItems.entries.associateWith { LazyGridState() },
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)
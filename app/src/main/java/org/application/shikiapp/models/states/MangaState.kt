@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density

data class MangaState(
    val showComments: Boolean = false,
    val showSheet: Boolean = false,
    val showRelated: Boolean = false,
    val showCharacters: Boolean = false,
    val showAuthors: Boolean = false,
    val showRate: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false,
    val showLinks: Boolean = false,
    val sheetBottom: SheetState = SheetState(false, Density(1f)),
    val sheetLinks: SheetState = SheetState(false, Density(1f)),
    val lazyCharacters: LazyListState = LazyListState(),
    val lazyAuthors: LazyListState = LazyListState(),
    val lazySimilar: LazyListState = LazyListState()
)
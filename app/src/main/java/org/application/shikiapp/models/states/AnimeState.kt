@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density

data class AnimeState(
    val lazyAuthors: LazyListState = LazyListState(),
    val lazyCharacters: LazyListState = LazyListState(),
    val lazySimilar: LazyListState = LazyListState(),
    val screenshot: Int = 0,
    val sheetBottom: SheetState = SheetState(false, Density(1f)),
    val sheetColumn: SheetState = SheetState(false, Density(1f)),
    val sheetLinks: SheetState = SheetState(false, Density(1f)),
    val showAuthors: Boolean = false,
    val showCharacters: Boolean = false,
    val showComments: Boolean = false,
    val showFandubbers: Boolean = false,
    val showFansubbers: Boolean = false,
    val showLinks: Boolean = false,
    val showRate: Boolean = false,
    val showRelated: Boolean = false,
    val showScreenshot: Boolean = false,
    val showScreenshots: Boolean = false,
    val showSheet: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false,
    val showVideo: Boolean = false
)

val AnimeState.showSheetContent: Boolean
    get() = showFansubbers || showFandubbers
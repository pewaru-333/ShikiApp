@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density

data class CharacterState(
    val sheetState: SheetState = SheetState(false, Density(1f)),
    val showComments: Boolean = false,
    val showRelated: Boolean = false,
    val showSeyu: Boolean = false,
    val showSheet: Boolean = false,
)
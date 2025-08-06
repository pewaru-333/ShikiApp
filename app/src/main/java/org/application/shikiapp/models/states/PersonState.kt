@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density

data class PersonState(
    val sheetState: SheetState = SheetState(false, Density(1f)),
    val showCharacters: Boolean = false,
    val showComments: Boolean = false,
    val showSheet: Boolean = false,
    val showWorks: Boolean = false
)
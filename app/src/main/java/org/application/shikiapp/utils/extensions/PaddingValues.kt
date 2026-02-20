package org.application.shikiapp.utils.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

fun PaddingValues.toContent() = PaddingValues(
    start = 8.dp,
    top = calculateTopPadding(),
    end = 8.dp,
    bottom = 0.dp
)
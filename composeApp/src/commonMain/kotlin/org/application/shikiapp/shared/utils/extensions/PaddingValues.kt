package org.application.shikiapp.shared.utils.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun PaddingValues.toContent() = PaddingValues(
    start = 8.dp,
    top = calculateTopPadding(),
    end = 8.dp,
    bottom = calculateBottomPadding()
)

fun PaddingValues.toContentLarge() = PaddingValues(
    start = 16.dp,
    top = calculateTopPadding(),
    end = 16.dp,
    bottom = calculateBottomPadding()
)

@Composable
fun PaddingValues.add(pad: PaddingValues, ld: LayoutDirection = LocalLayoutDirection.current) =
    PaddingValues(
        start = calculateStartPadding(ld) + pad.calculateStartPadding(ld),
        top = calculateTopPadding() + pad.calculateTopPadding(),
        end = calculateEndPadding(ld) + pad.calculateEndPadding(ld),
        bottom = calculateBottomPadding() + pad.calculateBottomPadding()
    )
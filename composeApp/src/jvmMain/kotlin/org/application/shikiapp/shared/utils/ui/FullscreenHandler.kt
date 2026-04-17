package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.staticCompositionLocalOf
import org.application.shikiapp.shared.utils.BLANK

val LocalFullscreenHandler = staticCompositionLocalOf<FullscreenHandler> {
    error(BLANK)
}

data class FullscreenHandler(
    val isFullscreen: Boolean,
    val toggle: () -> Unit
)
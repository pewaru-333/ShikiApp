package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import org.application.shikiapp.shared.utils.BLANK

val LocalFullscreenHandler = staticCompositionLocalOf<FullscreenHandler> {
    error(BLANK)
}

class FullscreenHandler(private val windowState: WindowState) {
    private var size = windowState.size
    private var position = windowState.position

    val isFullscreen: Boolean
        get() = windowState.placement == WindowPlacement.Fullscreen

    fun toggle() = if (isFullscreen) {
        windowState.placement = WindowPlacement.Floating
        windowState.position = position
        windowState.size = size
    } else {
        size = windowState.size
        position = windowState.position
        windowState.placement = WindowPlacement.Fullscreen
    }
}
@file:OptIn(ExperimentalComposeUiApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.v2.WindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.utils.BLANK

val LocalWindowManager = staticCompositionLocalOf<WindowManager> {
    error(BLANK)
}

@Stable
class WindowManager(private val windowState: WindowState, private val scope: CoroutineScope) {
    private var bounds by mutableStateOf(DpRect(DpOffset.Zero, DpSize.Zero))
    private var placement by mutableStateOf(WindowPlacement.Maximized)

    private var counter = 0

    val isFullscreen: Boolean
        get() = windowState.placement == WindowPlacement.Fullscreen

    fun toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            bounds = windowState.bounds
            placement = windowState.placement

            windowState.requestPlacement(WindowPlacement.Fullscreen)
        }
    }

    fun exitFullscreen() {
        if (!isFullscreen) return

        windowState.requestBounds { bounds }
        windowState.requestPlacement(WindowPlacement.Floating)

        scope.launch {
            snapshotFlow { windowState.placement }
                .first { it == WindowPlacement.Floating }
                .let {
                    if (placement == WindowPlacement.Maximized) {
                        windowState.requestPlacement(WindowPlacement.Maximized)
                    } else {
                        windowState.requestSize(resizeFloating())
                    }
                }
        }
    }

    private fun resizeFloating() = with(bounds.size) {
        if (++counter % 2 == 0) copy(width + 1.dp, height + 1.dp)
        else copy(width - 1.dp, height - 1.dp)
    }
}

@Composable
fun rememberWindowManager(windowState: WindowState): WindowManager {
    val scope = rememberCoroutineScope()

    return remember(windowState, scope) { WindowManager(windowState, scope) }
}
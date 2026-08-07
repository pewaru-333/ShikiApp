@file:OptIn(ExperimentalComposeUiApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.v2.WindowState
import org.application.shikiapp.shared.utils.BLANK
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

val LocalWindowManager = staticCompositionLocalOf<WindowManager> {
    error(BLANK)
}

class WindowManager(val windowState: WindowState, private val window: ComposeWindow) {
    private var size = DpSize.Unspecified
    private var position = DpOffset.Unspecified
    private var placement = WindowPlacement.Maximized

    val isFullscreen: Boolean
        get() = windowState.placement == WindowPlacement.Fullscreen

    fun toggleFullscreen() {
        if (isFullscreen) exitFullscreen() else enterFullscreen()
    }

    fun enterFullscreen() {
        if (isFullscreen) return

        size = windowState.size
        position = windowState.position
        placement = windowState.placement

        windowState.requestPlacement(WindowPlacement.Fullscreen)
    }

    fun exitFullscreen() {
        if (!isFullscreen) return

        windowState.requestPlacement(WindowPlacement.Floating)

        window.addComponentListener(componentListener)
    }

    private val componentListener = object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent) {
            window.removeComponentListener(this)

            if (placement == WindowPlacement.Maximized) {
                windowState.requestPlacement(WindowPlacement.Maximized)
            } else {
                windowState.requestPosition(position)
                windowState.requestSize(size)
            }
        }
    }
}

@Composable
fun rememberWindowManager(windowState: WindowState, window: ComposeWindow) =
    remember(windowState, window) { WindowManager(windowState, window) }
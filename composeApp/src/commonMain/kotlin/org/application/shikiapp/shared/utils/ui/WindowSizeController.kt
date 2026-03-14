package org.application.shikiapp.shared.utils.ui

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass
import org.application.shikiapp.shared.utils.enums.WindowSize

interface IWindowSize {
    val windowSize: WindowSize
    val isCompact: Boolean
}

@Composable
fun rememberWindowSize(): IWindowSize {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass

    val windowSize = when {
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> WindowSize.COMPACT
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> WindowSize.MEDIUM
        else -> WindowSize.LARGE
    }

    return remember(windowSize) {
        object : IWindowSize {
            override val windowSize = windowSize
            override val isCompact = windowSize == WindowSize.COMPACT
        }
    }
}
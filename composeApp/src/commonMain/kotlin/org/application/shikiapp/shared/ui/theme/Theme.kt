@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.EdgeToEdge
import org.application.shikiapp.shared.utils.enums.Theme
import org.application.shikiapp.shared.utils.platformColorScheme

@Composable
fun Theme(content: @Composable () -> Unit) {
    val theme by Preferences.theme.collectAsStateWithLifecycle()
    val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle()
    val colorPalette by Preferences.colorPaletteFlow.collectAsStateWithLifecycle()

    val darkTheme = when (theme) {
        Theme.LIGHT -> false
        Theme.DARK, Theme.DARK_AMOLED -> true
        else -> isSystemInDarkTheme()
    }

    val baseColors = platformColorScheme(darkTheme, dynamicColors)
        ?: colorPalette.getColorScheme(darkTheme)

    val colors = if (theme != Theme.DARK_AMOLED) baseColors else baseColors.copy(
        surface = Color.Black,
        background = Color.Black,
        surfaceContainerLow = Color.Black,
        surfaceContainerLowest = Color.Black
    )

    EdgeToEdge(darkTheme, theme == Theme.DARK_AMOLED)

    MaterialExpressiveTheme(colorScheme = colors, content = content)
}
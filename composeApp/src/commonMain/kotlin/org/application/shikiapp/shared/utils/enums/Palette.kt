package org.application.shikiapp.shared.utils.enums

import org.application.shikiapp.shared.ui.theme.Color
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.palette_default
import shikiapp.composeapp.generated.resources.palette_forest
import shikiapp.composeapp.generated.resources.palette_graphite
import shikiapp.composeapp.generated.resources.palette_night
import shikiapp.composeapp.generated.resources.palette_ocean
import shikiapp.composeapp.generated.resources.palette_sakura
import shikiapp.composeapp.generated.resources.palette_sunset

enum class Palette(val title: StringResource) {
    DEFAULT(Res.string.palette_default), // FF6750A4
    SAKURA(Res.string.palette_sakura), // FF984061
    OCEAN(Res.string.palette_ocean), // FF006A6A
    FOREST(Res.string.palette_forest), // FF386A20
    SUNSET(Res.string.palette_sunset), // FF9C4100
    NIGHT(Res.string.palette_night), // FF4355B9
    GRAPHITE(Res.string.palette_graphite); // FF5E5E5E

    fun getColorScheme(isDark: Boolean) = when (this) {
        DEFAULT -> if (isDark) Color.DefaultDark else Color.DefaultLight
        GRAPHITE -> if (isDark) Color.GraphiteDark else Color.GraphiteLight
        NIGHT -> if (isDark) Color.NightDark else Color.NightLight
        SUNSET -> if (isDark) Color.SunsetDark else Color.SunsetLight
        FOREST -> if (isDark) Color.ForestDark else Color.ForestLight
        OCEAN -> if (isDark) Color.OceanDark else Color.OceanLight
        SAKURA -> if (isDark) Color.SakuraDark else Color.SakuraLight
    }
}
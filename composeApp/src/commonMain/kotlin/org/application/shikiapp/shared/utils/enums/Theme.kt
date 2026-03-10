package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.preference_theme_dark
import shikiapp.composeapp.generated.resources.preference_theme_dark_amoled
import shikiapp.composeapp.generated.resources.preference_theme_light
import shikiapp.composeapp.generated.resources.preference_theme_system

enum class Theme(val title: StringResource) {
    SYSTEM(Res.string.preference_theme_system),
    LIGHT(Res.string.preference_theme_light),
    DARK(Res.string.preference_theme_dark),
    DARK_AMOLED(Res.string.preference_theme_dark_amoled)
}
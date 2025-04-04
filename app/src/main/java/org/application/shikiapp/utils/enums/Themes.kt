package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Themes(@StringRes val title: Int) {
    SYSTEM(R.string.preference_theme_system),
    LIGHT(R.string.preference_theme_light),
    DARK(R.string.preference_theme_dark)
}
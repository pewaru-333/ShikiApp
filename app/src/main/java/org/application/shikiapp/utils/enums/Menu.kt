package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.utils.navigation.Screen

enum class Menu(val route: Screen, @StringRes val title: Int, val icon: Int) {
    CATALOG(Screen.Catalog(), R.string.text_catalog, R.drawable.vector_home),
    NEWS(Screen.News, R.string.text_news, R.drawable.vector_news),
    CALENDAR(Screen.Calendar, R.string.text_calendar, R.drawable.vector_calendar),
    PROFILE(Screen.Profile, R.string.text_profile, R.drawable.vector_character),
    SETTINGS(Screen.Settings, R.string.text_settings, R.drawable.vector_settings)
}
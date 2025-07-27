package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.utils.navigation.Screen

enum class Menu(val route: Screen, @StringRes val title: Int, val icon: Int) {
    CATALOG(Screen.Catalog(), R.string.text_catalog, R.drawable.vector_compass),
    CALENDAR(Screen.Calendar, R.string.text_calendar, R.drawable.vector_calendar),
    NEWS(Screen.News, R.string.text_news, R.drawable.vector_news),
    LISTS(Screen.UserRates(editable = true), R.string.text_lists, R.drawable.vector_bookmark),
    PROFILE(Screen.Profile, R.string.text_profile, R.drawable.vector_character)
}
package org.application.shikiapp.shared.utils.enums

import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_calendar
import shikiapp.composeapp.generated.resources.text_catalog
import shikiapp.composeapp.generated.resources.text_lists
import shikiapp.composeapp.generated.resources.text_news
import shikiapp.composeapp.generated.resources.text_profile
import shikiapp.composeapp.generated.resources.vector_bookmark
import shikiapp.composeapp.generated.resources.vector_calendar
import shikiapp.composeapp.generated.resources.vector_character
import shikiapp.composeapp.generated.resources.vector_compass
import shikiapp.composeapp.generated.resources.vector_news

enum class Menu(val route: Screen, val title: StringResource, val icon: DrawableResource) {
    CATALOG(Screen.Catalog(), Res.string.text_catalog, Res.drawable.vector_compass),
    CALENDAR(Screen.Calendar, Res.string.text_calendar, Res.drawable.vector_calendar),
    NEWS(Screen.News, Res.string.text_news, Res.drawable.vector_news),
    LISTS(Screen.UserRates(editable = true), Res.string.text_lists, Res.drawable.vector_bookmark),
    PROFILE(Screen.Profile, Res.string.text_profile, Res.drawable.vector_character)
}
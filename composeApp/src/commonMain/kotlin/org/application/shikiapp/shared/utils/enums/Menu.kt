package org.application.shikiapp.shared.utils.enums

import androidx.compose.ui.graphics.vector.ImageVector
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_calendar
import shikiapp.composeapp.generated.resources.text_catalog
import shikiapp.composeapp.generated.resources.text_lists
import shikiapp.composeapp.generated.resources.text_news
import shikiapp.composeapp.generated.resources.text_profile

enum class Menu(val route: Screen, val title: StringResource, val icon: ImageVector) {
    CATALOG(Screen.Catalog(), Res.string.text_catalog, Icons.Compass),
    CALENDAR(Screen.Calendar, Res.string.text_calendar, Icons.Calendar),
    NEWS(Screen.News, Res.string.text_news, Icons.News),
    LISTS(Screen.UserRates(editable = true), Res.string.text_lists, Icons.Bookmark),
    PROFILE(Screen.Profile, Res.string.text_profile, Icons.Character)
}
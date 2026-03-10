package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_clubs
import shikiapp.composeapp.generated.resources.text_favourite
import shikiapp.composeapp.generated.resources.text_friends
import shikiapp.composeapp.generated.resources.text_history

enum class UserMenu(val title: StringResource) {
    FRIENDS(Res.string.text_friends), FAVOURITE(Res.string.text_favourite),
    CLUBS(Res.string.text_clubs), HISTORY(Res.string.text_history)
}
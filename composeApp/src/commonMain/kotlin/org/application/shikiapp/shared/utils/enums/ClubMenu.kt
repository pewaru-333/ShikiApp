package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_members
import shikiapp.composeapp.generated.resources.text_pictures
import shikiapp.composeapp.generated.resources.text_ranobe

enum class ClubMenu(val title: StringResource) {
    ANIME(Res.string.text_anime), CHARACTERS(Res.string.text_characters),
    MANGA(Res.string.text_manga), MEMBERS(Res.string.text_members),
    RANOBE(Res.string.text_ranobe), IMAGES(Res.string.text_pictures)
}
package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_mangakas
import shikiapp.composeapp.generated.resources.text_others
import shikiapp.composeapp.generated.resources.text_people
import shikiapp.composeapp.generated.resources.text_ranobe
import shikiapp.composeapp.generated.resources.text_seyu

enum class FavouriteItem(val title: StringResource, val linkedType: LinkedType) {
    ANIME(Res.string.text_anime, LinkedType.ANIME),
    MANGA(Res.string.text_manga, LinkedType.MANGA),
    RANOBE(Res.string.text_ranobe, LinkedType.RANOBE),
    CHARACTERS(Res.string.text_characters, LinkedType.CHARACTER),
    PEOPLE(Res.string.text_people, LinkedType.PERSON),
    MANGAKAS(Res.string.text_mangakas, LinkedType.PERSON),
    SEYU(Res.string.text_seyu, LinkedType.PERSON),
    OTHERS(Res.string.text_others, LinkedType.PERSON)
}
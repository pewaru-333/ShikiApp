package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

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
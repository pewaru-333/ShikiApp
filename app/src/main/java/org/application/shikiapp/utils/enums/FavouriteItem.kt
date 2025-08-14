package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class FavouriteItem(@StringRes val title: Int, val linkedType: LinkedType) {
    ANIME(R.string.text_anime, LinkedType.ANIME),
    MANGA(R.string.text_manga, LinkedType.MANGA),
    RANOBE(R.string.text_ranobe, LinkedType.RANOBE),
    CHARACTERS(R.string.text_characters, LinkedType.CHARACTER),
    PEOPLE(R.string.text_people, LinkedType.PERSON),
    MANGAKAS(R.string.text_mangakas, LinkedType.PERSON),
    SEYU(R.string.text_seyu, LinkedType.PERSON),
    OTHERS(R.string.text_others, LinkedType.PERSON)
}
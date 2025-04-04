package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class FavouriteItems(@StringRes val title: Int) {
    ANIME(R.string.text_anime),
    MANGA(R.string.text_manga),
    RANOBE(R.string.text_ranobe),
    CHARACTERS(R.string.text_characters),
    PEOPLE(R.string.text_people),
    MANGAKAS(R.string.text_mangakas),
    SEYU(R.string.text_seyu),
    OTHERS(R.string.text_others)
}
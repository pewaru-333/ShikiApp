package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class CatalogItems(@StringRes val title: Int, val icon: Int) {
    ANIME(R.string.text_anime, R.drawable.vector_anime),
    MANGA(R.string.text_manga, R.drawable.vector_manga),
    RANOBE(R.string.text_ranobe, R.drawable.vector_ranobe),
    CHARACTERS(R.string.text_characters, R.drawable.vector_character),
    PEOPLE(R.string.text_people, R.drawable.vector_person)
}
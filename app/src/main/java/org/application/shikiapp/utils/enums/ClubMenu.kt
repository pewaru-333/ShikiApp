package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class ClubMenu(@StringRes val title: Int) {
    ANIME(R.string.text_anime), CHARACTERS(R.string.text_characters),
    MANGA(R.string.text_manga), MEMBERS(R.string.text_members),
    RANOBE(R.string.text_ranobe), IMAGES(R.string.text_pictures)
}
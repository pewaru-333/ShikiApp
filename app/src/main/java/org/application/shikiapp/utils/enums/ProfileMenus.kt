package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class ProfileMenus(@StringRes val title: Int) {
    FRIENDS(R.string.text_friends),
    CLUBS(R.string.text_clubs),
    ACHIEVEMENTS(R.string.text_achievements)
}
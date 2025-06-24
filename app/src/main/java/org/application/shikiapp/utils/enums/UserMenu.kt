package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class UserMenu(@StringRes val title: Int) {
    FRIENDS(R.string.text_friends), FAVOURITE(R.string.text_favourite),
    CLUBS(R.string.text_clubs), HISTORY(R.string.text_history)
}
package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class PeopleFilterItems(@StringRes val title: Int) {
    SEYU(R.string.text_seyu),
    PRODUCER(R.string.text_producer),
    MANGAKA(R.string.text_mangaka)
}
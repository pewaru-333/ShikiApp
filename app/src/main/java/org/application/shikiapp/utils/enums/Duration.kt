package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Duration(@StringRes val title: Int) {
    S(R.string.duration_S),
    D(R.string.duration_D),
    F(R.string.duration_F)
}
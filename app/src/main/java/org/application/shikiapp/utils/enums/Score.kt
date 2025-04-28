package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Score(val score: Int, @StringRes val title: Int) {
    ZERO(0, R.string.blank),
    ONE(1, R.string.score_1),
    TWO(2, R.string.score_2),
    THREE(3, R.string.score_3),
    FOUR(4, R.string.score_4),
    FIVE(5, R.string.score_5),
    SIX(6, R.string.score_6),
    SEVEN(7, R.string.score_7),
    EIGHT(8, R.string.score_8),
    NINE(9, R.string.score_9),
    TEN(10, R.string.score_10)
}
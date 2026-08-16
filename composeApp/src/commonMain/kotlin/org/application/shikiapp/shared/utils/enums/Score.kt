package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class Score(val score: Int, val title: StringResource) {
    ZERO(0, Res.string.blank),
    ONE(1, Res.string.score_1),
    TWO(2, Res.string.score_2),
    THREE(3, Res.string.score_3),
    FOUR(4, Res.string.score_4),
    FIVE(5, Res.string.score_5),
    SIX(6, Res.string.score_6),
    SEVEN(7, Res.string.score_7),
    EIGHT(8, Res.string.score_8),
    NINE(9, Res.string.score_9),
    TEN(10, Res.string.score_10)
}
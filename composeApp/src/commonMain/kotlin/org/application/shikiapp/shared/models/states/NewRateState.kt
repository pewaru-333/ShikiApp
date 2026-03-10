package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.Score
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank

data class NewRateState(
    val id: String = BLANK,
    val userId: Long = Preferences.userId,
    val targetId: Long = 0L,
    val targetType: String = BLANK,
    val status: String? = null,
    val statusName: StringResource = Res.string.blank,
    val score: Score? = null,
    val scoreName: String? = null,
    val chapters: String? = null,
    val episodes: String? = null,
    val volumes: String? = null,
    val rewatches: String? = null,
    val text: String? = null
)
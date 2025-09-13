package org.application.shikiapp.models.states

import androidx.annotation.StringRes
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.Score

data class NewRateState(
    val id: String = BLANK,
    val userId: Long = Preferences.userId,
    val targetId: Long = 0L,
    val targetType: String = BLANK,
    val status: String? = null,
    @StringRes val statusName: Int = R.string.blank,
    val score: Score? = null,
    val scoreName: String? = null,
    val chapters: String? = null,
    val episodes: String? = null,
    val volumes: String? = null,
    val rewatches: String? = null,
    val text: String? = null
)
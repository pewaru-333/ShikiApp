package org.application.shikiapp.shared.utils.ui.subtitles.models

import androidx.compose.runtime.Immutable

@Immutable
data class SubtitleCue(private val startTime: Long, private val endTime: Long, val text: String) {
    fun isActive(currentTimeMs: Long) = currentTimeMs in startTime..endTime
}

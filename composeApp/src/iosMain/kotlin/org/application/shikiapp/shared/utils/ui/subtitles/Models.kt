package org.application.shikiapp.shared.utils.ui.subtitles

import androidx.compose.runtime.Immutable

@Immutable
data class SubtitleCue(val startTime: Long, val endTime: Long, val text: String) {
    fun isActive(currentTimeMs: Long) = currentTimeMs in startTime..endTime
}

@Immutable
data class SubtitleCueList(val cues: List<SubtitleCue> = emptyList()) {
    fun getActiveCues(currentTimeMs: Long) = cues.filter { it.isActive(currentTimeMs) }
}
package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.models.ui.VideoVoice

data class WatchState(
    val isLoading: Boolean = false,
    val isWatching: Boolean = false,
    val isVideoLoading: Boolean = false,
    val voices: List<VideoVoice> = emptyList(),
    val qualityList: List<Int> = emptyList(),
    val videoUrl: String? = null,
    val currentVoice: VideoVoice? = null,
    val currentEpisode: Int? = null,
    val currentQuality: Int = 720
)
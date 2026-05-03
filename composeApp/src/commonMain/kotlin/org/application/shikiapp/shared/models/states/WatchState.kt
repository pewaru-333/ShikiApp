package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice

data class WatchState(
    val isLoading: Boolean = false,
    val isWatching: Boolean = false,
    val isVideoLoading: Boolean = false,
    val sources: List<VideoSourceData> = emptyList(),
    val currentSource: VideoSourceData? = null,
    val currentVoice: VideoVoice? = null,
    val currentEpisode: Int? = null,
    val audioTrackIndex: Int? = null,
    val qualityList: List<Int> = emptyList(),
    val subtitles: List<SubtitleTrack> = emptyList(),
    val videoHeaders: Map<String, String> = emptyMap(),
    val videoUrl: String? = null,
    val fallbackUrls: List<String> = emptyList(),
    val currentQuality: Int? = null
)
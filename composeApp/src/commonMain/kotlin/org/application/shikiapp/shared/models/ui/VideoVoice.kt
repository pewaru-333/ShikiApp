package org.application.shikiapp.shared.models.ui

import org.application.shikiapp.shared.utils.enums.VideoSource

data class EpisodeModel(
    val number: Int,
    val link: String,
    val audioIndex: Int? = null,
    val screenshot: String? = null
)

data class SubtitleTrack(
    val name: String,
    val url: String
)

data class VideoVoice(
    val id: Int,
    val title: String,
    val hasDubbers: Boolean,
    val hasSubtitles: Boolean,
    val episodes: List<EpisodeModel>,
    val quality: String?,
    val lastEpisode: Int
) {
    val episodesCount: Int
        get() = episodes.size
}

data class VideoSourceData(
    val type: VideoSource,
    val voices: List<VideoVoice>
)

data class PlaylistResult(
    val url: String,
    val fallbackUrls: List<String> = emptyList(),
    val qualityList: List<Int> = emptyList(),
    val subtitles: List<SubtitleTrack> = emptyList(),
    val headers: Map<String, String> = emptyMap()
)

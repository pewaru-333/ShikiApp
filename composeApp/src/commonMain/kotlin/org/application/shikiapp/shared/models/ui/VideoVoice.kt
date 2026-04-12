package org.application.shikiapp.shared.models.ui

import org.jetbrains.compose.resources.StringResource

data class EpisodeModel(
    val number: Int,
    val link: String,
    val screenshot: String?
)

data class VideoVoice(
    val id: Int,
    val title: String,
    val type: StringResource,
    val isSubtitles: Boolean,
    val link: String,
    val episodes: List<EpisodeModel>,
    val quality: String?,
    val lastEpisode: Int
) {
    val episodesCount: Int
        get() = episodes.size
}

data class KodikPlaylistResult(
    val url: String,
    val qualityList: List<Int>
)

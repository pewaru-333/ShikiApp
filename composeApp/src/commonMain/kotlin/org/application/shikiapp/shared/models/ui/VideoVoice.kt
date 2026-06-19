package org.application.shikiapp.shared.models.ui

import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.VideoSource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_turned_off_short

data class EpisodeModel(
    val number: Int,
    val link: String,
    val audioIndex: Int? = null,
    val screenshot: String? = null
)

data class SubtitleTrack(
    val name: String,
    val url: String
) {
    companion object {
        suspend fun off() = SubtitleTrack(
            url = BLANK,
            name = ResourceText.StringResource(Res.string.text_turned_off_short).asString()
        )
    }
}

data class VideoVoice(
    val id: Int,
    val title: ResourceText,
    val hasDubbers: Boolean,
    val hasSubtitles: Boolean,
    val episodes: List<EpisodeModel>,
    val quality: ResourceText?,
    val lastEpisode: Int
) {
    val episodesCount: Int
        get() = episodes.size

    val hasEpisodes: Boolean
        get() = episodesCount > 0
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
package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.utils.enums.VideoSource

data class WatchState(
    val isLoading: Boolean = false,
    val isWatching: Boolean = false,
    val isEpisodesLoading: Boolean = false,
    val isVideoLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val sources: List<VideoSourceData> = emptyList(),
    val episodeVoices: List<VideoVoice>? = null,
    val currentSource: VideoSourceData? = null,
    val currentVoice: VideoVoice? = null,
    val currentEpisode: Int? = null,
    val selectedAnimelibEpisode: EpisodeModel? = null,
    val audioTrackIndex: Int? = null,
    val qualityList: List<Int> = emptyList(),
    val subtitles: List<SubtitleTrack> = emptyList(),
    val videoHeaders: Map<String, String> = emptyMap(),
    val videoUrl: String? = null,
    val fallbackUrls: List<String> = emptyList(),
    val currentQuality: Int? = null
)

val WatchState.isLib: Boolean
    get() = currentSource?.type == VideoSource.ANIMELIB

val WatchState.showIconLogout: Boolean
    get() = isLib && Preferences.libToken != null

val WatchState.voices: List<VideoVoice>
    get() = if (isLib) episodeVoices.orEmpty() else currentSource?.voices.orEmpty()

val WatchState.episodes: List<EpisodeModel>
    get() = if (isLib) currentSource?.voices?.firstOrNull()?.episodes.orEmpty()
    else currentVoice?.episodes.orEmpty()
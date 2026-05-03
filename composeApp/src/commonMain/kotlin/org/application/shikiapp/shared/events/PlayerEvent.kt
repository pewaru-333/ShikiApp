package org.application.shikiapp.shared.events

import org.application.shikiapp.shared.models.ui.EpisodeModel

sealed interface PlayerEvent {
    data object Play : PlayerEvent
    data object Pause : PlayerEvent

    data object MarkEpisodeWatched : PlayerEvent

    data class SelectEpisode(val number: Int) : PlayerEvent


    data class UpdateProgress(val currentTime: Float, val totalTime: Float) : PlayerEvent
    data class Seek(val seconds: Float) : PlayerEvent

    data class ChangeQuality(val quality: Int) : PlayerEvent
    data class OnAutoQualityChanged(val quality: Int, val qualityList: List<Int>) : PlayerEvent

    data class LoadVideo(val episodeModel: EpisodeModel) : PlayerEvent
    data class LoadFallback(val url: String) : PlayerEvent
}
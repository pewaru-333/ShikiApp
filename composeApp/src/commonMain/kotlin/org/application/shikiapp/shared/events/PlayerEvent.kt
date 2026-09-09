package org.application.shikiapp.shared.events

import org.application.shikiapp.shared.models.ui.EpisodeModel

sealed interface PlayerEvent {
    data object Play : PlayerEvent
    data object Pause : PlayerEvent
    data object Ended : PlayerEvent

    data class SelectEpisode(val number: Int) : PlayerEvent

    data class ChangeQuality(val quality: Int) : PlayerEvent

    sealed interface Command {
        data class LoadVideo(val episodeModel: EpisodeModel) : Command
        data class LoadQuality(val url: String) : Command
    }
}
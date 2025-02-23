package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Calendar(
    @SerialName("next_episode") val nextEpisode: Int,
    @SerialName("next_episode_at") val nextEpisodeAt: String,
    @SerialName("duration") val duration: Int?,
    @SerialName("anime") val anime: AnimeBasic
)
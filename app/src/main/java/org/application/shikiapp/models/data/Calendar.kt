package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Calendar(
    @Json(name = "next_episode") val nextEpisode: Int,
    @Json(name = "next_episode_at") val nextEpisodeAt: String,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "anime") val anime: AnimeShort
)
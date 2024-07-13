package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stats(
    @Json(name = "statuses") val statuses: Statuses,
    @Json(name = "full_statuses") val fullStatuses: Statuses,
    @Json(name = "scores") val scores: Scores,
    @Json(name = "types") val types: Types,
    @Json(name = "ratings") val ratings: Ratings,
    @Json(name = "has_anime?") val hasAnime: Boolean,
    @Json(name = "has_manga?") val hasManga: Boolean,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "studios") val studios: List<Studio>,
    @Json(name = "publishers") val publishers: List<Publisher>,
  //  @Json(name = "activity") val activity: List<Activity>
)

@JsonClass(generateAdapter = true)
data class Activity(
    @Json(name = "name") val name: Pair<Long, Long>,
    @Json(name = "value") val value: Long
)
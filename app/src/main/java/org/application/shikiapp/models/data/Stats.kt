package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stats(
    @SerialName("statuses") val statuses: Statuses = Statuses(),
//    @SerialName("full_statuses") val fullStatuses: Statuses = Statuses(),
//    @SerialName("scores") val scores: Scores = Scores(),
//    @SerialName("types") val types: Types = Types(),
//    @SerialName("ratings") val ratings: Ratings = Ratings(),
//    @SerialName("has_anime?") val hasAnime: Boolean = false,
//    @SerialName("has_manga?") val hasManga: Boolean = false,
//    @SerialName("genres") val genres: List<Genre> = emptyList(),
//    @SerialName("studios") val studios: List<Studio> = emptyList(),
//    @SerialName("publishers") val publishers: List<Publisher> = emptyList(),
    //  @SerialName("activity") val activity: List<Activity>
)

//@Serializable
//data class Activity(
//    val name: Pair<Long, Long>,
//    val value: Long
//)
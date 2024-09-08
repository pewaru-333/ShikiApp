package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class History(
    @Json(name = "id") val id: Long,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "description") val description: String,
    @Json(name = "target") val target: HistoryTarget?
)

@JsonClass(generateAdapter = true)
data class HistoryTarget(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "kind") val kind: String?,
    @Json(name = "score") val score: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "volumes") val volumes: Int?,
    @Json(name = "chapters") val chapters: Int?,
    @Json(name = "episodes") val episodes: Int?,
    @Json(name = "episodes_aired") val episodesAired: Int?,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?
)
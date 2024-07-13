package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnimeShort(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "kind") val kind: String,
    @Json(name = "score") val score: String,
    @Json(name = "status") val status: String,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "episodes_aired") val episodesAired: Int,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?
)

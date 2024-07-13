package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(
    @Json(name = "id") val id: Long,
    @Json(name = "url") val url: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "player_url") val playerUrl: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "kind") val kind: String?,
    @Json(name = "hosting") val hosting: String?
)
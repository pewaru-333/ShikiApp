package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryAnime(
    @Json(name = "id") val id: Long,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "description") val description: String,
    @Json(name = "target") val target: AnimeShort
)
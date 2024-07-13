package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Achievement(
    @Json(name = "id") val id: Long,
    @Json(name = "neko_id") val nekoId: String,
    @Json(name = "level") val level: Int,
    @Json(name = "progress") val progress: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)



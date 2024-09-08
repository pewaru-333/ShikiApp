package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ban(
    @Json(name = "id") val id: Long,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "comment") val comment: String,
    @Json(name = "moderator_id") val moderatorId: Long,
    @Json(name = "reason") val reason: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "duration_minutes") val durationMinutes: Int,
    @Json(name = "user") val user: UserShort,
    @Json(name = "moderator") val moderator: UserShort
)

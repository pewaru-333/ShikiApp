package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val id: Long,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "commentable_id") val commentableId: Long,
    @Json(name = "commentable_type") val commentableType: String,
    @Json(name = "body") val body: String,
    @Json(name = "html_body") val htmlBody: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "is_offtopic") val isOfftopic: Boolean,
    @Json(name = "is_summary") val isSummary: Boolean,
    @Json(name = "can_be_edited") val canBeEdited: Boolean,
    @Json(name = "user") val user: User
)
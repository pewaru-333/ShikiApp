package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("id") val id: Long,
    @SerialName("user_id") val userId: Long,
    @SerialName("commentable_id") val commentableId: Long,
    @SerialName("commentable_type") val commentableType: String,
    @SerialName("body") val body: String,
    @SerialName("html_body") val htmlBody: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_offtopic") val isOfftopic: Boolean,
    @SerialName("is_summary") val isSummary: Boolean,
    @SerialName("can_be_edited") val canBeEdited: Boolean,
    @SerialName("user") val user: UserBasic
)
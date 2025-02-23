package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    @SerialName("id") val id: Long,
    @SerialName("neko_id") val nekoId: String,
    @SerialName("level") val level: Int,
    @SerialName("progress") val progress: Int,
    @SerialName("user_id") val userId: Long,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
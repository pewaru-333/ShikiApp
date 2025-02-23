package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class History(
    @SerialName("id") val id: Long,
    @SerialName("created_at") val createdAt: String,
    @SerialName("description") val description: String,
    @SerialName("target") val target: BasicContent?
)
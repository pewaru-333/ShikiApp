package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbuseRequest(
    @SerialName("kind") val kind: String,
    @SerialName("value") val value: Boolean,
    @SerialName("affected_ids") val affectedIds: List<Int>
)

package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String,
    @SerialName("kind") val kind: String,
    @SerialName("entry_type") val entryType: String
)
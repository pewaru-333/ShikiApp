package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Studio(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("filtered_name") val filteredName: String,
    @SerialName("real") val real: Boolean,
    @SerialName("image") val image: String?
)
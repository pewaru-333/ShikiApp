package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalLink(
    @SerialName("id") val id: Long?,
    @SerialName("kind") val kind: String,
    @SerialName("url") val url: String,
    @SerialName("source") val source: String,
    @SerialName("entry_id") val entryId: Long,
    @SerialName("entry_type") val entryType: String,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("imported_at") val importedAt: String?
)

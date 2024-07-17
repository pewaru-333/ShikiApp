package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalLink(
    @Json(name = "id") val id: Long?,
    @Json(name = "kind") val kind: String,
    @Json(name = "url") val url: String,
    @Json(name = "source") val source: String,
    @Json(name = "entry_id") val entryId: Long,
    @Json(name = "entry_type") val entryType: String,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "imported_at") val importedAt: String?
)

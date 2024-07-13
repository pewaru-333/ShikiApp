package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String,
    @Json(name = "kind") val kind: String,
    @Json(name = "entry_type") val entryType: String
)
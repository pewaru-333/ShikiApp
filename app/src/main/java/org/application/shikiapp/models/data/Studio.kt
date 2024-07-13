package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Studio(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "filtered_name") val filteredName: String,
    @Json(name = "real") val real: Boolean,
    @Json(name = "image") val image: String?
)
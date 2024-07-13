package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Publisher(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
)

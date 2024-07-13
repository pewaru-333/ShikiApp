package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Screenshot(
    @Json(name = "original") val original: String,
    @Json(name = "preview") val preview: String
)
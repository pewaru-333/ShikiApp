package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "original") val original: String,
    @Json(name = "preview") val preview: String?,
    @Json(name = "x96") val x96: String?,
    @Json(name = "x48") val x48: String?,
)
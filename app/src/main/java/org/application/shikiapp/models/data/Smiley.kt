package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Smiley(
    @Json(name = "bbcode") val bbCode: String,
    @Json(name = "path") val path: String
)
package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatesStatuses(
    @Json(name = "name") val name: String,
    @Json(name = "value") val value: Int
)
package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AbuseRequest(
    @Json(name = "kind") val kind: String,
    @Json(name = "value") val value: Boolean,
    @Json(name = "affected_ids") val affectedIds: List<Int>
)

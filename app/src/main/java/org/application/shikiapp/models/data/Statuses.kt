package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Statuses(
    @Json(name = "anime") val anime: List<ShortInfo>,
    @Json(name = "manga") val manga: List<ShortInfo>
)

@JsonClass(generateAdapter = true)
data class Scores(
    @Json(name = "anime") val anime: List<RatesScores>,
    @Json(name = "manga") val manga: List<RatesScores>
)

@JsonClass(generateAdapter = true)
data class Types(
    @Json(name = "anime") val anime: List<RatesScores>,
    @Json(name = "manga") val manga: List<RatesScores>
)

@JsonClass(generateAdapter = true)
data class Ratings(
    @Json(name = "anime") val anime: List<RatesScores>
)

@JsonClass(generateAdapter = true)
data class ShortInfo(
    @Json(name = "id") val id: Long,
    @Json(name = "grouped_id") val groupedId: String,
    @Json(name = "name") val name: String,
    @Json(name = "size") val size: Long,
    @Json(name = "type") val type: String
)
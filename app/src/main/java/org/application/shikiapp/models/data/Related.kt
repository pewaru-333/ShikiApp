package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Related(
    @Json(name = "relation") val relation: String,
    @Json(name = "relation_russian") val relationRussian: String,
    @Json(name = "anime") val anime: Anime?,
    @Json(name = "manga") val manga: Manga?
)

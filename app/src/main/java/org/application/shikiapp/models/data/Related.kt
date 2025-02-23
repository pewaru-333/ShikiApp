package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Related(
    @SerialName("relation") val relation: String,
    @SerialName("relation_russian") val relationRussian: String,
    @SerialName("anime") val anime: AnimeBasic?,
    @SerialName("manga") val manga: MangaBasic?
)

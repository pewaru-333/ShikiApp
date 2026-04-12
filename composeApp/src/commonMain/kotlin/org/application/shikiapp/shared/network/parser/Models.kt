package org.application.shikiapp.shared.network.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KodikTokensResponse(
    val stable: List<KodikTokenItem> = emptyList(),
    val unstable: List<KodikTokenItem> = emptyList(),
    val legacy: List<KodikTokenItem> = emptyList()
)

@Serializable
data class KodikTokenItem(
    val tokn: String
)

@Serializable
data class KodikSearchResponse(
    val results: List<KodikResultItem>
)

@Serializable
data class KodikResultItem(
    val id: String,
    val link: String,
    val translation: KodikTranslation,
    @SerialName("episodes_count")
    val episodesCount: Int? = null,
    val type: String,
    val seasons: Map<String, KodikSeason>? = null,
    val screenshots: List<String>? = null,
    val quality: String? = null,
    @SerialName("last_episode")
    val lastEpisode: Int? = null
)

@Serializable
data class KodikSeason(
    val link: String,
    val episodes: Map<String, KodikEpisode> = emptyMap()
)

@Serializable
data class KodikEpisode(
    val link: String,
    val screenshots: List<String> = emptyList()
)

@Serializable
data class KodikTranslation(
    val id: Int,
    val title: String,
    val type: String
)
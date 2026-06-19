package org.application.shikiapp.shared.network.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
    @SerialName("imdb_id")
    val imdbId: String,
    @SerialName("title_orig")
    val titleOrig: String,
    @SerialName("other_title")
    val otherTitle: String,
    val link: String,
    val translation: KodikTranslation,
    @SerialName("last_season")
    val lastSeason: Int? = null,
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

// ================================================================================================

@Serializable
data class CollapsTitleDetails(
    @SerialName("iframe_url")
    val iframeUrl: String? = null,
    val poster: String? = null,
    val quality: String? = null,
    val voiceActing: List<String>? = null,
    val subtitle: List<String>? = null,
    val seasons: List<CollapsSeason>? = null
)

@Serializable
data class CollapsSeason(
    val season: Int,
    val episodes: List<CollapsEpisode> = emptyList()
)

@Serializable
data class CollapsEpisode(
    val episode: Int,
    @SerialName("iframe_url")
    val iframeUrl: String? = null,
    @SerialName("iframe_poster")
    val iframePoster: String? = null,
    val voiceActing: List<String>? = null,
    val subtitle: List<String>? = null
)

@Serializable
data class CollapsSeasonData(
    val season: Int,
    val episodes: List<CollapsEpisodeData> = emptyList()
)

@Serializable
data class CollapsEpisodeData(
    val episode: String? = null,
    val hls: String? = null,
    val dash: String? = null,
    val dasha: String? = null,
    val cc: List<JsonObject>? = null
)

// ================================================================================================

@Serializable
data class CvhPlaylistResponse(
    val items: List<CvhPlaylistItem> = emptyList()
)

@Serializable
data class CvhPlaylistItem(
    val vkId: String,
    val voiceStudio: String? = null,
    val voiceType: String? = null,
    val season: Int = 1,
    val episode: Int = 1
)

@Serializable
data class CvhVideoResponse(
    val sources: CvhSources = CvhSources()
)

@Serializable
data class CvhSources(
    val hlsUrl: String? = null,
    val dashUrl: String? = null,
    val mpegQhdUrl: String? = null,
    val mpeg2kUrl: String? = null,
    val mpeg4kUrl: String? = null,
    val mpegHighUrl: String? = null,
    val mpegFullHdUrl: String? = null,
    val mpegMediumUrl: String? = null,
    val mpegLowUrl: String? = null,
    val mpegLowestUrl: String? = null,
    val mpegTinyUrl: String? = null
)

// ================================================================================================
@Serializable
data class AnimeLibSearchResult(
    val data: List<AnimeLibResultItem>
)

@Serializable
data class AnimeLibSearchResultItem(
    val data: AnimeLibResultItem
)

@Serializable
data class AnimeLibEpisodesList(
    val data: List<AnimeLibEpisodeResultItem>
)

@Serializable
data class AnimeLibTeamItem(
    val id: Long,
    val name: String
)

@Serializable
data class AnimeLibResultItem(
    val id: Long,
    @SerialName("slug_url")
    val slugUrl: String,
    @SerialName("shikimori_href")
    val shikimoriHref: String,
    val teams: List<AnimeLibTeamItem> = emptyList()
)

@Serializable
data class AnimeLibEpisodeResultItem(
    val id: Long,
    val name: String?,
    val number: String,
    @SerialName("item_number")
    val itemNumber: Int
)

@Serializable
data class AnimeLibEpisodeDetailResponse(
    val data: AnimeLibEpisodeDetail
)

@Serializable
data class AnimeLibEpisodeDetail(
    val id: Long,
    val players: List<AnimeLibPlayer> = emptyList()
)

@Serializable
data class AnimeLibTranslationType(
    val id: Int,
    val label: String
)

@Serializable
data class AnimeLibPlayer(
    val id: Long,
    val player: String,
    val video: AnimeLibVideo? = null,
    val team: AnimeLibTeamItem? = null,
    @SerialName("translation_type")
    val translationType: AnimeLibTranslationType? = null,
    val subtitles: List<AnimeLibSubtitle> = emptyList()
)

@Serializable
data class AnimeLibVideo(
    val id: Long,
    val quality: List<AnimeLibVideoQuality> = emptyList()
)

@Serializable
data class AnimeLibVideoQuality(
    val href: String,
    val quality: Int
)

@Serializable
data class AnimeLibSubtitle(
    val id: Long,
    val format: String,
    val name: String,
    val src: String
)
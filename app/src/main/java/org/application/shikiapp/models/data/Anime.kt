package org.application.shikiapp.models.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Anime(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "kind") val kind: String,
    @Json(name = "score") val score: String,
    @Json(name = "status") val status: String,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "episodes_aired") val episodesAired: Int,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "english") val english: List<String>?,
    @Json(name = "japanese") val japanese: List<String>?,
    @Json(name = "synonyms") val synonyms: List<String>?,
    @Json(name = "license_name_ru") val licenseNameRu: String?,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "description_html") val descriptionHtml: String?,
    @Json(name = "description_source") val descriptionSource: String?,
    @Json(name = "franchise") val franchise: String?,
    @Json(name = "favoured") val favoured: Boolean,
    @Json(name = "anons") val anons: Boolean?,
    @Json(name = "ongoing") val ongoing: Boolean?,
    @Json(name = "thread_id") val threadId: Long?,
    @Json(name = "topic_id") val topicId: Long?,
    @Json(name = "myanimelist_id") val myAnimeListId: Long?,
    @Json(name = "rates_scores_stats") val ratesScoresStats: List<RatesScores>?,
    @Json(name = "rates_statuses_stats") val ratesStatusesStats: List<RatesStatuses>?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "next_episode_at") val nextEpisodeAt: String?,
    @Json(name = "fansubbers") val fanSubbers: List<String> = emptyList(),
    @Json(name = "fandubbers") val fanDubbers: List<String> = emptyList(),
    @Json(name = "licensors") val licensors: List<String> = emptyList(),
    // @Json(name = "genres") val genres: List<AnimeGenresQuery.Genre> = emptyList(),
    @Json(name = "studios") val studios: List<Studio> = emptyList(),
    @Json(name = "videos") val videos: List<Video> = emptyList(),
    @Json(name = "screenshots") val screenshots: List<Screenshot> = emptyList(),
    @Json(name = "user_rate") val userRate: UserRate?
)

@JsonClass(generateAdapter = true)
data class AnimeShort(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "kind") val kind: String,
    @Json(name = "score") val score: String,
    @Json(name = "status") val status: String,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "episodes_aired") val episodesAired: Int,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?
)
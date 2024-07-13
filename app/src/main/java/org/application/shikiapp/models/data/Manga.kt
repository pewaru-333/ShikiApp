package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Manga(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "kind") val kind: String,
    @Json(name = "score") val score: String,
    @Json(name = "status") val status: String,
    @Json(name = "volumes") val volumes: Int,
    @Json(name = "chapters") val chapters: Int,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?,
    @Json(name = "english") val english: List<String> = emptyList(),
    @Json(name = "japanese") val japanese: List<String> = emptyList(),
    @Json(name = "synonyms") val synonyms: List<String> = emptyList(),
    @Json(name = "license_name_ru") val licenseNameRu: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "description_html") val descriptionHTML: String?,
    @Json(name = "description_source") val descriptionSource: String?,
    @Json(name = "franchise") val franchise: String?,
    @Json(name = "favoured") val favoured: Boolean?,
    @Json(name = "anons") val anons: Boolean?,
    @Json(name = "ongoing") val ongoing: Boolean?,
    @Json(name = "thread_id") val threadId: Long?,
    @Json(name = "topic_id") val topicId: Long?,
    @Json(name = "myanimelist_id") val myAnimeListId: Long?,
    @Json(name = "rates_scores_stats") val ratesScoresStats: List<RatesScores> = emptyList(),
    @Json(name = "rates_statuses_stats") val ratesStatusesStats: List<RatesStatuses> = emptyList(),
    @Json(name = "licensors") val licensors: List<String> = emptyList(),
    @Json(name = "genres") val genres: List<Genre> = emptyList(),
    @Json(name = "publishers") val publishers: List<Publisher> = emptyList(),
    @Json(name = "user_rate") val userRate: List<UserRate> = emptyList()
)

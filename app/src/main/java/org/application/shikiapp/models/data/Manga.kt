package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String,
    @SerialName("image") val image: Image,
    @SerialName("url") val url: String,
    @SerialName("kind") val kind: String,
    @SerialName("score") val score: String,
    @SerialName("status") val status: String,
    @SerialName("volumes") val volumes: Int,
    @SerialName("chapters") val chapters: Int,
    @SerialName("aired_on") val airedOn: String?,
    @SerialName("released_on") val releasedOn: String?,
    @SerialName("english") val english: List<String?> = emptyList(),
    @SerialName("japanese") val japanese: List<String> = emptyList(),
    @SerialName("synonyms") val synonyms: List<String> = emptyList(),
    @SerialName("license_name_ru") val licenseNameRu: String?,
    @SerialName("description") val description: String?,
    @SerialName("description_html") val descriptionHTML: String?,
    @SerialName("description_source") val descriptionSource: String?,
    @SerialName("franchise") val franchise: String?,
    @SerialName("favoured") val favoured: Boolean,
    @SerialName("anons") val anons: Boolean?,
    @SerialName("ongoing") val ongoing: Boolean?,
    @SerialName("thread_id") val threadId: Long?,
    @SerialName("topic_id") val topicId: Long?,
    @SerialName("myanimelist_id") val myAnimeListId: Long?,
    @SerialName("rates_scores_stats") val ratesScoresStats: List<RatesScores> = emptyList(),
    @SerialName("rates_statuses_stats") val ratesStatusesStats: List<RatesStatuses> = emptyList(),
    @SerialName("licensors") val licensors: List<String> = emptyList(),
    @SerialName("genres") val genres: List<Genre> = emptyList(),
    @SerialName("publishers") val publishers: List<Publisher> = emptyList(),
    @SerialName("user_rate") val userRate: UserRate?
)

@Serializable
class MangaBasic : BasicContent() {
    val volumes: Int? = null
    val chapters: Int? = null
}
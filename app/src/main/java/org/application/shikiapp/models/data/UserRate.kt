package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserRate(
    @Json(name = "id") val id: Long,
    @Json(name = "user_id") val userId: Long?,
    @Json(name = "target_id") val targetId: Long?,
    @Json(name = "target_type") val targetType: String?,
    @Json(name = "score") val score: Int,
    @Json(name = "status") val status: String,
    @Json(name = "rewatches") val rewatches: Int?,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "volumes") val volumes: Int?,
    @Json(name = "chapters") val chapters: Int?,
    @Json(name = "text") val text: String?,
    @Json(name = "text_html") val textHtml: String?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class AnimeRate(
    @Json(name = "id") val id: Long,
    @Json(name = "score") val score: Int,
    @Json(name = "status") val status: String,
    @Json(name = "text") val text: String?,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "chapters") val chapters: Int?,
    @Json(name = "volumes") val volumes: Int?,
    @Json(name = "text_html") val textHtml: String?,
    @Json(name = "rewatches") val rewatches: Int,
    @Json(name = "anime") val anime: AnimeRateInfo,
)

@JsonClass(generateAdapter = true)
data class AnimeRateInfo(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "kind") val kind: String?,
    @Json(name = "status") val status: String,
    @Json(name = "episodes") val episodes: Int,
    @Json(name = "episodes_aired") val episodesAired: Int,
)

@JsonClass(generateAdapter = true)
data class NewRate(
    @Json(name = "user_id") val userId: Long? = null,
    @Json(name = "target_id") val targetId: Long? = null,
    @Json(name = "target_type") val targetType: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "score") val score: String? = null,
    @Json(name = "chapters") val chapters: String? = null,
    @Json(name = "episodes") val episodes: String? = null,
    @Json(name = "volumes") val volumes: String? = null,
    @Json(name = "rewatches") val rewatches: String? = null,
    @Json(name = "text") val text: String? = null
)
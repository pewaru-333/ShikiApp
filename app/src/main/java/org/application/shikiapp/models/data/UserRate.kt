package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseRate(
    @SerialName("id") val id: Long,
    @SerialName("score") val score: Int,
    @SerialName("status") val status: String,
    @SerialName("text") val text: String?,
    @SerialName("text_html") val textHtml: String?,
    @SerialName("rewatches") val rewatches: Int,
    @SerialName("episodes") val episodes: Int?,
    @SerialName("chapters") val chapters: Int?,
    @SerialName("volumes") val volumes: Int?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("anime") val anime: AnimeBasic?,
    @SerialName("manga") val manga: MangaBasic?
)

//@Serializable
//data class UserRate(
//    @SerialName("id") val id: Long,
//    @SerialName("user_id") val userId: Long?,
//    @SerialName("target_id") val targetId: Long?,
//    @SerialName("target_type") val targetType: String?,
//    @SerialName("score") val score: Int,
//    @SerialName("status") val status: String,
//    @SerialName("rewatches") val rewatches: Int?,
//    @SerialName("episodes") val episodes: Int?,
//    @SerialName("volumes") val volumes: Int?,
//    @SerialName("chapters") val chapters: Int?,
//    @SerialName("text") val text: String?,
//    @SerialName("text_html") val textHtml: String?,
//    @SerialName("created_at") val createdAt: String?,
//    @SerialName("updated_at") val updatedAt: String?
//)

@Serializable
data class NewRate(
    @SerialName("id") val id: Long? = null,
    @SerialName("user_id") val userId: Long? = null,
    @SerialName("target_id") val targetId: Long? = null,
    @SerialName("target_type") val targetType: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("score") val score: String? = null,
    @SerialName("chapters") val chapters: String? = null,
    @SerialName("episodes") val episodes: String? = null,
    @SerialName("volumes") val volumes: String? = null,
    @SerialName("rewatches") val rewatches: String? = null,
    @SerialName("text") val text: String? = null
)
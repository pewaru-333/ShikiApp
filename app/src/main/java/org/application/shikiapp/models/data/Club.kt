package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Club(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "logo") val logo: ClubImage,
    @Json(name = "is_censored") val isCensored: Boolean,
    @Json(name = "join_policy") val joinPolicy: String?,
    @Json(name = "comment_Policy") val commentPolicy: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "description_html") val descriptionHtml: String?,
    @Json(name = "thread_id") val threadId: Long?,
    @Json(name = "topic_id") val topicId: Long?,
    @Json(name = "user_role") val userRole: String?,
    @Json(name = "style_id") val styleId: Long?,
    @Json(name = "images") val images: List<ClubImages> = emptyList(),
    @Json(name = "members") val members: List<UserShort> = emptyList(),
    @Json(name = "animes") val animes: List<AnimeShort> = emptyList(),
    @Json(name = "mangas") val mangas: List<MangaShort> = emptyList(),
    @Json(name = "characters") val characters: List<Character> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ClubImage(
    @Json(name = "original") val original: String?,
    @Json(name = "main") val main: String?,
    @Json(name = "x96") val x96: String?,
    @Json(name = "x73") val x73: String?,
    @Json(name = "x48") val x48: String?
)

@JsonClass(generateAdapter = true)
data class ClubImages(
    @Json(name = "id") val id: Long,
    @Json(name = "original_url") val originalUrl: String?,
    @Json(name = "main_url") val mainUrl: String?,
    @Json(name = "preview_url") val previewUrl: String?,
    @Json(name = "can_destroy") val canDestroy: Boolean?,
    @Json(name = "user_id") val userId: Long
)
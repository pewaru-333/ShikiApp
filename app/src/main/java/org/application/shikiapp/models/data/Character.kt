package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Character(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "altname") val altName: String?,
    @Json(name = "japanese") val japanese: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "description_html") val descriptionHTML: String?,
    @Json(name = "description_source") val descriptionSource: String?,
    @Json(name = "favoured") val favoured: Boolean,
    @Json(name = "thread_id") val threadId: Long?,
    @Json(name = "topic_id") val topicId: Long?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "seyu") val seyu: List<CharacterPerson> = emptyList(),
    @Json(name = "animes") val animes: List<AnimeShort> = emptyList(),
    @Json(name = "mangas") val mangas: List<MangaShort> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CharacterPerson(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
)
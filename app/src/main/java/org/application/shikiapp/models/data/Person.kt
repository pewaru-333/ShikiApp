package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Person(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "russian") val russian: String?,
    @Json(name = "image") val image: Image,
    @Json(name = "url") val url: String,
    @Json(name = "japanese") val japanese: String?,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "birth_on") val birthOn: Date = Date(),
    @Json(name = "deceased_on") val deceasedOn: Date = Date(),
    @Json(name = "website") val website: String,
    @Json(name = "groupped_roles") val grouppedRoles: List<Pair<String, Int>>?,
    @Json(name = "roles") val roles: List<Roles>?,
    @Json(name = "works") val works: List<Works>?,
    @Json(name = "topic_id") val topicId: Long?,
    @Json(name = "person_favoured") val personFavoured: Boolean,
    @Json(name = "producer") val producer: Boolean,
    @Json(name = "producer_favoured") val producerFavoured: Boolean,
    @Json(name = "mangaka") val mangaka: Boolean,
    @Json(name = "mangaka_favoured") val mangakaFavoured: Boolean,
    @Json(name = "seyu") val seyu: Boolean,
    @Json(name = "seyu_favoured") val seyuFavoured: Boolean,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "thread_id") val threadId: Long?,
    @Json(name = "birthday") val birthday: Date = Date()
)

@JsonClass(generateAdapter = true)
data class Date(
    @Json(name = "day") val day: Int? = null,
    @Json(name = "month") val month: Int? = null,
    @Json(name = "year") val year: Int? = null
)

@JsonClass(generateAdapter = true)
data class Roles(
    @Json(name = "characters") val characters: List<CharacterPerson>,
    @Json(name = "animes") val animes: List<AnimeShort>
)

@JsonClass(generateAdapter = true)
data class Works(
    @Json(name = "anime") val anime: AnimeShort?,
    @Json(name = "manga") val manga: MangaShort?,
    @Json(name = "roles") val roles: Role?
)
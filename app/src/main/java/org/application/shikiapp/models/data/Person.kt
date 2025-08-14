package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("russian") val russian: String?,
    @SerialName("image") val image: Image,
    @SerialName("url") val url: String,
    @SerialName("japanese") val japanese: String,
    @SerialName("job_title") val jobTitle: String,
    // @SerialName("birth_on") val birthOn: Date? = null,
    @SerialName("deceased_on") val deceasedOn: Date?,
    @SerialName("website") val website: String,
    @SerialName("groupped_roles") val grouppedRoles: List<List<String>>,
    @SerialName("roles") val roles: List<Roles>?,
    @SerialName("works") val works: List<Works>?,
    @SerialName("topic_id") val topicId: Long?,
    @SerialName("person_favoured") val personFavoured: Boolean,
    @SerialName("producer") val producer: Boolean,
    @SerialName("producer_favoured") val producerFavoured: Boolean,
    @SerialName("mangaka") val mangaka: Boolean,
    @SerialName("mangaka_favoured") val mangakaFavoured: Boolean,
    @SerialName("seyu") val seyu: Boolean,
    @SerialName("seyu_favoured") val seyuFavoured: Boolean,
    @SerialName("updated_at") val updatedAt: String,
    //  @SerialName("thread_id") val threadId: Long?,
    @SerialName("birthday") val birthday: Date?
)

@Serializable
data class Date(
    val day: Int?,
    val month: Int?,
    val year: Int?
)

@Serializable
data class Roles(
    val characters: List<BasicInfo>,
    val animes: List<AnimeBasic>
)

@Serializable
data class Works(
    val anime: AnimeBasic?,
    val manga: MangaBasic?,
    val role: String?
)
package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
class Character : BasicInfo() {
    @SerialName("altname")
    val altName: String? = null

    @SerialName("japanese")
    val japanese: String? = null

    @SerialName("description")
    val description: String? = null

    @SerialName("description_html")
    val descriptionHTML: String? = null

    @SerialName("description_source")
    val descriptionSource: String? = null

    @SerialName("favoured")
    val favoured: Boolean = false

//    @SerialName("thread_id")
//    val threadId: Long = 0L

    @SerialName("topic_id")
    val topicId: Long? = null

    @SerialName("updated_at")
    val updatedAt: String = BLANK

    @SerialName("seyu")
    val seyu: List<BasicInfo> = emptyList()

    @SerialName("animes")
    val animes: List<AnimeBasic> = emptyList()

    @SerialName("mangas")
    val mangas: List<MangaBasic> = emptyList()
}
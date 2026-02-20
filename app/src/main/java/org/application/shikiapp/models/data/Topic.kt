package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
class Topic {
    @SerialName("id")
    val id: Long = 0L

    @SerialName("linked")
    val linked: BasicContent = BasicContent()

    @SerialName("event")
    val event: String? = null

//    @SerialName("episode")
//    val episode: Int? = null

    @SerialName("created_at")
    val createdAt: String = BLANK

    @SerialName("url")
    val url: String = BLANK
}
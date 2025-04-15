package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
class News {
    @SerialName("id")
    val id: Long = 0L

    @SerialName("topic_title")
    val topicTitle: String = BLANK

    @SerialName("body")
    val body: String = BLANK

    @SerialName("html_body")
    val htmlBody: String = BLANK

    @SerialName("html_footer")
    val htmlFooter: String = BLANK

    @SerialName("created_at")
    val createdAt: String = BLANK

    @SerialName("comments_count")
    val commentsCount: Int = 0

    @SerialName("user")
    val user: UserBasic = UserBasic()
}
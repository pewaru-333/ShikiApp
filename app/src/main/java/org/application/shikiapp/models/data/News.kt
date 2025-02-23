package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    @SerialName("id") val id: Long,
    @SerialName("topic_title") val topicTitle: String,
    @SerialName("body") val body: String,
    @SerialName("html_body") val htmlBody: String,
    @SerialName("html_footer") val htmlFooter: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("user") val user: UserBasic
)
package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class News(
    @Json(name = "id") val id: Long,
    @Json(name = "topic_title") val topicTitle: String,
    @Json(name = "body") val body: String,
    @Json(name = "html_body") val htmlBody: String,
    @Json(name = "html_footer") val htmlFooter: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "user") val user: User
)
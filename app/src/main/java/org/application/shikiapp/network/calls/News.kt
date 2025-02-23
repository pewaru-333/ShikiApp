package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.News

class News(private val client: HttpClient) {
    suspend fun getNewsList(page: Int, limit: Int) = client.get("topics") {
        parameter("forum", "news")
        parameter("page", page)
        parameter("limit", limit)
    }.body<List<News>>()

    suspend fun getTopic(id: Long) = client.get("topics/$id").body<News>()

    suspend fun getComments(
        id: Long,
        type: String,
        page: Int,
        limit: Int,
        desc: Int = 1
    ) = client.get("comments") {
        parameter("commentable_id", id)
        parameter("commentable_type", type)
        parameter("page", page)
        parameter("limit", limit)
        parameter("desc", desc)
    }.body<List<Comment>>()
}
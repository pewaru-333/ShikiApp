package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.application.shikiapp.models.data.BaseRate
import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.utils.extensions.requestWithCache

class UserRates(private val client: HttpClient) {
    suspend fun getAnimeRates(id: Long, page: Int, limit: Int = 5000) = client.requestWithCache<List<BaseRate>?>(
        cacheKey = "anime_rates:$id",
        url = "users/$id/anime_rates"
    ) {
        parameter("page", page)
        parameter("limit", limit)
    }

    suspend fun getMangaRates(id: Long, page: Int, limit: Int = 5000) = client.requestWithCache<List<BaseRate>?>(
        cacheKey = "manga_rates:$id",
        url = "users/$id/manga_rates"
    ) {
        parameter("page", page)
        parameter("limit", limit)
    }

    suspend fun createRate(newRate: NewRate) = client.post("v2/user_rates") {
        contentType(ContentType.Application.Json)
        setBody(newRate)
    }

    suspend fun updateRate(id: Long, newRate: NewRate) = client.patch("v2/user_rates/$id") {
        contentType(ContentType.Application.Json)
        setBody(newRate)
    }

    suspend fun increment(id: Long) = client.post("v2/user_rates/$id/increment")
    suspend fun delete(id: Long) = client.delete("v2/user_rates/$id")
}
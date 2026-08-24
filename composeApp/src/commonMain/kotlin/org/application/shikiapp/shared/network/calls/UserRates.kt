package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.BaseRate
import org.application.shikiapp.shared.models.data.NewRate
import org.application.shikiapp.shared.models.data.UserRate

class UserRates(private val client: HttpClient) {
    suspend fun getAnimeRates(id: Long, page: Int, limit: Int = 5000): List<BaseRate>? =
        client.get("users/$id/anime_rates") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    suspend fun getMangaRates(id: Long, page: Int, limit: Int = 5000): List<BaseRate>? =
        client.get("users/$id/manga_rates") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    suspend fun getAnimeRate(animeId: String) = client.get("v2/user_rates") {
        parameter("user_id", Preferences.userId)
        parameter("target_id", animeId)
        parameter("target_type", "Anime")
    }.body<List<UserRate>>()

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
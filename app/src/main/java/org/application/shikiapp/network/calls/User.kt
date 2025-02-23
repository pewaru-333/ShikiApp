package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.History
import org.application.shikiapp.models.data.MangaRate
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic

class User(private val client: HttpClient) {

//    @GET("users")
//    suspend fun getList(
//        @Query("search") search: String? = null,
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 5,
//    ): List<User>

    suspend fun getUser(id: Long) = client.get("users/$id").body<User>()

    suspend fun getFriends(id: Long, page: Int = 1, limit: Int = 5) =
        client.get("users/$id/friends") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<UserBasic>>()

    suspend fun getClubs(id: Long) = client.get("users/$id/clubs").body<List<ClubBasic>>()

    suspend fun getFavourites(id: Long) = client.get("users/$id/favourites").body<Favourites>()

    suspend fun getAnimeRates(
        id: Long,
        status: String? = null,
        censored: Boolean? = null,
        page: Int = 1,
        limit: Int = 5000
    ) = client.get("users/$id/anime_rates") {
        parameter("status", status)
        parameter("censored", censored)
        parameter("page", page)
        parameter("limit", limit)
    }.body<List<AnimeRate>>()

    suspend fun getMangaRates(
        id: Long,
        status: String? = null,
        censored: Boolean? = null,
        page: Int = 1,
        limit: Int = 5000
    ) = client.get("users/$id/manga_rates") {
        parameter("status", status)
        parameter("censored", censored)
        parameter("page", page)
        parameter("limit", limit)
    }.body<List<MangaRate>>()

    suspend fun getHistory(
        id: Long,
        page: Int = 1,
        limit: Int = 20,
        targetType: String? = null
    ) = client.get("users/$id/history") {
        parameter("page", page)
        parameter("limit", limit)
        parameter("target_type", targetType)
    }.body<List<History>>()
}
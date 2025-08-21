package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.History
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.utils.extensions.requestWithCache

class User(private val client: HttpClient) {
    suspend fun getUser(id: Long) = client.requestWithCache<User>(
        cacheKey = "user:$id",
        url = "users/$id"
    )

    suspend fun getFriends(id: Long, page: Int = 1, limit: Int = 5): List<UserBasic> =
        client.requestWithCache(
            cacheKey = "user_friends:$id:page=$page:limit=$limit",
            url = "users/$id/friends"
        ) {
            parameter("page", page)
            parameter("limit", limit)
        }

    suspend fun getClubs(id: Long): List<ClubBasic> = client.requestWithCache(
        cacheKey = "user_clubs:$id",
        url = "users/$id/clubs"
    )

    suspend fun getFavourites(id: Long) = client.requestWithCache<Favourites>(
        cacheKey = "user_favourites:$id",
        url = "users/$id/favourites"
    )

    suspend fun getHistory(
        id: Long,
        page: Int = 1,
        limit: Int = 20,
        targetType: String? = null
    ): List<History> = client.requestWithCache(
        cacheKey = "history:$id:page=$page:limit=$limit:type=${targetType ?: "all"}",
        url = "users/$id/history"
    ) {
        parameter("page", page)
        parameter("limit", limit)
        parameter("target_type", targetType)
    }

    suspend fun addFriend(id: Long) = client.post("friends/$id")

    suspend fun removeFriend(id: Long) = client.delete("friends/$id")
}
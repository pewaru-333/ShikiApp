package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.application.shikiapp.shared.models.data.*
import org.application.shikiapp.shared.models.data.User

class User(private val client: HttpClient) {
    suspend fun getUser(id: Long): User = client.get("users/$id").body()

    suspend fun getFriends(id: Long, page: Int = 1, limit: Int = 5): List<UserBasic> =
        client.get("users/$id/friends") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    suspend fun getClubs(id: Long): List<ClubBasic> = client.get("users/$id/clubs").body()

    suspend fun getFavourites(id: Long): Favourites = client.get("users/$id/favourites").body()

    suspend fun getHistory(
        id: Long,
        page: Int = 1,
        limit: Int = 20,
        targetType: String? = null
    ): List<History> = client.get("users/$id/history") {
        parameter("page", page)
        parameter("limit", limit)
        parameter("target_type", targetType)
    }.body()

    suspend fun addFriend(id: Long) = client.post("friends/$id")

    suspend fun removeFriend(id: Long) = client.delete("friends/$id")
}
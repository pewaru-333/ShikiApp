package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.data.UserBasic

class Clubs(private val client: HttpClient) {
    suspend fun getClubs(search: String, page: Int = 1, limit: Int = 15) = client.get("clubs") {
        parameter("search", search)
        parameter("page", page)
        parameter("limit", limit)
    }.body<List<Club>>()

    suspend fun getClub(id: Any) = client.get("clubs/$id").body<Club>()

    suspend fun getAnime(id: Long, page: Int = 1, limit: Int = 20) =
        client.get("clubs/$id/animes") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<AnimeBasic>>()

    suspend fun getManga(id: Long, page: Int = 1, limit: Int = 20) =
        client.get("clubs/$id/mangas") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<MangaBasic>>()

    suspend fun getRanobe(id: Long, page: Int = 1, limit: Int = 20) =
        client.get("clubs/$id/ranobe") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<MangaBasic>>()

    suspend fun getCharacters(id: Long, page: Int = 1, limit: Int = 20) =
        client.get("clubs/$id/characters") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<BasicInfo>>()

//    @GET("clubs/{clubId}/collections")
//    suspend fun getClubCollections(
//        @Path(value = "clubId") clubId: Long,
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 10
//    ): List<Collections>

    suspend fun getClubClubs(id: Long, page: Int = 1, limit: Int = 10) =
        client.get("clubs/$id/clubs") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<Club>>()

    suspend fun getMembers(id: Long, page: Int = 1, limit: Int = 50) =
        client.get("clubs/$id/members") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<UserBasic>>()

    suspend fun getImages(id: Long, page: Int = 1, limit: Int = 30) =
        client.get("clubs/$id/images") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<ClubImages>>()

    suspend fun joinClub(id: Long) = client.post("clubs/$id/join")

    suspend fun leaveClub(id: Long) = client.post("clubs/$id/leave")
}
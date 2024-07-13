package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.Manga
import org.application.shikiapp.models.data.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Clubs {
    @GET("clubs")
    suspend fun getClubs(): List<Club>

    @GET("clubs/{clubId}")
    suspend fun getClub(@Path(value = "clubId") clubId: Long): Club

    @GET("clubs/{clubId}/animes")
    suspend fun getAnime(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<AnimeShort>

    @GET("clubs/{clubId}/mangas")
    suspend fun getManga(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<Manga>

    @GET("clubs/{clubId}/ranobe")
    suspend fun getRanobe(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<Manga>

    @GET("clubs/{clubId}/characters")
    suspend fun getCharacters(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<Character>

//    @GET("clubs/{clubId}/collections")
//    suspend fun getClubCollections(
//        @Path(value = "clubId") clubId: Long,
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 10
//    ): List<Collections>

    @GET("clubs/{clubId}/clubs")
    suspend fun getClubClubs(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<Club>

    @GET("clubs/{clubId}/members")
    suspend fun getMembers(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<User>

    @GET("clubs/{clubId}/images")
    suspend fun getImages(
        @Path(value = "clubId") clubId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): List<ClubImages>
}
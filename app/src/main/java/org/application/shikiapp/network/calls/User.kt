package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.HistoryAnime
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserShort
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface User {

    @GET("users")
    suspend fun getList(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5,
    ): List<User>

    @GET("users/{userId}")
    suspend fun getUser(@Path(value = "userId") userId: Long): User

    @GET("users/{userId}/info")
    suspend fun getBriefInfo(@Path(value = "userId") userId: Long): User

    @GET("users/{userId}/friends")
    suspend fun getFriends(
        @Path(value = "userId") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5
    ): List<UserShort>

    @GET("users/{userId}/clubs")
    suspend fun getClubs(@Path(value = "userId") userId: Long): List<Club>

    @GET("users/{userId}/anime_rates")
    suspend fun getAnimeRates(
        @Path(value = "userId") userId: Long,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5000
    ): List<AnimeRate>

    @GET("users/{userId}/favourites")
    suspend fun getFavourites(@Path(value = "userId") userId: Long): Favourites

    @GET("users/{userId}/history")
    suspend fun getHistory(
        @Path(value = "userId") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("target_type") targetType: String = "Anime"
    ): List<HistoryAnime>
}
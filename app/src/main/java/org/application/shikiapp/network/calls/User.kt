package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface User {

    @GET("users")
    suspend fun getList(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5,
        @Query("search") search: String? = null
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
    ): List<User>

    @GET("users/{userId}/clubs")
    suspend fun getClubs(@Path(value = "userId") userId: Long): List<Club>

    @GET("users/{userId}/anime_rates")
    suspend fun getAnimeRates(
        @Path(value = "userId") userId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5000,
        @Query("status") status: String? = null,
        @Query("censored") censored: Boolean? = null
    ): List<AnimeRate>
}
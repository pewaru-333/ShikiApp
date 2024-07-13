package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.models.data.UserRate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserRates {

    @GET("v2/user_rates")
    suspend fun getRates(
        @Query("user_id") userId: Long? = null,
        @Query("target_id") targetId: Long? = null,
        @Query("target_type") targetType: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Long? = 5
    ): List<UserRate>

    @POST("v2/user_rates")
    suspend fun createRate(@Body newRate: NewRate): UserRate

    @PATCH("v2/user_rates/{id}")
    suspend fun updateRate(@Path("id") id: Long, @Body newRate: NewRate): UserRate

    @FormUrlEncoded
    @POST("v2/user_rates/{id}/increment")
    suspend fun increment(@Path("id") id: Long)

    @DELETE("v2/user_rates/{id}")
    suspend fun delete(@Path("id") id: Long)
}
package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.NewRate
import org.application.shikiapp.models.data.UserRate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserRates {

    @POST("v2/user_rates")
    suspend fun createRate(@Body newRate: NewRate): UserRate

    @PATCH("v2/user_rates/{id}")
    suspend fun updateRate(@Path("id") id: Long, @Body newRate: NewRate): UserRate

    @POST("v2/user_rates/{id}/increment")
    suspend fun increment(@Path("id") id: Long)

    @DELETE("v2/user_rates/{id}")
    suspend fun delete(@Path("id") id: Long)
}
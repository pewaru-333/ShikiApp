package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.TOKEN_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CLIENT_SECRET
import org.application.shikiapp.utils.GRANT_TYPE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.REFRESH_TOKEN
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface Profile {

    @FormUrlEncoded
    @POST
    suspend fun getToken(
        @Url url: String = TOKEN_URL,
        @Field("grant_type") grantType: String = GRANT_TYPE,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = REDIRECT_URI
    ): Token

    @FormUrlEncoded
    @Headers("User-Agent: ShikiApp")
    @POST
    suspend fun getRefreshToken(
        @Url url: String = TOKEN_URL,
        @Field("grant_type") grantType: String = REFRESH_TOKEN,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET,
        @Field("refresh_token") refreshToken: String,
    ): Token

    @GET("users/whoami")
    suspend fun whoAmI(): User

    @POST("users/sign_out")
    suspend fun signOut()

    @POST("favorites/{linked_type}/{linked_id}/{kind}")
    suspend fun addFavourite(
        @Path("linked_type") linkedType: String,
        @Path("linked_id") linkedId: Long,
        @Path("kind") kind: String = BLANK
    )

    @DELETE("favorites/{linked_type}/{linked_id}")
    suspend fun deleteFavourite(
        @Path("linked_type") linkedType: String,
        @Path("linked_id") linkedId: Long
    )
}
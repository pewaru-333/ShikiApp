package org.application.shikiapp.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.application.shikiapp.network.calls.Anime
import org.application.shikiapp.network.calls.Clubs
import org.application.shikiapp.network.calls.NetworkCalls
import org.application.shikiapp.network.calls.Profile
import org.application.shikiapp.network.calls.User
import org.application.shikiapp.network.calls.UserRates
import org.application.shikiapp.utils.PairAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val API_URL = "https://shikimori.one/api/"
private const val GRAPH_URL = "https://shikimori.one/api/graphql"
const val AUTH_URL = "https://shikimori.one/oauth/authorize"
const val TOKEN_URL = "https://shikimori.one/oauth/token"

private val moshi = Moshi.Builder()
    .add(PairAdapterFactory)
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(TokenInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

object NetworkClient {
    val anime: Anime by lazy { retrofit.create(Anime::class.java) }
    val clubs: Clubs by lazy { retrofit.create(Clubs::class.java) }
    val profile: Profile by lazy { retrofit.create(Profile::class.java) }
    val rates: UserRates by lazy { retrofit.create(UserRates::class.java) }
    val user: User by lazy { retrofit.create(User::class.java) }

    val client: NetworkCalls by lazy { retrofit.create(NetworkCalls::class.java) }
    val apollo = ApolloClient.Builder()
        .serverUrl(GRAPH_URL)
        .okHttpClient(okHttpClient)
        .build()
}

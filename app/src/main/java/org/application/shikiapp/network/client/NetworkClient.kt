@file:OptIn(ExperimentalSerializationApi::class)

package org.application.shikiapp.network.client

import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.ktorClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.application.shikiapp.network.calls.Anime
import org.application.shikiapp.network.calls.Clubs
import org.application.shikiapp.network.calls.Content
import org.application.shikiapp.network.calls.Manga
import org.application.shikiapp.network.calls.Profile
import org.application.shikiapp.network.calls.Topics
import org.application.shikiapp.network.calls.User
import org.application.shikiapp.network.calls.UserRates
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.TokenManager

private const val API_URL = "https://shikimori.one/api/"
private const val GRAPH_URL = "https://shikimori.one/api/graphql"
const val AUTH_URL = "https://shikimori.one/oauth/authorize"
const val TOKEN_URL = "https://shikimori.one/oauth/token"

object NetworkClient {
    private val client = HttpClient(OkHttp) {
        expectSuccess = true

        engine {
            config {
                dispatcher = Dispatchers.IO

                addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newBuilder = originalRequest.newBuilder()

                    Preferences.getToken().let { token ->
                        if (token.isNotBlank())
                            newBuilder.header(HttpHeaders.Authorization, "Bearer $token")
                    }

                    val newRequest = newBuilder.build()
                    chain.proceed(newRequest)
                }

                authenticator { route, response ->
                    synchronized(this) {
                        val newToken = runBlocking { TokenManager.refreshToken() }
                        val newBuilder = response.request.newBuilder()

                        if (newToken != null) {
                            newBuilder.header(HttpHeaders.Authorization, "Bearer ${newToken.accessToken}")
                        }

                        newBuilder.build()
                    }
                }
            }
        }

        install(UserAgent) {
            agent = "ShikiApp"
        }

        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    decodeEnumsCaseInsensitive = true
                }
            )
        }

        defaultRequest {
            url(API_URL)
        }
    }

    val apollo = ApolloClient.Builder()
        .serverUrl(GRAPH_URL)
        .dispatcher(Dispatchers.IO)
        .ktorClient(client)
        .build()

    val anime = Anime(client)
    val manga = Manga(client)
    val clubs = Clubs(client)
    val rates = UserRates(client)
    val user = User(client)
    val profile = Profile(client)
    val topics = Topics(client)
    val content = Content(client)
}
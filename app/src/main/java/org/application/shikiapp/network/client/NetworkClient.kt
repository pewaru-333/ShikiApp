@file:OptIn(ExperimentalSerializationApi::class)

package org.application.shikiapp.network.client

import com.apollographql.apollo.ApolloClient
import com.apollographql.ktor.http.KtorHttpEngine
import com.apollographql.ktor.ktorClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.application.shikiapp.network.calls.Anime
import org.application.shikiapp.network.calls.Clubs
import org.application.shikiapp.network.calls.Content
import org.application.shikiapp.network.calls.Manga
import org.application.shikiapp.network.calls.News
import org.application.shikiapp.network.calls.Profile
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
            dispatcher = Dispatchers.IO
            clientCacheSize = 0

            config {
                addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder()

                    if (Preferences.isTokenExists()) {
                        builder.header(Authorization, "Bearer ${Preferences.getToken()}")
                    }

                    val request = builder.build()
                    chain.proceed(request)
                }
                authenticator { _, response ->
                    synchronized(this) {
                        TokenManager.refreshToken()


                        response.request.newBuilder()
                            .header(Authorization, "Bearer ${Preferences.getToken()}")
                            .build()
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
        .httpEngine(KtorHttpEngine())
        .ktorClient(client)
        .build()

    val anime = Anime(client)
    val manga = Manga(client)
    val clubs = Clubs(client)
    val rates = UserRates(client)
    val user = User(client)
    val profile = Profile(client)
    val news = News(client)
    val content = Content(client)
}
@file:OptIn(ExperimentalSerializationApi::class)

package org.application.shikiapp.network.client

import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
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
import org.application.shikiapp.utils.API_URL
import org.application.shikiapp.utils.GRAPH_URL
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.extensions.refreshToken


object Network {
    val client: HttpClient by lazy {
        HttpClient(Android) {
            engine {
                dispatcher = Dispatchers.IO
            }

            install(UserAgent) {
                agent = "ShikiApp"
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        Preferences.token?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }

                    refreshTokens {
                        val newToken = refreshToken()

                        newToken?.let { BearerTokens(it.accessToken, it.refreshToken) }
                    }
                }
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

            install(HttpRequestRetry) {
                maxRetries = 3

                delayMillis { 1000 }

                retryIf { _, response ->
                    response.status == HttpStatusCode.TooManyRequests
                }
            }

            defaultRequest {
                url(API_URL)
            }
        }
    }

    val apollo by lazy {
        ApolloClient.Builder()
            .serverUrl(GRAPH_URL)
            .httpEngine(KtorEngine(client))
            .build()
    }

    val anime by lazy { Anime(client) }
    val manga by lazy { Manga(client) }
    val clubs by lazy { Clubs(client) }
    val rates by lazy { UserRates(client) }
    val user by lazy { User(client) }
    val profile by lazy { Profile(client) }
    val topics by lazy { Topics(client) }
    val content by lazy { Content(client) }
}
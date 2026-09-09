package org.application.shikiapp.shared.network.client

import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.ProxyConfig
import io.ktor.client.engine.http
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.calls.*
import org.application.shikiapp.shared.network.calls.shiki.IAnimeRepository
import org.application.shikiapp.shared.network.calls.shiki.ICharacterRepository
import org.application.shikiapp.shared.network.calls.shiki.IMangaRepository
import kotlin.io.encoding.Base64


object Network {
    val baseClient: HttpClient by lazy {
        HttpClient {
            engine {
                dispatcher = Dispatchers.IO
                proxy = configureProxy()
            }

            install(UserAgent) {
                agent = AppConfig.userAgent
            }

            defaultRequest {
                if (Preferences.useProxy) {
                    val host = Preferences.proxyHost
                    val user = Preferences.proxyUsername
                    val password = Preferences.proxyPassword

                    val isHttpProxy = host.startsWith("http", ignoreCase = true)

                    if (isHttpProxy && user.isNotBlank() && password.isNotBlank()) {
                        val credentials = Base64.encode("$user:$password".encodeToByteArray())
                        header(HttpHeaders.ProxyAuthorization, "Basic $credentials")
                    }
                }
            }
        }
    }

    val watchClient: HttpClient by lazy {
        HttpClient {
            engine {
                dispatcher = Dispatchers.IO
            }

            BrowserUserAgent()
        }
    }

    val client: HttpClient by lazy {
        baseClient.config {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 15_000
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        Preferences.token?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }

                    refreshTokens {
                        oldTokens?.refreshToken?.let { refreshToken ->
                            val newToken = profile.refreshToken(refreshToken) {
                                markAsRefreshTokenRequest()
                            }

                            newToken?.let { BearerTokens(it.accessToken, it.refreshToken) }
                        }
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

                retryIf { _, response -> response.status == HttpStatusCode.TooManyRequests }

                retryOnExceptionIf { _, throwable -> throwable is IOException }
            }

            install(HttpCache)

            install(RateLimit)

            install(BaseUrlResolverPlugin) {
                val (baseUrl, mirrors) = Preferences.appUrlPair

                isUserMode = { Preferences.useUserUrlList }

                baseUrlProvider = { baseUrl }
                mirrorsProvider = { mirrors }

                onNewUrl = { workingUrl ->
                    ApiRoutes.workingBaseUrl = workingUrl
                }
            }

            defaultRequest {
                url("/api/")
            }
        }
    }

    val apollo by lazy {
        ApolloClient.Builder()
            .serverUrl("/api/graphql")
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

    val animeRepository by lazy {
        if (AppConfig.isShikimori) IAnimeRepository(apollo)
        else org.application.shikiapp.shared.network.calls.dark.IAnimeRepository(apollo)
    }

    val mangaRepository by lazy {
        if (AppConfig.isShikimori) IMangaRepository(apollo)
        else org.application.shikiapp.shared.network.calls.dark.IMangaRepository(apollo)
    }

    val characterRepository by lazy {
        if (AppConfig.isShikimori) ICharacterRepository(apollo)
        else org.application.shikiapp.shared.network.calls.dark.ICharacterRepository(apollo)
    }

    internal fun configureProxy(): ProxyConfig? {
        if (!Preferences.useProxy) return null

        val host = Preferences.proxyHost
        val port = Preferences.proxyPort

        if (host.isBlank() || port.isBlank()) return null
        val portInt = port.toIntOrNull() ?: return null // всегда число (проверка при вводе)

        val isSocks = host.startsWith("socks5://", ignoreCase = true)
        val isHttps = host.startsWith("https://", ignoreCase = true)

        val cleanHost = host.substringAfter("://")

        return if (isSocks) {
            ProxyBuilder.socks(cleanHost, portInt)
        } else {
            val scheme = if (isHttps) "https" else "http"
            ProxyBuilder.http("$scheme://$cleanHost:$portInt")
        }
    }
}
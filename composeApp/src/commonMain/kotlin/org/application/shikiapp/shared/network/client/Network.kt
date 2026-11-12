package org.application.shikiapp.shared.network.client

import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
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
import org.application.shikiapp.shared.di.AppServices
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.calls.*


object Network {
    val isConnectionActive: Boolean
        get() = extension?.isActive == true

    val hasNetworkExtension: Boolean
        get() = extension != null

    val requiresAuthProxy: Boolean
        get() = extension?.requiresAuthProxy == true

    internal val baseUrlResolver = BaseUrlResolver()

    private val extension
        get() = AppServices.networkExtension

    val baseClient: HttpClient by lazy {
        val proxy = getProxyConfig()

        createHttpClient(proxy, extension) {
            engine {
                dispatcher = Dispatchers.IO
            }

            install(UserAgent) {
                agent = AppConfig.userAgent
            }

            defaultRequest {
                proxy?.httpAuthHeader?.let { header ->
                    header(HttpHeaders.ProxyAuthorization, header)
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

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            install(HttpCache)

            install(RateLimit)

            install(BaseUrlResolverPlugin)

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
    val animeRepository get() = AppServices.animeRepository
    val mangaRepository get() = AppServices.mangaRepository
    val characterRepository get() = AppServices.characterRepository

    internal fun getProxyConfig() = ProxyConfig.create(
        enabled = Preferences.useProxy.value,
        host = Preferences.proxyHost.value,
        port = Preferences.proxyPort.value,
        user = Preferences.proxyUsername.value,
        pass = Preferences.proxyPassword.value
    )

    suspend fun resolveFallback(): String = extension?.resolve(baseClient) ?: throw IOException()

    suspend fun startAuthProxy(): Int? = extension?.startAuthProxy()

    suspend fun stopAuthProxy() {
        extension?.stopAuthProxy()
    }
}
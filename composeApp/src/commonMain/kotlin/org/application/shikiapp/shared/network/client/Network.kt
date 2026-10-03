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
    val isYggdrasilAvailable: Boolean
        get() = AppServices.yggdrasilTransport != null && AppConfig.yggdrasilAddress != null

    private val yggdrasil by lazy {
        YggdrasilConfig(
            enabled = isYggdrasilAvailable && Preferences.yggdrasilEnabled.value,
            peers = Preferences.yggdrasilPeerList,
            privateKeyPem = Preferences.yggdrasilPrivateKey.value.takeIf(String::isNotBlank)
        )
    }

    val baseClient: HttpClient by lazy {
        val proxy = if (yggdrasil.enabled) {
            null
        } else {
            getProxyConfig()
        }

        createHttpClient(proxy, yggdrasil) {
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
                if (yggdrasil.enabled) {
                    val address = AppConfig.yggdrasilAddress
                    if (address != null) {
                        baseUrlProvider = { address }
                        isYggdrasil = { true }
                    }
                } else {
                    val (baseUrl, mirrors) = Preferences.appUrlPair

                    baseUrlProvider = { baseUrl }
                    mirrorsProvider = { mirrors }
                }

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
}
package org.application.shikiapp.shared.network.client

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.util.generateNonceSuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.*
import okio.ByteString.Companion.encodeUtf8
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.parser.AnimeLibParser
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.basicJson
import kotlin.coroutines.cancellation.CancellationException

object AnimeLibAuth {
    private val mutex = Mutex()
    private val callbackUri = Url(ApiRoutes.REDIRECT_URI_LIB)
    val isLogging: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private val client by lazy {
        Network.watchClient.config {
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }

            defaultRequest {
                header(HttpHeaders.Origin, AnimeLibParser.ORIGIN)
                header("Referer", "${AnimeLibParser.ORIGIN}/")
                header("site_id", "5")
            }
        }
    }

    suspend fun authUri(): String = mutex.withLock {
        var state = Preferences.libAuthState
        var verifier = Preferences.libCodeVerifier

        if (state.isBlank() || verifier.isBlank()) {
            state = generateNonceSuspend(40)
            verifier = generateNonceSuspend(64)
            Preferences.saveAuthLib(state, verifier)
        }

        val builder = URLBuilder(AnimeLibParser.API_AUTH_URL)
        val parameters = builder.parameters

        parameters.append("scope", BLANK)
        parameters.append("client_id", "3")
        parameters.append("response_type", "code")
        parameters.append("redirect_uri", ApiRoutes.REDIRECT_URI_LIB)
        parameters.append("state", state)
        parameters.append("code_challenge", generateCodeChallenge(verifier))
        parameters.append("code_challenge_method", "S256")
        parameters.append("prompt", "consent")

        builder.buildString()
    }

    fun isCallback(uri: String): Boolean = try {
        val url = Url(uri)
        url.protocol == callbackUri.protocol &&
                url.host.equals(callbackUri.host, ignoreCase = true) &&
                url.port == callbackUri.port &&
                url.encodedPath == callbackUri.encodedPath &&
                url.user == null &&
                url.password == null
    } catch (_: Exception) {
        false
    }

    suspend fun handleCallback(uri: String): Boolean? {
        if (!isCallback(uri)) return null

        val parameters = Url(uri).parameters
        val state = parameters["state"] ?: return null
        if (state.isBlank() || state != Preferences.libAuthState) return null
        if (!isLogging.compareAndSet(false, true)) return null

        return try {
            mutex.withLock {
                val verifier = Preferences.libCodeVerifier
                if (state != Preferences.libAuthState || verifier.isBlank()) return@withLock null

                try {
                    if (parameters["error"] != null) return@withLock false
                    val code = parameters["code"]?.takeIf(String::isNotBlank) ?: return@withLock false

                    val response = requestToken(
                        parameters = buildJsonObject {
                            put("grant_type", "authorization_code")
                            put("client_id", 3)
                            put("redirect_uri", ApiRoutes.REDIRECT_URI_LIB)
                            put("code_verifier", verifier)
                            put("code", code)
                        }
                    )

                    if (!response.status.isSuccess()) return@withLock false

                    val json = basicJson.parseToJsonElement(response.bodyAsText()) as? JsonObject ?: return@withLock false
                    val accessToken = json.token("access_token") ?: return@withLock false
                    val refreshToken = json.token("refresh_token") ?: return@withLock false

                    if (state != Preferences.libAuthState) return@withLock null

                    Preferences.saveTokenLib(accessToken, refreshToken)
                    true
                } finally {
                    if (state == Preferences.libAuthState) {
                        Preferences.saveAuthLib(BLANK, BLANK)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        } finally {
            isLogging.value = false
        }
    }

    suspend fun refreshToken(): Boolean {
        val token = Preferences.libToken ?: return false

        return mutex.withLock {
            if (Preferences.libToken != token) return@withLock Preferences.libToken != null

            try {
                val response = requestToken(
                    parameters = buildJsonObject {
                        put("grant_type", "refresh_token")
                        put("client_id", 3)
                        put("refresh_token", token.refreshToken)
                    }
                )
                val json = basicJson.parseToJsonElement(response.bodyAsText()) as? JsonObject ?: return@withLock false
                if (Preferences.libToken != token) return@withLock Preferences.libToken != null

                if (response.status.isSuccess()) {
                    val accessToken = json.token("access_token") ?: return@withLock false
                    val refreshToken = json.token("refresh_token") ?: token.refreshToken

                    Preferences.saveTokenLib(accessToken, refreshToken)
                    true
                } else {
                    if (
                        (response.status == HttpStatusCode.BadRequest || response.status == HttpStatusCode.Unauthorized) &&
                        json.token("error") == "invalid_grant"
                    ) {
                        Preferences.saveTokenLib(BLANK, BLANK)
                    }
                    false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                false
            }
        }
    }

    suspend fun validateToken(): Boolean? {
        val token = Preferences.libToken ?: return false

        return try {
            val response = client.get("${AnimeLibParser.API_HOST}/auth/me") {
                header(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
            }

            if (Preferences.libToken != token) return null
            if (response.status == HttpStatusCode.Unauthorized) return false
            if (!response.status.isSuccess()) return null

            val json = basicJson.parseToJsonElement(response.bodyAsText()) as? JsonObject ?: return null
            val data = json["data"] as? JsonObject ?: return false
            val id = (data["id"] as? JsonPrimitive)?.longOrNull
            id != null && id > 0L
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    fun signOut() {
        Preferences.saveAuthLib(BLANK, BLANK)
        Preferences.saveTokenLib(BLANK, BLANK)
    }

    private fun generateCodeChallenge(verifier: String) = verifier.encodeUtf8()
        .sha256()
        .base64Url()
        .trimEnd('=')

    private suspend fun requestToken(parameters: JsonObject): HttpResponse =
        client.post("${AnimeLibParser.API_HOST}/auth/oauth/token") {
            contentType(ContentType.Application.Json)
            setBody(parameters.toString())
        }

    private fun JsonObject.token(name: String): String? =
        (this[name] as? JsonPrimitive)
            ?.takeIf(JsonPrimitive::isString)
            ?.contentOrNull
            ?.takeIf(String::isNotBlank)
}

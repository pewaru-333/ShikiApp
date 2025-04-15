package org.application.shikiapp.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.client.TOKEN_URL

object TokenManager {
    suspend fun getToken(code: String) {
        val token = NetworkClient.profile.getToken(code = code)
        Preferences.saveToken(token)
    }

    suspend fun getUser(): User {
        val whoAmI = NetworkClient.profile.whoAmI()
        val user = NetworkClient.user.getUser(whoAmI.id)
        Preferences.setUserId(whoAmI.id)

        return user
    }

    suspend fun refreshToken(): Token? {
        val client = HttpClient {
            expectSuccess = true

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
        }

        return Mutex().withLock {
            repeat(3) { attempt ->
                try {
                    val token = client.submitForm(
                        url = TOKEN_URL,
                        formParameters = parameters {
                            append("grant_type", REFRESH_TOKEN)
                            append("client_id", CLIENT_ID)
                            append("client_secret", CLIENT_SECRET)
                            append("refresh_token", Preferences.getRefreshToken())
                        }
                    ).body<Token>()

                    Preferences.saveToken(token)
                    return@withLock token
                } catch (_: Throwable) {
                    if (attempt < 2) {
                        delay(2000L)
                    }
                }
            }

            val token = Token(BLANK, BLANK, 0, BLANK, BLANK, 0)
            Preferences.saveToken(token)

            return@withLock null
        }
    }
}
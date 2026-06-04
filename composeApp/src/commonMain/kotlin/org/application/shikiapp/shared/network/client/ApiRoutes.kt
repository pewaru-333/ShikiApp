package org.application.shikiapp.shared.network.client

import io.ktor.http.URLBuilder
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.network.parser.AnimeLibParser
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.generateCodeChallenge
import org.application.shikiapp.shared.utils.generateRandomString
import kotlin.concurrent.Volatile

object ApiRoutes {
    @Volatile
    var workingBaseUrl = AppConfig.baseUrl

    val authUrl: String
        get() = "$workingBaseUrl/oauth/authorize"

    val tokenUrl: String
        get() = "$workingBaseUrl/oauth/token"

    const val REDIRECT_URI_LIB = "ru.libapp.oauth://type/callback"

    val authUri: String
        get() = URLBuilder(authUrl).apply {
            encodedParameters.apply {
                append("client_id", AppConfig.clientId)
                append("redirect_uri", AppConfig.redirectUri)
                append("response_type", "code")
                append("scope", AppConfig.authScopes.joinToString("+", transform = String::lowercase))
            }
        }.buildString()

    val authUriLib: String
        get() {
            val state = generateRandomString(40)
            val secret = generateRandomString(128)
            val challenge = generateCodeChallenge(secret)

            currentVerifier = secret

            return URLBuilder(AnimeLibParser.API_AUTH_URL).apply {
                encodedParameters.apply {
                    append("scope", BLANK)
                    append("client_id", "3")
                    append("response_type", "code")
                    append("redirect_uri", REDIRECT_URI_LIB)
                    append("state", state)
                    append("code_challenge", challenge)
                    append("code_challenge_method", "S256")
                    append("prompt", "consent")
                }
            }.buildString()
        }

    var currentVerifier = BLANK
}
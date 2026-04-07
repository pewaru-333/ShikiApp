package org.application.shikiapp.shared.network.client

import io.ktor.http.URLBuilder
import org.application.shikiapp.shared.di.AppConfig

object ApiRoutes {
    @Volatile
    var workingBaseUrl = AppConfig.baseUrl

    val authUrl: String
        get() = "$workingBaseUrl/oauth/authorize"

    val tokenUrl: String
        get() = "$workingBaseUrl/oauth/token"

    val authUri: String
        get() = URLBuilder(authUrl).apply {
            encodedParameters.apply {
                append("client_id", AppConfig.clientId)
                append("redirect_uri", AppConfig.redirectUri)
                append("response_type", "code")
                append("scope", AppConfig.authScopes.joinToString("+", transform = String::lowercase))
            }
        }.buildString()
}
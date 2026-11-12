package org.application.shikiapp.shared.network.client

import io.ktor.http.URLBuilder
import org.application.shikiapp.shared.di.AppConfig

object ApiRoutes {
    val workingBaseUrl: String
        get() = Network.baseUrlResolver.workingUrl ?: AppConfig.baseUrl

    val tokenUrl: String
        get() = "$workingBaseUrl/oauth/token"

    const val REDIRECT_URI_LIB = "ru.libapp.oauth://type/callback"

    fun authUri(baseUrl: String): String {
        val builder = URLBuilder("$baseUrl/oauth/authorize")
        val parameters = builder.parameters

        parameters.append("client_id", AppConfig.clientId)
        parameters.append("redirect_uri", AppConfig.redirectUri)
        parameters.append("response_type", "code")
        parameters.append("scope", AppConfig.authScopes.joinToString(" "))

        return builder.buildString()
    }
}
package org.application.shikiapp.shared.network.client

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.parser.AnimeLibParser
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.basicJson

object AnimeLibAuth {
    suspend fun getToken(code: String) = try {
        val response = Network.watchClient.post("${AnimeLibParser.API_HOST}/auth/oauth/token") {
            contentType(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("grant_type", "authorization_code")
                    put("client_id", 3)
                    put("redirect_uri", ApiRoutes.REDIRECT_URI_LIB)
                    put("code_verifier", ApiRoutes.currentVerifier)
                    put("code", code)
                }.toString()
            )

            headers {
                append("Origin", AnimeLibParser.ORIGIN)
                append("Referer", "${AnimeLibParser.ORIGIN}/")
                append("site_id", "5")
            }
        }

        if (response.status.isSuccess()) {
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject

            val accessToken = json["access_token"]?.jsonPrimitive?.content
            val refreshToken = json["refresh_token"]?.jsonPrimitive?.content

            if (accessToken != null && refreshToken != null) {
                Preferences.saveTokenLib(accessToken, refreshToken)
                true
            } else {
                false
            }
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }

    suspend fun refreshToken() = try {
        val response = Network.watchClient.post("${AnimeLibParser.API_HOST}/auth/oauth/token") {
            contentType(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("grant_type", "refresh_token")
                    put("client_id", 3)
                    put("refresh_token", Preferences.libToken?.refreshToken)
                }.toString()
            )

            headers {
                append("Origin", AnimeLibParser.ORIGIN)
                append("Referer", "${AnimeLibParser.ORIGIN}/")
                append("site_id", "5")
            }
        }

        if (response.status.isSuccess()) {
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject

            val accessToken = json["access_token"]?.jsonPrimitive?.content
            val refreshToken = json["refresh_token"]?.jsonPrimitive?.content

            if (accessToken != null && refreshToken != null) {
                Preferences.saveTokenLib(accessToken, refreshToken)
            } else {
                Preferences.saveTokenLib(BLANK, BLANK)
            }
        } else {
            Preferences.saveTokenLib(BLANK, BLANK)
        }
    } catch (_: Exception) {
        Preferences.saveTokenLib(BLANK, BLANK)
    }

    suspend fun validateToken(): Boolean = try {
        val response = Network.watchClient.get("${AnimeLibParser.API_HOST}/auth/me").bodyAsText()

        basicJson.parseToJsonElement(response)
            .jsonObject["data"]
            ?.jsonObject
            ?.containsKey("id") == true
    } catch (_: Exception) {
        false
    }
}
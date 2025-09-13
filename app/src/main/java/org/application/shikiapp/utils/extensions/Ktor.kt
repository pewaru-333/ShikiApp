package org.application.shikiapp.utils.extensions

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CLIENT_SECRET
import org.application.shikiapp.utils.REFRESH_TOKEN
import org.application.shikiapp.utils.TOKEN_URL

suspend fun RefreshTokensParams.refreshToken(): Token? = Mutex().withLock {
    repeat(3) { attempt ->
        try {
            val token = client.submitForm(
                url = TOKEN_URL,
                formParameters = parameters {
                    append("grant_type", REFRESH_TOKEN)
                    append("client_id", CLIENT_ID)
                    append("client_secret", CLIENT_SECRET)
                    append("refresh_token", oldTokens?.refreshToken.toString())
                }
            ) { markAsRefreshTokenRequest() }.body<Token>()

            Preferences.saveToken(token)
            return@withLock token
        } catch (e: Throwable) {
            if (attempt < 2) delay(2000L)
            else when (e) {
                is ClientRequestException -> Preferences.saveToken(Token.empty)

                else -> Unit
            }
        }
    }

    return@withLock null
}
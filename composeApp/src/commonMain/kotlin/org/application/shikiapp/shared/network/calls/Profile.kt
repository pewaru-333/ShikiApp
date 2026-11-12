package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitForm
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.*
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.REFRESH_TOKEN
import org.application.shikiapp.shared.utils.enums.MessageType
import kotlin.time.Duration.Companion.milliseconds

class Profile(private val client: HttpClient) {
    private val mutex = Mutex()

    suspend fun getToken(code: String) = client.submitForm(
        url = ApiRoutes.tokenUrl,
        formParameters = parameters {
            append("grant_type", "authorization_code")
            append("client_id", AppConfig.clientId)
            append("client_secret", AppConfig.clientSecret)
            append("code", code)
            append("redirect_uri", AppConfig.redirectUri)
        }
    ) {
        expectSuccess = true
        attributes.put(AuthCircuitBreaker, Unit)
        retry { maxRetries = 0 }
    }.body<Token>()

    suspend fun refreshToken(refreshToken: String, builder: HttpRequestBuilder.() -> Unit): Token? =
        mutex.withLock {
            val currentToken = Preferences.token ?: return@withLock null
            if (currentToken.refreshToken != refreshToken) {
                return@withLock currentToken
            }

            repeat(3) { attempt ->
                try {
                    val response = client.submitForm(
                        url = ApiRoutes.tokenUrl,
                        block = builder,
                        formParameters = parameters {
                            append("grant_type", REFRESH_TOKEN)
                            append("client_id", AppConfig.clientId)
                            append("client_secret", AppConfig.clientSecret)
                            append("refresh_token", refreshToken)
                        }
                    )

                    val newToken = if (response.status.isSuccess()) response.body<Token>() else null
                    currentCoroutineContext().ensureActive()

                    val savedToken = Preferences.token
                    if (savedToken?.refreshToken != refreshToken) {
                        return@withLock savedToken
                    }

                    if (newToken != null) {
                        Preferences.saveToken(newToken)
                        return@withLock newToken
                    } else if (response.status == HttpStatusCode.BadRequest || response.status == HttpStatusCode.Unauthorized) {
                        Preferences.saveToken(Token.empty)
                        return@withLock null
                    } else {
                        throw ResponseException(response, BLANK)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    if (e !is IOException || attempt == 2) {
                        throw e
                    }

                    delay((1000L * (attempt + 1)).milliseconds)
                }
            }

            return@withLock null
        }

    suspend fun whoAmI() = client.get("users/whoami") {
        expectSuccess = true
        header(HttpHeaders.CacheControl, "no-store")
    }.body<UserBrief>()

    suspend fun signOut(accessToken: String) = client.post("users/sign_out") {
        attributes.put(AuthCircuitBreaker, Unit)
        bearerAuth(accessToken)
        retry { maxRetries = 0 }
        timeout { requestTimeoutMillis = 5000 }
    }

    suspend fun addFavourite(linkedType: String, linkedId: Any, kind: String = BLANK) =
        client.post("favorites/$linkedType/$linkedId/$kind")

    suspend fun deleteFavourite(linkedType: String, linkedId: Any) =
        client.delete("favorites/$linkedType/$linkedId")

    suspend fun getDialogs(): List<Dialog> = client.get("dialogs").body()

    suspend fun getUserDialog(userId: Long, page: Int, limit: Int): List<FullMessage> =
        client.get("dialogs/$userId") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    suspend fun deleteUserDialog(userId: Long) = client.delete("dialogs/$userId")

    suspend fun getMessages(userId: Long, type: MessageType, page: Int, limit: Int) =
        client.get("users/$userId/messages") {
            parameter("type", type.name.lowercase())
            parameter("page", page)
            parameter("limit", limit)
        }.body<List<FullMessage>>()

    suspend fun getUnreadMessages(userId: Long) =
        client.get("users/$userId/unread_messages").body<UnreadMessages>()

    suspend fun sendMessage(message: MessageToSend) = client.post("messages") {
        contentType(ContentType.Application.Json)
        setBody(message)
    }

    suspend fun markRead(id: Long, isRead: Int) = client.post("messages/mark_read") {
        parameter("ids", id)
        parameter("is_read", isRead)
    }

    suspend fun markAllRead(type: MessageType) = client.post("messages/read_all") {
        parameter("type", type.name.lowercase())
    }

    suspend fun deleteMessage(id: Long) = client.delete("messages/$id")

    suspend fun deleteAllMessages(type: MessageType) = client.post("messages/delete_all") {
        parameter("type", type.name.lowercase())
    }

    suspend fun createComment(comment: CommentToCreate) = client.post("comments") {
        contentType(ContentType.Application.Json)
        setBody(comment)
    }

    suspend fun updateComment(id: Long, comment: CommentToCreate.Comment) =
        client.patch("comments/$id") {
            contentType(ContentType.Application.Json)
            setBody(comment)
        }

    suspend fun deleteComment(id: Long) = client.delete("comments/$id")

    suspend fun changeOfftopic(id: Long) = client.post("v2/abuse_requests/offtopic") {
        parameter("comment_id", id)
    }


//    suspend fun editMessage(id: Long, message: MessageToSend) = client.patch("messages/$id") {
//        contentType(ContentType.Application.Json)
//        setBody(message)
//    }
}
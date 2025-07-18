package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import org.application.shikiapp.models.data.Dialog
import org.application.shikiapp.models.data.FullMessage
import org.application.shikiapp.models.data.MessageToSend
import org.application.shikiapp.models.data.Token
import org.application.shikiapp.models.data.UserBrief
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CLIENT_SECRET
import org.application.shikiapp.utils.GRANT_TYPE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.TOKEN_URL

class Profile(private val client: HttpClient) {
    suspend fun getToken(code: String) = client.submitForm(
        url = TOKEN_URL,
        formParameters = parameters {
            append("grant_type", GRANT_TYPE)
            append("client_id", CLIENT_ID)
            append("client_secret", CLIENT_SECRET)
            append("code", code)
            append("redirect_uri", REDIRECT_URI)
        }
    ).body<Token>()

    suspend fun whoAmI() = client.get("users/whoami").body<UserBrief>()

    suspend fun signOut() = client.post("users/sign_out")

    suspend fun addFavourite(linkedType: String, linkedId: Any, kind: String = BLANK) =
        client.post("favorites/$linkedType/$linkedId/$kind")

    suspend fun deleteFavourite(linkedType: String, linkedId: Any) =
        client.delete("favorites/$linkedType/$linkedId")

    suspend fun getDialogs() = client.get("dialogs").body<List<Dialog>>()

    suspend fun getUserDialog(nickname: String) = client.get("dialogs/$nickname").body<List<FullMessage>>()

    suspend fun deleteUserDialog(nickname: String) = client.delete("dialogs/$nickname")

    suspend fun sendMessage(message: MessageToSend) = client.post("messages") {
        contentType(ContentType.Application.Json)
        setBody(message)
    }

//    suspend fun editMessage(id: Long, message: MessageToSend) = client.patch("messages/$id") {
//        contentType(ContentType.Application.Json)
//        setBody(message)
//    }

//    suspend fun deleteMessage(id: Long) = client.delete("messages/$id")
}
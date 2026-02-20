package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
open class Message {
    val id: Long = 0L
    val kind: String = BLANK
    val read: Boolean = false
    //val body: String? = null

    @SerialName("html_body")
    val htmlBody: String? = null

    @SerialName("created_at")
    val createdAt: String = BLANK

//    @SerialName("linked_id")
//    val linkedId: Long = 0L

    @SerialName("linked_type")
    val linkedType: String? = null
    val linked: AnimeBasic? = null
}

@Serializable
class FullMessage : Message() {
    val from: UserBasic = UserBasic()
    val to: UserBasic = UserBasic()
}

@Serializable
data class MessageToSend(
    val frontend: String,
    val message: MessageToSendShort
)

@Serializable
data class MessageToSendShort(
    val body: String,
    val kind: String,
    @SerialName("from_id") val fromId: Long,
    @SerialName("to_id") val toId: Long
)

@Serializable
data class UnreadMessages(
    val messages: Int,
    val news: Int,
    val notifications: Int
)

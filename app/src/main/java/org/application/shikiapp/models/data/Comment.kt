package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
class Comment {
    @SerialName("id")
    val id: Long = 0L

    @SerialName("user_id")
    val userId: Long = 0L

//    @SerialName("commentable_id")
//    val commentableId: Long = 0L
//
//    @SerialName("commentable_type")
//    val commentableType: String = BLANK

    @SerialName("body")
    val body: String = BLANK

    @SerialName("html_body")
    val htmlBody: String = BLANK

    @SerialName("created_at")
    val createdAt: String = BLANK

    @SerialName("updated_at")
    val updatedAt: String = BLANK

    @SerialName("is_offtopic")
    val isOfftopic: Boolean = false

//    @SerialName("is_summary")
//    val isSummary: Boolean = false
//
//    @SerialName("can_be_edited")
//    val canBeEdited: Boolean = false

    @SerialName("user")
    val user: UserBasic = UserBasic()
}
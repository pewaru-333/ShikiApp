package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dialog(
    @SerialName("target_user") val targetUser: UserBasic,
    val message: Message
)

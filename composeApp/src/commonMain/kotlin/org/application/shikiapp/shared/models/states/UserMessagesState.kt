package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.MessageType

data class UserMessagesState(
    val messageType: MessageType = MessageType.INBOX,
    val userId: Long = -1L,
    val userNickname: AsyncData<String> = AsyncData.Loading,
    val userAvatar: AsyncData<String> = AsyncData.Loading,
    val lastOnlineAt: String = BLANK,
    val isFromList: Boolean = false,
    val showDeleteAll: Boolean = false
)

package org.application.shikiapp.models.states

import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.enums.MessageType

data class UserMessagesState(
    val messageType: MessageType = MessageType.INBOX,
    val userId: Long = -1L,
    val userNickname: AsyncData<String> = AsyncData.Loading,
    val userAvatar: AsyncData<String> = AsyncData.Loading,
    val isFromList: Boolean = false,
    val showDeleteAll: Boolean = false
)

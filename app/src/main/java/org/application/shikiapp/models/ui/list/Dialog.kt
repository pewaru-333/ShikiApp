package org.application.shikiapp.models.ui.list

import org.application.shikiapp.utils.CommentContent

data class Dialog(
    val id: Long,
    val userId: Long,
    val userNickname: String,
    val userAvatar: String,
    val lastMessage: List<CommentContent>,
    val lastDate: String,
    val accountUser: Boolean,
    val isSending: Boolean = false,
    val isError: Boolean = false
)

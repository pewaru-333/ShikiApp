package org.application.shikiapp.models.ui.list

import org.application.shikiapp.utils.CommentContent

data class Dialog(
    val id: String,
    val userId: Long,
    val userNickname: String,
    val userAvatar: String,
    val lastMessage: List<CommentContent>,
    val lastDate: String
)

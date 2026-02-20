package org.application.shikiapp.models.ui

import org.application.shikiapp.utils.CommentContent

data class Comment(
    val id: Long,
    val userId: Long,
    val userAvatar: String,
    val userNickname: String,
    val createdAt: String,
    val commentContent: List<CommentContent>?,
    val isOfftopic: Boolean
)

package org.application.shikiapp.models.ui.list

data class Dialog(
    val id: String,
    val userId: Long,
    val userNickname: String,
    val userAvatar: String,
    val lastMessage: String,
    val lastDate: String
)

package org.application.shikiapp.models.ui.list

import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.CommentContent

data class Message(
    val id: Long,
    val kind: String,
    val read: AsyncData<Boolean>,
    val body: List<CommentContent>,
    val linked: Related?,
    val from: UserBasic,
    val to: UserBasic,
    val createdAt: String,
    val isDeleting: AsyncData<Boolean>
)

package org.application.shikiapp.shared.models.ui.list

import org.application.shikiapp.shared.models.data.UserBasic
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.ui.CommentContent

data class Message(
    val id: Long,
    val kind: String,
    val read: AsyncData<Boolean>,
    val body: List<CommentContent>,
    val linked: Related?,
    val from: UserBasic,
    val to: UserBasic,
    val createdAt: String,
    val isDeleting: AsyncData<Boolean>,
    val isBroadcast: Boolean = false
)

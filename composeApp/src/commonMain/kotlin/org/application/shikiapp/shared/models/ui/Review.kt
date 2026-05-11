package org.application.shikiapp.shared.models.ui

import org.application.shikiapp.shared.utils.enums.OpinionType
import org.application.shikiapp.shared.utils.ui.CommentContent

data class Review(
    val id: String,
    val userId: Long,
    val userNickname: String,
    val userAvatar: String,
    val date: String,
    val body: List<CommentContent>,
    val opinion: OpinionType,
    val animeScore: Int?,
    val watchStatus: String?,
    val votesFor: String,
    val votesAgainst: String
)

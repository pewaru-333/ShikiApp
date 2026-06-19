package org.application.shikiapp.shared.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.data.ClubCommentPolicy
import org.application.shikiapp.shared.models.data.ClubJoinPolicy
import org.application.shikiapp.shared.utils.ui.CommentContent

data class Club(
    val name: String,
    val image: String?,
    val description: List<CommentContent>,
    val comments: Flow<PagingData<Comment>>,
    val isCensored: Boolean,
    val joinPolicy: ClubJoinPolicy,
    val commentPolicy: ClubCommentPolicy
)

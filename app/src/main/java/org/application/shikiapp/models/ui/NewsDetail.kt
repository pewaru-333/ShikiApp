package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.utils.CommentContent

data class NewsDetail(
    val title: String,
    val newsBody: AnnotatedString,
    val poster: CommentContent?,
    val userId: Long,
    val userNickname: String,
    val userImage: String,
    val date: String,
    val images: List<String>,
    val videos: List<CommentContent.VideoContent>,
    val commentsCount: Int,
    val comments: Flow<PagingData<Comment>>
)

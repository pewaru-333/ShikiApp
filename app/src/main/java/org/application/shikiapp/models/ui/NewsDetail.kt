package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class NewsDetail(
    val title: String,
    val newsBody: AnnotatedString,
    val poster: String?,
    val userId: Long,
    val userNickname: String,
    val userImage: String,
    val date: String,
    val images: List<String>,
    val videos: List<String>,
    val commentsCount: Int,
    val comments: Flow<PagingData<Comment>>
)

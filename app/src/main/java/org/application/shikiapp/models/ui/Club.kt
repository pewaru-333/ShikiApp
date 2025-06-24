package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.ClubCommentPolicy
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.ClubJoinPolicy
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.list.Content

data class Club(
    val id: Long,
    val topicId: Long,
    val name: String,
    val image: String?,
    val description: AnnotatedString,
    val images: Flow<PagingData<ClubImages>>,
    val members: Flow<PagingData<UserBasic>>,
    val animes: Flow<PagingData<Content>>,
    val mangas: Flow<PagingData<Content>>,
    val ranobe: Flow<PagingData<Content>>,
    val characters: Flow<PagingData<Content>>,
    val clubs: Flow<PagingData<Content>>,
    val comments: Flow<PagingData<Comment>>,
    val isCensored: Boolean,
    val joinPolicy: ClubJoinPolicy,
    val commentPolicy: ClubCommentPolicy
)

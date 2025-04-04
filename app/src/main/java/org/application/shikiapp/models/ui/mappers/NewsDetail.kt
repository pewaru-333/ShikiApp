package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getLinks

fun News.mapper(comments: Flow<PagingData<Comment>>) = NewsDetail(
    title = topicTitle,
    poster = getLinks(htmlFooter).find { it.contains(".jpg") },
    newsBody = fromHtml(htmlBody),
    userId = user.id,
    userNickname = user.nickname,
    userImage = user.image.x160,
    date = convertDate(createdAt),
    images = getLinks(htmlFooter).filter { it.contains("original") },
    videos = getLinks(htmlFooter).filter { it.contains("youtu") },
    commentsCount = commentsCount,
    comments = comments
)
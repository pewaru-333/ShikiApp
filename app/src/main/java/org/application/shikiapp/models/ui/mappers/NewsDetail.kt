package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getLinks

fun News.mapper(comments: Flow<PagingData<Comment>>) = NewsDetail(
    comments = comments,
    commentsCount = commentsCount,
    date = convertDate(createdAt),
    images = getLinks(htmlFooter).filter { it.contains("original") },
    newsBody = fromHtml(htmlBody),
    poster = getLinks(htmlFooter).find { it.contains(".jpg") },
    title = topicTitle,
    userId = user.id,
    userImage = user.image.x160,
    userNickname = user.nickname,
    videos = getLinks(htmlFooter).filter { it.contains("youtu") },
)
package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.ui.Comment
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getLinks

suspend fun News.mapper(comments: Flow<PagingData<Comment>>) = withContext(Dispatchers.Default) {
    val (images, videos, poster) = getLinks(htmlFooter)

    NewsDetail(
        comments = comments,
        commentsCount = commentsCount,
        date = convertDate(createdAt),
        images = images,
        newsBody = fromHtml(htmlBody),
        poster = poster,
        title = topicTitle,
        userId = user.id,
        userImage = user.image.x160,
        userNickname = user.nickname,
        videos = videos
    )
}
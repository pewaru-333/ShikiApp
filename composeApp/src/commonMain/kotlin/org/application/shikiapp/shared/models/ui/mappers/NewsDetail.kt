package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.data.News
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.NewsDetail
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter

suspend fun News.mapper(comments: Flow<PagingData<Comment>>) = withContext(Dispatchers.Default) {
    val (images, videos, poster) = Formatter.getLinks(htmlFooter)

    NewsDetail(
        comments = comments,
        commentsCount = commentsCount,
        date = Formatter.convertDate(createdAt),
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
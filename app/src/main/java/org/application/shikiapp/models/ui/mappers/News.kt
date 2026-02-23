package org.application.shikiapp.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.models.data.News
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CommentContent
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getLinks

suspend fun News.mapper() = withContext(Dispatchers.Default) {
    val (_, _, poster) = getLinks(htmlFooter)
    val posterUrl = when (poster) {
        is CommentContent.VideoContent -> poster.previewUrl
        is CommentContent.ImageContent -> poster.fullUrl
        else -> BLANK
    }

    org.application.shikiapp.models.ui.list.News(
        id = id,
        title = topicTitle,
        poster = posterUrl,
        date = convertDate(createdAt),
        author = user.nickname
    )
}
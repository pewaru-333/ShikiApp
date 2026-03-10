package org.application.shikiapp.shared.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.data.News
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.ui.Formatter

suspend fun News.mapper() = withContext(Dispatchers.Default) {
    val (_, _, poster) = Formatter.getLinks(htmlFooter)
    val posterUrl = when (poster) {
        is CommentContent.VideoContent -> poster.previewUrl
        is CommentContent.ImageContent -> poster.fullUrl
        else -> BLANK
    }

    org.application.shikiapp.shared.models.ui.list.News(
        id = id,
        title = topicTitle,
        poster = posterUrl,
        date = Formatter.convertDate(createdAt),
        author = user.nickname
    )
}
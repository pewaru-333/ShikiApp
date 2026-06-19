package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.News
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.ui.Formatter

suspend fun News.mapper(): org.application.shikiapp.shared.models.ui.list.News {
    val (_, _, poster) = Formatter.getLinks(htmlFooter)
    val posterUrl = when (poster) {
        is CommentContent.VideoContent -> poster.previewUrl
        is CommentContent.ImageContent -> poster.fullUrl
        else -> BLANK
    }

    return org.application.shikiapp.shared.models.ui.list.News(
        id = id,
        title = topicTitle,
        poster = posterUrl,
        date = Formatter.convertDate(createdAt),
        author = user.nickname
    )
}
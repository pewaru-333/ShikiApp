package org.application.shikiapp.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.utils.HtmlParser
import org.application.shikiapp.utils.convertDate

suspend fun Comment.mapper() = org.application.shikiapp.models.ui.Comment(
    id = id,
    userId = userId,
    userAvatar = user.image.x160,
    userNickname = user.nickname,
    createdAt = convertDate(createdAt),
    commentContent = withContext(Dispatchers.Default) { HtmlParser.parseComment(htmlBody) }
)
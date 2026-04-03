package org.application.shikiapp.shared.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.data.Comment
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser

suspend fun Comment.mapper() = org.application.shikiapp.shared.models.ui.Comment(
    id = id,
    userId = userId,
    userAvatar = user.image.x160,
    userNickname = user.nickname,
    createdAt = Formatter.convertDate(createdAt),
    commentContent = withContext(Dispatchers.Default) { HtmlParser.parseComment(htmlBody) },
    isOfftopic = isOfftopic,
    canBeEdited = canBeEdited,
    type = type?.uppercase()
)
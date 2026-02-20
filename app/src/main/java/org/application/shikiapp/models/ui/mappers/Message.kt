package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.di.Preferences
import org.application.shikiapp.models.data.FullMessage
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.ui.list.Message
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.HtmlParser
import org.application.shikiapp.utils.convertDate

fun org.application.shikiapp.models.data.Dialog.toDialog() = Dialog(
    id = message.id,
    userId = targetUser.id,
    userNickname = targetUser.nickname,
    userAvatar = targetUser.image.x160,
    lastMessage = HtmlParser.parseComment(message.htmlBody.orEmpty()),
    lastDate = convertDate(message.createdAt),
    accountUser = true
)

fun FullMessage.toDialogMessage() = Dialog(
    id = id,
    userId = from.id,
    userNickname = from.nickname,
    userAvatar = from.image.x160,
    lastMessage = HtmlParser.parseComment(htmlBody.orEmpty()),
    lastDate = convertDate(createdAt),
    accountUser = from.id == Preferences.userId
)

fun FullMessage.toNewsMessage() = Message(
    id = id,
    kind = kind,
    read = AsyncData.Success(read),
    body = HtmlParser.parseComment(htmlBody.orEmpty()),
    linked = linked?.toRelated(),
    from = from,
    to = to,
    createdAt = convertDate(createdAt),
    isDeleting = AsyncData.Success(false)
)
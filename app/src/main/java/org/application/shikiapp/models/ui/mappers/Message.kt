package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.FullMessage
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.utils.HtmlParser
import org.application.shikiapp.utils.convertDate

fun org.application.shikiapp.models.data.Dialog.toDialog() = Dialog(
    id = targetUser.nickname,
    userId = targetUser.id,
    userNickname = targetUser.nickname,
    userAvatar = targetUser.image.x160,
    lastMessage = HtmlParser.parseComment(message.htmlBody),
    lastDate = convertDate(message.createdAt)
)

fun FullMessage.toDialogMessage() = Dialog(
    id = id.toString(),
    userId = from.id,
    userNickname = from.nickname,
    userAvatar = from.image.x160,
    lastMessage = HtmlParser.parseComment(htmlBody),
    lastDate = convertDate(createdAt)
)
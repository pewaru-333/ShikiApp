package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.FullMessage
import org.application.shikiapp.shared.models.ui.list.Dialog
import org.application.shikiapp.shared.models.ui.list.Message
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.extensions.getLastMessage
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser

fun org.application.shikiapp.shared.models.data.Dialog.toDialog(): Dialog {
    val messageBody = message.htmlBody.orEmpty()
    val commentContents = HtmlParser.parseComment(messageBody)

    return Dialog(
        id = message.id,
        userId = targetUser.id,
        userNickname = targetUser.nickname,
        userAvatar = targetUser.image.x160,
        lastMessages = commentContents,
        lastMessage = commentContents.getLastMessage(),
        lastDate = Formatter.convertDate(message.createdAt),
        accountUser = true
    )
}

fun FullMessage.toDialogMessage() = Dialog(
    id = id,
    userId = from.id,
    userNickname = from.nickname,
    userAvatar = from.image.x160,
    lastMessages = HtmlParser.parseComment(htmlBody.orEmpty()),
    lastMessage = ResourceText.StaticString(BLANK),
    lastDate = Formatter.convertDate(createdAt),
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
    createdAt = Formatter.convertDate(createdAt),
    isDeleting = AsyncData.Success(false)
)
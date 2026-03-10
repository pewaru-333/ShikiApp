package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank
import shikiapp.composeapp.generated.resources.text_dialogs
import shikiapp.composeapp.generated.resources.text_news
import shikiapp.composeapp.generated.resources.text_notifications

enum class MessageType(val title: StringResource) {
    INBOX(Res.string.text_dialogs),
    PRIVATE(Res.string.blank),
    SENT(Res.string.blank),
    NEWS(Res.string.text_news),
    NOTIFICATIONS(Res.string.text_notifications);

    companion object {
        val tabs = listOf(INBOX, NEWS, NOTIFICATIONS)
    }
}
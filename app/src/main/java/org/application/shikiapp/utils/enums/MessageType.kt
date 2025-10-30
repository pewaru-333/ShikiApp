package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class MessageType(@StringRes val title: Int) {
    INBOX(R.string.text_dialogs),
    PRIVATE(R.string.blank),
    SENT(R.string.blank),
    NEWS(R.string.text_news),
    NOTIFICATIONS(R.string.text_notifications);

    companion object {
        val tabs = listOf(INBOX, NEWS, NOTIFICATIONS)
    }
}
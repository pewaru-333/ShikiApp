package org.application.shikiapp.models.states

import org.application.shikiapp.utils.BLANK

data class UserMessagesState(
    val dialogId: String? = null,
    val toDeleteId: String? = null,
    val userId: Long = 0L,
    val text: String = BLANK
)

val UserMessagesState.showDialogDelete: Boolean
    get() = toDeleteId != null
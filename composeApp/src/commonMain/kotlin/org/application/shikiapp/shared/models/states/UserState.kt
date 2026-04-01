package org.application.shikiapp.shared.models.states

data class UserState(
    val isFriend: Boolean = false,
    override val isSendingComment: Boolean = false,
    val showDeleteUserDialog: Boolean = false,
    val unreadMessages: UnreadMessages = UnreadMessages(),
    override val dialogState: BaseDialogState? = null
) : BaseState<UserState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)

    data class UnreadMessages(
        val messages: Int = 0,
        val news: Int = 0,
        val notifications: Int = 0
    ) {
        val total: Int
            get() = messages + news + notifications
    }
}

val UserState.contentMenu: BaseDialogState.User.Menu?
    get() = dialogState as? BaseDialogState.User.Menu

val UserState.showContent: Boolean
    get() = dialogState is BaseDialogState.User.Menu

val UserState.showDialogs: Boolean
    get() = dialogState is BaseDialogState.User.DialogAll || dialogState is BaseDialogState.User.DialogUser
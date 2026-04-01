package org.application.shikiapp.shared.models.states

data class ClubState(
    val image: String? = null,
    val isMember: Boolean = false,
    override val isSendingComment: Boolean = false,
    override val dialogState: BaseDialogState? = null
) : BaseState<ClubState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)
}

val ClubState.showContent: Boolean
    get() = dialogState is BaseDialogState.Club.Image || dialogState is BaseDialogState.Club.Menu
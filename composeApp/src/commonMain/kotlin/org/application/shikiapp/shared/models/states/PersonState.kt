package org.application.shikiapp.shared.models.states

data class PersonState(
    override val isSendingComment: Boolean = false,
    override val dialogState: BaseDialogState? = null
) : BaseState<PersonState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)
}
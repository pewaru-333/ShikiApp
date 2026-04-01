package org.application.shikiapp.shared.models.states

data class CharacterState(
    override val isSendingComment: Boolean = false,
    override val dialogState: BaseDialogState? = null
) : BaseState<CharacterState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)
}
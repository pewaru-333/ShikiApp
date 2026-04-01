package org.application.shikiapp.shared.models.states

data class MangaState(
    override val isSendingComment: Boolean = false,
    override val dialogState: BaseDialogState? = null
) : BaseState<MangaState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)
}

val MangaState.showAuthors: Boolean
    get() = dialogState is BaseDialogState.Media.Authors

val MangaState.showCharacters: Boolean
    get() = dialogState is BaseDialogState.Media.Characters
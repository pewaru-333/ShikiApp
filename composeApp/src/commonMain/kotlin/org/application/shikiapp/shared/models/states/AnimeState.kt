package org.application.shikiapp.shared.models.states

data class AnimeState(
    val screenshot: Int = 0,
    override val isSendingComment: Boolean = false,
    override val dialogState: BaseDialogState? = null
) : BaseState<AnimeState> {
    override fun updateSendingState(isSending: Boolean) = copy(isSendingComment = isSending)
    override fun updateDialogState(dialogState: BaseDialogState?) = copy(dialogState = dialogState)
}

val AnimeState.showAuthors: Boolean
    get() = dialogState is BaseDialogState.Media.Authors

val AnimeState.showCharacters: Boolean
    get() = dialogState is BaseDialogState.Media.Characters

val AnimeState.showFandubbers: Boolean
    get() = dialogState is BaseDialogState.Anime.Fandubbers

val AnimeState.showFansubbers: Boolean
    get() = dialogState is BaseDialogState.Anime.Fansubbers

val AnimeState.showScreenshots: Boolean
    get() = dialogState is BaseDialogState.Anime.Screenshots || dialogState is BaseDialogState.Media.Image && dialogState.parentDialog != null

val AnimeState.showSheetContent: Boolean
    get() = showFansubbers || showFandubbers
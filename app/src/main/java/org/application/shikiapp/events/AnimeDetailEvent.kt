package org.application.shikiapp.events

sealed interface AnimeDetailEvent: ContentDetailEvent {
    data object Reload : AnimeDetailEvent

    data object ShowCharacters : AnimeDetailEvent
    data object ShowAuthors : AnimeDetailEvent
    data object ShowScreenshots : AnimeDetailEvent
    data object ShowVideo : AnimeDetailEvent
    data object ShowRate : AnimeDetailEvent

    data class ShowScreenshot(val index: Int = 0) : AnimeDetailEvent
    data class SetScreenshot(val index: Int) : AnimeDetailEvent

    data class ToggleFavourite(val favoured: Boolean) : AnimeDetailEvent
}
package org.application.shikiapp.events

import org.application.type.MangaKindEnum

sealed interface MangaDetailEvent : ContentDetailEvent {
    data object Reload : MangaDetailEvent

    data object ShowCharacters : MangaDetailEvent
    data object ShowAuthors : MangaDetailEvent
    data object ShowRate : MangaDetailEvent

    data class ToggleFavourite(val type: MangaKindEnum?, val favoured: Boolean) : MangaDetailEvent
}
package org.application.shikiapp.events

sealed interface CharacterDetailEvent : ContentDetailEvent {
    data object ShowAnime : CharacterDetailEvent
    data object ShowManga : CharacterDetailEvent
    data object ShowSeyu : CharacterDetailEvent
    data object HideAll : CharacterDetailEvent

    data class ToggleFavourite(val favoured: Boolean) : CharacterDetailEvent
}
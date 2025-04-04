package org.application.shikiapp.events

sealed interface PersonDetailEvent : ContentDetailEvent {
    data object ShowRoles : PersonDetailEvent
    data object ShowCharacters : PersonDetailEvent

    data class ToggleFavourite(val kind: String, val favoured: Boolean) : PersonDetailEvent
}
package org.application.shikiapp.events

sealed interface ContentDetailEvent {
    data object ShowSheet : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent, PersonDetailEvent
    data object ShowComments : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent, CharacterDetailEvent, PersonDetailEvent, NewsDetailEvent
    data object ShowRelated : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent
    data object ShowSimilar : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent
    data object ShowStats : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent
    data object ShowLinks : ContentDetailEvent, AnimeDetailEvent, MangaDetailEvent
    data object ShowRate : ContentDetailEvent

    data class ToggleFavourite(val favoured: Boolean) : ContentDetailEvent
}
package org.application.shikiapp.events

sealed interface NewsDetailEvent : ContentDetailEvent {
    data class ShowImage(val index: Int = 0) : NewsDetailEvent, ContentDetailEvent
    data class SetImage(val index: Int = 0) : NewsDetailEvent, ContentDetailEvent
}
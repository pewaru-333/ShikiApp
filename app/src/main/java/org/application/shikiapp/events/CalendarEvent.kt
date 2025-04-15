package org.application.shikiapp.events

sealed interface CalendarEvent {
    data object ShowFullUpdates : CalendarEvent
}
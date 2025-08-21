package org.application.shikiapp.events

sealed interface CalendarEvent {
    data object Reload : CalendarEvent
    data object ShowFullUpdates : CalendarEvent
}
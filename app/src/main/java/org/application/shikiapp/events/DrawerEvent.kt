package org.application.shikiapp.events

sealed interface DrawerEvent {
    data object Clear : DrawerEvent
    data object Click : DrawerEvent
}
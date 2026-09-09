package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import org.application.shikiapp.shared.utils.ui.VideoPlayerController

actual fun Modifier.playerKeyEvents(controller: VideoPlayerController) = onPreviewKeyEvent { event ->
    if (controller.controls.expandedEpisodes) return@onPreviewKeyEvent false

    when (event.type) {
        KeyEventType.KeyDown -> when (event.key) {
            Key.Tab -> {
                controller.controls.refreshInteractionMillis()
                false
            }

            Key.DirectionUp, Key.DirectionDown -> {
                val sign = if (event.key == Key.DirectionUp) 1.0f else -1.0f
                controller.setVolume((controller.state.volume + sign * 0.05f))
                controller.controls.showVolume()
                true
            }

            else -> false
        }

        KeyEventType.KeyUp -> when (event.key) {
            Key.J -> { controller.showSubtitles(); true }
            Key.F -> { controller.toggleFullscreen(); true }
            Key.Spacebar -> { controller.togglePlayPause(); true }
            Key.DirectionLeft -> { controller.seek(-10f); true }
            Key.DirectionRight -> { controller.seek(10f); true }

            else -> false
        }

        else -> false
    }
}


actual fun Modifier.playerMouseEvents(controller: VideoPlayerController) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)

            when (event.type) {
                PointerEventType.Move -> controller.controls.refreshInteractionMillis()

                PointerEventType.Release -> {
                    if (event.changes.none(PointerInputChange::isConsumed) && !controller.controls.isSliderDragging) {
                        controller.togglePlayPause()
                        event.changes.forEach(PointerInputChange::consume)
                    }
                }
            }
        }
    }
}

actual fun Modifier.playerFocusRequest(onRequest: () -> Unit) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.type == PointerEventType.Press) {
                onRequest()
            }
        }
    }
}
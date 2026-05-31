package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

actual fun Modifier.playerKeyEvents(playerState: VideoPlayerState) = onPreviewKeyEvent { event ->
    if (playerState.controls.expandedEpisodes) return@onPreviewKeyEvent false

    when (event.type) {
        KeyEventType.KeyDown -> when (event.key) {
            Key.Tab -> {
                playerState.controls.refreshInteractionMillis()
                false
            }

            Key.DirectionUp, Key.DirectionDown -> {
                val sign = if (event.key == Key.DirectionUp) 1.0f else -1.0f
                playerState.setVolume((playerState.volume + sign * 0.05f))
                playerState.controls.hideVolume()
                true
            }

            else -> false
        }

        KeyEventType.KeyUp -> when (event.key) {
            Key.J -> { playerState.showSubtitles(); true }
            Key.F -> { playerState.toggleFullscreen(); true }
            Key.Spacebar -> { playerState.togglePlayPause(); true }
            Key.DirectionLeft -> { playerState.seekTo(playerState.currentTime - 10f); true }
            Key.DirectionRight -> { playerState.seekTo(playerState.currentTime + 10f); true }

            else -> false
        }

        else -> false
    }
}


actual fun Modifier.playerMouseEvents(playerState: VideoPlayerState) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)

            when (event.type) {
                PointerEventType.Move -> playerState.controls.refreshInteractionMillis()

                PointerEventType.Release -> {
                    if (event.changes.none(PointerInputChange::isConsumed)) {
                        playerState.togglePlayPause()
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
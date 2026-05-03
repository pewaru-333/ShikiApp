package org.application.shikiapp.shared.utils.extensions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

actual fun Modifier.playerKeyEvents(playerState: VideoPlayerState) = onPreviewKeyEvent { event ->
    if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false

    when (event.key) {
        Key.J -> playerState.showSubtitles()
        Key.F -> playerState.toggleFullscreen()
        Key.Spacebar -> playerState.togglePlayPause()
        Key.DirectionLeft -> playerState.seekTo(playerState.currentTime - 10f)
        Key.DirectionRight -> playerState.seekTo(playerState.currentTime + 10f)
        else -> return@onPreviewKeyEvent false
    }

    true
}

actual fun Modifier.playerMouseEvents(playerState: VideoPlayerState) = composed {
    val scope = rememberCoroutineScope()
    val interactionFlow = remember {
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(playerState.isPlaying) {
        interactionFlow.collectLatest {
            if (!playerState.controls.isControlsVisible) {
                playerState.controls.showControls()
            }

            if (playerState.isPlaying) {
                delay(3000L)
                playerState.controls.hideControls()
            }
        }
    }

    pointerInput(Unit) {
        var volumeDragJob: Job? = null

        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)

                when (event.type) {
                    PointerEventType.Move -> interactionFlow.tryEmit(Unit)

                    PointerEventType.Release -> {
                        if (event.changes.none(PointerInputChange::isConsumed)) {
                            playerState.togglePlayPause()
                            event.changes.forEach(PointerInputChange::consume)
                        }
                    }

                    PointerEventType.Scroll -> {
                        if (event.changes.none(PointerInputChange::isConsumed)) {
                            val delta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                            if (delta != 0f) {
                                playerState.setVolume((playerState.volume - delta / 20f))

                                volumeDragJob?.cancel()
                                volumeDragJob = scope.launch {
                                    playerState.controls.isVolumeDragging = true
                                    delay(2000L)
                                    playerState.controls.isVolumeDragging = false
                                }

                                event.changes.forEach { it.consume() }
                            }
                        }
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
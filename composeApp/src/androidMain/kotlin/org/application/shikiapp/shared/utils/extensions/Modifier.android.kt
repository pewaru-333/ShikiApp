package org.application.shikiapp.shared.utils.extensions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.shared.utils.isTV
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

actual fun Modifier.playerKeyEvents(playerState: VideoPlayerState): Modifier = composed {
    if (!isTV()) this
    else {
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

        onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                interactionFlow.tryEmit(Unit)
            }

            if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false

            when (event.key) {
                Key.MediaPlayPause, Key.MediaPlay, Key.MediaPause -> {
                    playerState.togglePlayPause()
                    true
                }

                Key.MediaFastForward -> {
                    playerState.seekTo(playerState.currentTime + 10f)
                    true
                }

                Key.MediaRewind -> {
                    playerState.seekTo(playerState.currentTime - 10f)
                    true
                }

                Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.togglePlayPause()
                        true
                    }
                }

                Key.DirectionLeft -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.seekTo(playerState.currentTime - 10f)
                        true
                    }
                }

                Key.DirectionRight -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.seekTo(playerState.currentTime + 10f)
                        true
                    }
                }

                else -> false
            }
        }
    }
}

actual fun Modifier.playerMouseEvents(playerState: VideoPlayerState) = this
actual fun Modifier.playerFocusRequest(onRequest: () -> Unit): Modifier = composed {
    if (!isTV()) this
    else {
        LaunchedEffect(Unit) {
            try { onRequest() } catch (_: Exception) { }
        }

        this
    }
}
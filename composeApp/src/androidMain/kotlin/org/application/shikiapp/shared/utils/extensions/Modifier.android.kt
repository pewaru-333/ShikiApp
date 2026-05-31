package org.application.shikiapp.shared.utils.extensions

import android.view.KeyEvent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

actual fun Modifier.playerKeyEvents(playerState: VideoPlayerState) =
    if (playerState.controls.utils.isTV) {
        onPreviewKeyEvent { event ->
            if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                playerState.controls.refreshInteractionMillis()
            }

            if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) return@onPreviewKeyEvent false

            when (event.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.seekTo(playerState.currentTime + 10f)
                        true
                    }
                }

                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.seekTo(playerState.currentTime - 10f)
                        true
                    }
                }

                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                    if (playerState.controls.isControlsVisible) false
                    else {
                        playerState.togglePlayPause()
                        playerState.controls.showControls()
                        true
                    }
                }

                else -> false
            }
        }
    } else this


actual fun Modifier.playerMouseEvents(playerState: VideoPlayerState) = this
actual fun Modifier.playerFocusRequest(onRequest: () -> Unit) = composed {
    LaunchedEffect(Unit) {
        try { onRequest() } catch (_: Exception) { }
    }

    this
}
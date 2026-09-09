package org.application.shikiapp.shared.utils.extensions

import android.view.KeyEvent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import org.application.shikiapp.shared.utils.ui.VideoPlayerController

actual fun Modifier.playerKeyEvents(controller: VideoPlayerController) =
    if (controller.feature.isTV) {
        onPreviewKeyEvent { event ->
            if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                controller.controls.refreshInteractionMillis()
            }

            if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) return@onPreviewKeyEvent false

            when (event.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                    if (controller.controls.isControlsVisible) false
                    else {
                        controller.seek(10f)
                        true
                    }
                }

                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
                    if (controller.controls.isControlsVisible) false
                    else {
                        controller.seek(-10f)
                        true
                    }
                }

                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                    if (controller.controls.isControlsVisible) false
                    else {
                        controller.togglePlayPause()
                        controller.controls.showControls()
                        true
                    }
                }

                else -> false
            }
        }
    } else this


actual fun Modifier.playerMouseEvents(controller: VideoPlayerController) = this
actual fun Modifier.playerFocusRequest(onRequest: () -> Unit) = composed {
    LaunchedEffect(Unit) {
        try {
            onRequest()
        } catch (_: Exception) {
        }
    }

    this
}
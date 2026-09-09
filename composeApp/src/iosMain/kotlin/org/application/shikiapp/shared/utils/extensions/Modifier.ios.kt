package org.application.shikiapp.shared.utils.extensions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import org.application.shikiapp.shared.utils.ui.VideoPlayerController

actual fun Modifier.playerKeyEvents(controller: VideoPlayerController) = this
actual fun Modifier.playerMouseEvents(controller: VideoPlayerController) = this
actual fun Modifier.playerFocusRequest(onRequest: () -> Unit) = composed {
    LaunchedEffect(Unit) {
        try { onRequest() } catch (_: Exception) { }
    }

    this
}
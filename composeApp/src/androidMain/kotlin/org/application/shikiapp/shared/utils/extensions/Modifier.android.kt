package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.Modifier
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

actual fun Modifier.playerKeyEvents(playerState: VideoPlayerState) = this
actual fun Modifier.playerMouseEvents(playerState: VideoPlayerState) = this
actual fun Modifier.playerFocusRequest(onRequest: () -> Unit) = this
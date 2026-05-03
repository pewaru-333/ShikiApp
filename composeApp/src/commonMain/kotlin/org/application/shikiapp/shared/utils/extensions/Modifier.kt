package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.Modifier
import org.application.shikiapp.shared.utils.ui.VideoPlayerState

expect fun Modifier.playerKeyEvents(playerState: VideoPlayerState): Modifier
expect fun Modifier.playerMouseEvents(playerState: VideoPlayerState): Modifier
expect fun Modifier.playerFocusRequest(onRequest: () -> Unit): Modifier
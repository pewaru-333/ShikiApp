package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.Modifier
import org.application.shikiapp.shared.utils.ui.VideoPlayerController

expect fun Modifier.playerKeyEvents(controller: VideoPlayerController): Modifier
expect fun Modifier.playerMouseEvents(controller: VideoPlayerController): Modifier
expect fun Modifier.playerFocusRequest(onRequest: () -> Unit): Modifier
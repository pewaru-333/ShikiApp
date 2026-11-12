package org.application.shikiapp.shared.utils.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.onSizeChanged
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.player.Player
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage

actual class VideoPlayerController : VideoPlayer() {
    private val player = Player.create(state, ::onEvent)

    internal var isFullscreen by mutableStateOf(false)
        private set

    actual override val feature = object : VideoPlayerFeature {
        override val isTV = false
        override val pictureInPicture = null
        override val showPlayPause = false
        override val doubleTapToSeek = false
        override val visibilityDelay = 3000L
        override val pointerIcon = PointerIcon(
            Toolkit.getDefaultToolkit().createCustomCursor(
                BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), Point(0, 0), BLANK
            )
        )
    }

    actual override fun create() = Unit
    actual override fun release() = player.close()

    actual override fun onLoadVideo(url: String) = player.load(url)

    actual override fun onPlay() = player.play()
    actual override fun onPause() = player.pause()

    actual override fun onSetVolume(volume: Float) {
        updateVolume(volume)
        player.volume(volume)
    }

    actual override fun onSetSpeed(speed: Float) {
        updateSpeed(speed)
        player.speed(speed)
    }

    actual override fun onSeek(millis: Float) = player.seek(millis)
    actual override fun onLoadAudioTrack(index: Int) = player.audio(index)
    actual override fun onLoadSubtitleTrack(index: Int) = player.subtitle(index)

    internal fun <R> withImage(block: (Image?) -> R) = player.withImage(block)

    internal fun resize(width: Int, height: Int) = player.resize(width, height)

    internal fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }
}

@Composable
actual fun rememberVideoPlayerController(onEvent: (PlayerEvent) -> Unit): VideoPlayerController {
    val currentEvent by rememberUpdatedState(onEvent)

    return remember {
        VideoPlayerController().apply {
            eventListener = { currentEvent(it) }
        }
    }
}

@Composable
actual fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier) {
    val windowManager = LocalWindowManager.current
    val zoom by animateFloatAsState(
        targetValue = if (controller.state.isZoomed) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(controller.isFullscreen) {
        if (controller.isFullscreen != windowManager.isFullscreen) {
            windowManager.toggleFullscreen()
        }
    }

    DisposableEffect(controller, windowManager) {
        onDispose { windowManager.exitFullscreen() }
    }

    Box(modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .onSizeChanged { controller.resize(it.width, it.height) }
        ) {
            controller.withImage { image ->
                if (image == null || size.width <= 0f || size.height <= 0f) return@withImage

                val fit = minOf(size.width / image.width, size.height / image.height)
                val crop = maxOf(size.width / image.width, size.height / image.height)
                val scale = fit + zoom * (crop - fit)
                val width = image.width * scale
                val height = image.height * scale
                val left = (size.width - width) / 2f
                val top = (size.height - height) / 2f

                drawIntoCanvas {
                    it.skiaCanvas.drawImageRect(image, Rect.makeXYWH(left, top, width, height))
                }
            }
        }
    }
}
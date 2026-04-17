package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Stable
class VideoPlayerState {
    var url by mutableStateOf<String?>(null)
        internal set
    var isLoading by mutableStateOf(true)
        internal set
    var isPlaying by mutableStateOf(true)
        internal set
    var volume by mutableFloatStateOf(1f)
        internal set
    var speed by mutableFloatStateOf(1f)
        internal set
    var currentTime by mutableFloatStateOf(0f)
        internal set
    var totalTime by mutableFloatStateOf(0f)
        internal set
    var bufferPercentage by mutableFloatStateOf(0f)
        internal set
    var isZoomed by mutableStateOf(false)
        internal set
    var isFullscreen by mutableStateOf(false) // desktop only
        internal set

    internal var seekTrigger by mutableStateOf<Float?>(null)

    fun loadUrl(newUrl: String) {
        url = newUrl
        currentTime = 0f
        totalTime = 0f
        bufferPercentage = 0f
        seekTrigger = null
        isLoading = true
    }

    fun pause() {
        isPlaying = false
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
    }

    fun toggleZoom() {
        isZoomed = !isZoomed
    }

    fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
    }

    fun setSpeed(newSpeed: Float) {
        speed = newSpeed
    }

    fun seekTo(seconds: Float) {
        val target = seconds.coerceIn(0f, totalTime.takeIf { it > 0f } ?: Float.MAX_VALUE)
        seekTrigger = target
        currentTime = target
    }

    internal fun updateTime(current: Float, total: Float) {
        currentTime = current
        totalTime = total
    }

    internal fun updateBuffer(percent: Float) {
        bufferPercentage = percent.coerceIn(0f, 1f)
    }

    internal fun clearSeekTrigger() {
        seekTrigger = null
    }
}

@Composable
fun rememberVideoPlayerState(): VideoPlayerState {
    return remember { VideoPlayerState() }
}

@Composable
expect fun VideoPlayer(state: VideoPlayerState, modifier: Modifier = Modifier)
package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.di.AppleContext
import org.application.shikiapp.shared.di.PlatformContext
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.CMTimeRangeValue
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.loadedTimeRanges
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.volume
import platform.CoreGraphics.CGRectZero
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.QuartzCore.CALayer
import platform.UIKit.UIView
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val player = remember { AVPlayer() }

    LaunchedEffect(state.url) {
        val currentUrl = state.url ?: return@LaunchedEffect
        val nsUrl = NSURL.URLWithString(currentUrl) ?: return@LaunchedEffect

        val options = mutableMapOf<Any?, Any?>()
        if (state.headers.isNotEmpty()) {
            options["AVURLAssetHTTPHeaderFieldsKey"] = state.headers
        }

        val asset = AVURLAsset(nsUrl, options)
        val playerItem = AVPlayerItem.playerItemWithAsset(asset)

        player.replaceCurrentItemWithPlayerItem(playerItem)

        if (state.isPlaying) {
            player.play()
        }
    }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) player.play() else player.pause()
    }

    LaunchedEffect(state.volume) {
        player.volume = state.volume
    }

    LaunchedEffect(state.speed) {
        player.rate = state.speed
    }

    LaunchedEffect(state.seekTrigger) {
        state.seekTrigger?.let { seconds ->
            player.seekToTime(
                time = CMTimeMakeWithSeconds(seconds.toDouble(), 1000),
                toleranceBefore = kCMTimeZero.readValue(),
                toleranceAfter = kCMTimeZero.readValue()
            )
        }
    }

    LaunchedEffect(player) {
        while (true) {
            player.currentItem?.let { currentItem ->
                val currentTime = CMTimeGetSeconds(player.currentTime())
                val totalDuration = CMTimeGetSeconds(currentItem.duration)

                val isValid = !totalDuration.isNaN() && totalDuration > 0.0
                if (isValid && player.timeControlStatus == AVPlayerTimeControlStatusPlaying) {
                    state.updateTime(currentTime.toFloat(), totalDuration.toFloat())
                }

                val firstRange = currentItem.loadedTimeRanges.firstOrNull() as? NSValue
                if (firstRange != null && isValid) {
                    firstRange.CMTimeRangeValue.useContents {
                        val bufferStart = CMTimeGetSeconds(start.readValue())
                        val bufferDuration = CMTimeGetSeconds(duration.readValue())

                        val bufferEnd = bufferStart + bufferDuration
                        val bufferPercentage = (bufferEnd / totalDuration).toFloat()

                        state.updateBuffer(bufferPercentage)
                    }
                }
            }

            delay(500.milliseconds)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
        }
    }

    UIKitView(
        modifier = modifier,
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        ),
        factory = {
            val view = PlayerUIView().apply {
                clipsToBounds = true
            }

            val playerLayer = AVPlayerLayer().apply {
                this.player = player
                videoGravity = if (state.isZoomed) AVLayerVideoGravityResizeAspectFill
                else AVLayerVideoGravityResizeAspect
            }

            view.layer.addSublayer(playerLayer)
            view
        },
        update = { view ->
            val layer = view.layer.sublayers?.firstOrNull { it is AVPlayerLayer } as? AVPlayerLayer

            layer?.videoGravity = (if (state.isZoomed) AVLayerVideoGravityResizeAspectFill
            else AVLayerVideoGravityResizeAspect)
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class PlayerUIView : UIView(CGRectZero.readValue()) {
    override fun layoutSubviews() {
        super.layoutSubviews()

        layer.sublayers?.forEach {
            (it as? CALayer)?.frame = bounds
        }
    }
}

actual class VideoPlayerUtils actual constructor(context: PlatformContext) {
    actual val isTV = false
    actual val showPlayPause = true
    actual val visibilityDelay = 3000L
    actual val pointerIcon = PointerIcon.Default

    actual constructor() : this(AppleContext())
}
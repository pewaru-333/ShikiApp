@file:OptIn(ExperimentalForeignApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.*
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.ui.subtitles.ComposeSubtitleLayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.AVKit.AVPictureInPictureController
import platform.CoreGraphics.CGRectZero
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.UIKit.*
import platform.darwin.NSObjectProtocol
import platform.darwin.dispatch_get_main_queue
import kotlin.time.Duration.Companion.seconds

actual class VideoPlayerController: VideoPlayer(), VideoPlayer.VideoPlayerPictureInPicture {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    internal val player = AVPlayer()

    internal var pipController: AVPictureInPictureController? = null

    private var resignObserver: NSObjectProtocol? = null
    private var backgroundObserver: NSObjectProtocol? = null
    private var timeObserver: Any? = null
    private var fallbackJob: Job? = null


    override val feature = object : VideoPlayerFeature {
        override val pictureInPicture =
            if (AVPictureInPictureController.isPictureInPictureSupported()) this@VideoPlayerController
            else null

        override val isTV = false
        override val showPlayPause = true
        override val visibilityDelay = 3000L
        override val pointerIcon = PointerIcon.Default
    }


    override fun create() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (_: Exception) {

        }

        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        resignObserver = center.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = queue,
            usingBlock = {
                if (pipController?.isPictureInPictureActive() == false) {
                    pause()
                }
            }
        )

        backgroundObserver = center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = queue,
            usingBlock = {
                if (pipController?.isPictureInPictureActive() == false) {
                    pause()
                }
            }
        )

        timeObserver = player.addPeriodicTimeObserverForInterval(
            interval = CMTimeMakeWithSeconds(1.0, 1000),
            queue = dispatch_get_main_queue(),
            usingBlock = { updatePlayerState() }
        )
    }

    override fun release() {
        val center = NSNotificationCenter.defaultCenter

        backgroundObserver?.let { center.removeObserver(it) }
        resignObserver?.let { center.removeObserver(it) }
        timeObserver?.let { player.removeTimeObserver(it) }

        player.pause()
        player.replaceCurrentItemWithPlayerItem(null)

        coroutineScope.cancel()
    }

    override fun onLoadVideo(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return

        val options = mutableMapOf<Any?, Any?>()
        if (state.headers.isNotEmpty()) {
            options["AVURLAssetHTTPHeaderFieldsKey"] = state.headers
        }

        val asset = AVURLAsset(nsUrl, options)
        val playerItem = AVPlayerItem.playerItemWithAsset(asset)

        player.replaceCurrentItemWithPlayerItem(playerItem)
        player.play()

        fallbackJob?.cancel()
        fallbackJob = coroutineScope.launch {
            delay(10.seconds)

            if (state.isPlaying) return@launch

            val isActuallyPlaying = player.timeControlStatus == AVPlayerTimeControlStatusPlaying
            val currentTime = CMTimeGetSeconds(player.currentTime())
            val hasStarted = !currentTime.isNaN() && currentTime > 0.1

            if (!isActuallyPlaying && !hasStarted) {
                player.pause()
                player.replaceCurrentItemWithPlayerItem(null)
                playNext()
            }
        }
    }

    override fun onPlay() {
        player.play()

        state.isPlaying = true
    }

    override fun onPause() {
        player.pause()

        state.isPlaying = false
    }

    override fun onSetVolume(volume: Float) {
        player.volume = volume

        updateVolume(volume)
    }

    override fun onSetSpeed(speed: Float) {
        player.rate = speed

        updateSpeed(speed)
    }

    override fun onSeek(millis: Float) {
        player.seekToTime(
            time = CMTimeMakeWithSeconds(millis.toDouble(), 1000),
            toleranceBefore = kCMTimeZero.readValue(),
            toleranceAfter = kCMTimeZero.readValue()
        )
    }

    override fun onLoadAudioTrack(index: Int) {
        val currentItem = player.currentItem ?: return
        val asset = currentItem.asset

        val audioGroup = asset.mediaSelectionGroupForMediaCharacteristic(AVMediaCharacteristicAudible) ?: return
        val options = audioGroup.options

        if (index >= 0 && index < options.size) {
            val selectedOption = options[index] as? AVMediaSelectionOption ?: return

            val currentSelection = currentItem.currentMediaSelection.selectedMediaOptionInMediaSelectionGroup(audioGroup)
            if (currentSelection == selectedOption) return

            currentItem.selectMediaOption(selectedOption, audioGroup)
        }
    }

    override fun onLoadSubtitleTrack(index: Int) {
        val currentItem = player.currentItem ?: return
        val asset = currentItem.asset

        val subtitleGroup = asset.mediaSelectionGroupForMediaCharacteristic(AVMediaCharacteristicLegible) ?: return
        val options = subtitleGroup.options

        if (index == 0 && subtitleGroup.allowsEmptySelection) {
            currentItem.selectMediaOption(null, subtitleGroup)
            return
        }

        val optionIndex = index - 1
        if (optionIndex !in options.indices) return

        val selectedOption = options[optionIndex] as? AVMediaSelectionOption ?: return
        val currentOption = currentItem.currentMediaSelection.selectedMediaOptionInMediaSelectionGroup(subtitleGroup)

        if (currentOption?.extendedLanguageTag == selectedOption.extendedLanguageTag) {
            return
        }

        currentItem.selectMediaOption(selectedOption, subtitleGroup)
    }

    override fun enterPIP() {
        val pip = pipController ?: return

        if (!pip.isPictureInPictureActive()) {
            pip.startPictureInPicture()
            controls.hideControls()
        }
    }

    private fun updatePlayerState() {
        val item = player.currentItem ?: return

        val isReady = item.status == AVPlayerItemStatusReadyToPlay
        val isWaiting = player.timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate

        state.isLoading = !isReady || isWaiting
        state.isPlaying = player.timeControlStatus == AVPlayerTimeControlStatusPlaying

        val current = CMTimeGetSeconds(player.currentTime())
        val duration = CMTimeGetSeconds(item.duration)

        if (!duration.isNaN() && duration > 0) {
            state.currentTime = current.toFloat()
            state.totalTime = duration.toFloat()

            val range = item.loadedTimeRanges.firstOrNull() as? NSValue ?: return
            range.CMTimeRangeValue.useContents {
                val start = CMTimeGetSeconds(this.start.readValue())
                val bufferDuration = CMTimeGetSeconds(this.duration.readValue())

                updateBuffer(((start + bufferDuration) / duration).toFloat())
            }
        }
    }
}

@Composable
actual fun rememberVideoPlayerController(onEvent: (PlayerEvent) -> Unit): VideoPlayerController {
    val currentEvent by rememberUpdatedState(onEvent)

    return remember {
        VideoPlayerController().apply {
            eventListener = currentEvent
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier) {
    UIKitView(
        modifier = modifier,
        onRelease = { it.player = null },
        factory = {
            PlayerUIView().apply {
                this.player = controller.player
                backgroundColor = UIColor.blackColor

                if (AVPictureInPictureController.isPictureInPictureSupported()) {
                    controller.pipController = AVPictureInPictureController(playerLayer).apply {
                        canStartPictureInPictureAutomaticallyFromInline = true
                    }
                }
            }
        },
        update = { view ->
            view.videoGravity = if (controller.state.isZoomed) AVLayerVideoGravityResizeAspectFill
            else AVLayerVideoGravityResizeAspect
        }
    )

    if (controller.state.selectedSubtitlesTrack != null) {
        val track = controller.state.subtitles.find { it.name == controller.state.selectedSubtitlesTrack }

        ComposeSubtitleLayer(
            currentTimeMs = (controller.state.currentTime * 1000f).toLong(),
            subtitleTrack = track
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PlayerUIView : UIView(CGRectZero.readValue()) {
    companion object : UIViewMeta() {
        override fun layerClass() = AVPlayerLayer
    }

    val playerLayer: AVPlayerLayer
        get() = layer as AVPlayerLayer

    var player: AVPlayer?
        get() = playerLayer.player
        set(value) {
            playerLayer.player = value
        }

    var videoGravity: String?
        get() = playerLayer.videoGravity
        set(value) {
            playerLayer.videoGravity = value
        }
}
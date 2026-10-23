@file:OptIn(ExperimentalForeignApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.*
import kotlinx.io.IOException
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.player.ShikiPlayerObserverProtocol
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.subtitles.ComposeSubtitleLayer
import org.application.shikiapp.shared.utils.ui.subtitles.models.SubtitleCue
import org.application.shikiapp.shared.utils.ui.subtitles.parsers.ParserASS
import org.application.shikiapp.shared.utils.ui.subtitles.parsers.ParserSRT
import org.application.shikiapp.shared.utils.ui.subtitles.parsers.ParserWEBVTT
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.AVKit.AVPictureInPictureController
import platform.AVKit.AVPictureInPictureControllerDelegateProtocol
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringConvertEncodingToNSStringEncoding
import platform.CoreFoundation.CFStringConvertIANACharSetNameToEncoding
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.kCFStringEncodingInvalidId
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreGraphics.CGRectZero
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.*
import platform.UIKit.*
import platform.UIKit.UIApplicationState.UIApplicationStateBackground
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

actual class VideoPlayerController : VideoPlayer(), VideoPlayer.VideoPlayerPictureInPicture {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val observers = ArrayList<NSObjectProtocol>()
    private val itemKeys = listOf("status", "duration", "loadedTimeRanges", "presentationSize", "tracks")
    private var item: AVPlayerItem? = null
    private var timeObserver: Any? = null
    private var playerLayer: AVPlayerLayer? = null
    private var pipController: AVPictureInPictureController? = null
    private var created = false
    private var released = false
    private var isReady = false
    private var playRequested = true
    private var wasPlaying = false
    private var pendingSeek: Double? = null
    private var pipStarting = false
    private var subtitleJob: Job? = null
    private var subtitleSource: Pair<String, Map<String, String>>? = null
    private var subtitleCues by mutableStateOf<List<SubtitleCue>>(emptyList())

    internal val subtitleText by derivedStateOf(structuralEqualityPolicy()) {
        val cues = subtitleCues
        if (cues.isEmpty()) return@derivedStateOf BLANK

        val currentTimeMs = (state.currentTime * 1000f).toLong()
        buildString {
            for (cue in cues) {
                if (!cue.isActive(currentTimeMs)) continue

                if (isNotEmpty()) append('\n')
                append(cue.text)
            }
        }
    }

    internal val player = AVPlayer()

    internal var videoSize by mutableStateOf(Size.Unspecified)
        private set

    private val playerObserver = object : NSObject(), ShikiPlayerObserverProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?,
            ofObject: Any?,
            change: Map<Any?, *>?,
            context: COpaquePointer?
        ) {
            scope.launch {
                if (ofObject == player) {
                    updatePlaybackState()
                } else if (ofObject == item) {
                    updateItemState()

                    if (keyPath == "tracks") {
                        val index = state.audioTrackIndex
                        if (index != null) onLoadAudioTrack(index)
                        disableEmbeddedSubtitles()
                    }
                }
            }
        }
    }

    private val pipDelegate = object : NSObject(), AVPictureInPictureControllerDelegateProtocol {
        override fun pictureInPictureControllerWillStartPictureInPicture(pictureInPictureController: AVPictureInPictureController) {
            pipStarting = true
            controls.hideControls()
        }

        override fun pictureInPictureControllerDidStartPictureInPicture(pictureInPictureController: AVPictureInPictureController) {
            pipStarting = false
        }

        override fun pictureInPictureController(
            pictureInPictureController: AVPictureInPictureController,
            failedToStartPictureInPictureWithError: NSError
        ) {
            pipStarting = false

            if (UIApplication.sharedApplication.applicationState == UIApplicationStateBackground) {
                pause()
            }
        }

        override fun pictureInPictureControllerDidStopPictureInPicture(pictureInPictureController: AVPictureInPictureController) {
            pipStarting = false

            if (UIApplication.sharedApplication.applicationState == UIApplicationStateBackground) {
                pause()
            }
        }

        override fun pictureInPictureController(
            pictureInPictureController: AVPictureInPictureController,
            restoreUserInterfaceForPictureInPictureStopWithCompletionHandler: (Boolean) -> Unit
        ) = restoreUserInterfaceForPictureInPictureStopWithCompletionHandler(!released && playerLayer != null)
    }

    actual override val feature = object : VideoPlayerFeature {
        override val pictureInPicture =
            if (AVPictureInPictureController.isPictureInPictureSupported()) this@VideoPlayerController
            else null

        override val isTV = false
        override val showPlayPause = true
        override val doubleTapToSeek = true
        override val visibilityDelay = 3000L
        override val pointerIcon = PointerIcon.Default
    }

    actual override fun create() {
        if (created || released) return
        created = true

        val audioSession = AVAudioSession.sharedInstance()
        audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
        audioSession.setActive(true, null)

        player.addObserver(playerObserver, "timeControlStatus", NSKeyValueObservingOptionNew, null)
        player.addObserver(playerObserver, "rate", NSKeyValueObservingOptionNew, null)

        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        observers.add(center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = queue,
            usingBlock = {
                scope.launch {
                    delay(500.milliseconds)

                    if (UIApplication.sharedApplication.applicationState == UIApplicationStateBackground && pipController?.isPictureInPictureActive() != true && !pipStarting) {
                        pause()
                    }
                }
            }
        ))

        observers.add(center.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = null,
            queue = queue,
            usingBlock = { notification ->
                val finishedItem = notification?.`object` as? AVPlayerItem
                scope.launch {
                    if (finishedItem == null || finishedItem != item || state.isVideoEnded || pendingSeek != null) return@launch

                    playRequested = false
                    pendingSeek = null

                    state.isLoading = false
                    state.isVideoEnded = true

                    if (state.totalTime > 0f) state.currentTime = state.totalTime

                    updatePlaying(false)
                    onEvent(PlayerEvent.Ended)
                }
            }
        ))

        observers.add(center.addObserverForName(
            name = AVPlayerItemFailedToPlayToEndTimeNotification,
            `object` = null,
            queue = queue,
            usingBlock = { notification ->
                val failedItem = notification?.`object` as? AVPlayerItem
                scope.launch {
                    if (failedItem != null && failedItem == item) fail()
                }
            }
        ))

        timeObserver = player.addPeriodicTimeObserverForInterval(
            interval = CMTimeMakeWithSeconds(0.1, 1000),
            queue = dispatch_get_main_queue(),
            usingBlock = { updateProgress() }
        )
    }

    actual override fun release() {
        if (released) return
        released = true

        if (created) {
            player.removeObserver(playerObserver, "timeControlStatus")
            player.removeObserver(playerObserver, "rate")
        }

        val center = NSNotificationCenter.defaultCenter
        for (observer in observers) {
            center.removeObserver(observer)
        }
        observers.clear()

        val observer = timeObserver
        if (observer != null) {
            player.removeTimeObserver(observer)
            timeObserver = null
        }

        clearPictureInPicture()
        clearSubtitles()
        player.pause()

        replaceItem(null)
        updatePlaying(false)

        state.isLoading = false

        scope.cancel()
        eventListener = null
    }

    actual override fun onLoadVideo(url: String) {
        if (released) return
        create()

        player.pause()

        replaceItem(null)
        updatePlaying(false)

        isReady = false
        playRequested = true
        pendingSeek = null
        videoSize = Size.Unspecified

        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl == null || url.substringBefore('?').substringBefore('#').endsWith(".mpd", ignoreCase = true)) {
            clearSubtitles()
            scope.launch {
                if (state.url == url && item == null) playNext()
            }

            return
        }

        val options = mutableMapOf<Any?, Any?>()
        if (state.headers.isNotEmpty()) {
            options["AVURLAssetHTTPHeaderFieldsKey"] = state.headers
        }

        replaceItem(AVPlayerItem.playerItemWithAsset(AVURLAsset(nsUrl, options)))
        player.play()
        updateItemState()

        loadSubtitles(state.subtitles.firstOrNull { it.name == state.selectedSubtitlesTrack }?.url)
    }

    actual override fun onPlay() {
        if (released) return
        playRequested = true

        if (state.isVideoEnded) {
            onSeek(0f)
        }

        player.play()
        if (isReady) {
            player.rate = state.speed
        }

        updatePlaybackState()
    }

    actual override fun onPause() {
        playRequested = false
        player.pause()

        updatePlaybackState()
    }

    actual override fun onSetVolume(volume: Float) {
        player.volume = volume

        updateVolume(volume)
    }

    actual override fun onSetSpeed(speed: Float) {
        updateSpeed(speed)
        if (isReady && player.timeControlStatus != AVPlayerTimeControlStatusPaused) {
            player.rate = speed
        }
    }

    actual override fun onSeek(millis: Float) {
        if (released) return
        pendingSeek = millis.toDouble()

        if (state.totalTime <= 0f || millis < state.totalTime) {
            state.isVideoEnded = false
        }

        if (isReady) seek()
    }

    actual override fun onLoadAudioTrack(index: Int) {
        if (index < 0) return
        state.audioTrackIndex = index

        val currentItem = item ?: return
        if (!isReady) return

        val group = currentItem.asset.mediaSelectionGroupForMediaCharacteristic(AVMediaCharacteristicAudible) ?: return
        val option = group.options.getOrNull(index) as? AVMediaSelectionOption ?: return
        if (currentItem.currentMediaSelection.selectedMediaOptionInMediaSelectionGroup(group) != option) {
            currentItem.selectMediaOption(option, group)
        }
    }

    actual override fun onLoadSubtitleTrack(index: Int) {
        if (released || index != 0 && index !in state.subtitles.indices) return

        updateSubtitleTrack(index)
        disableEmbeddedSubtitles()
        loadSubtitles(if (index == 0) null else state.subtitles[index].url)
    }

    private fun loadSubtitles(url: String?) {
        if (url == null || released) {
            clearSubtitles()
            return
        }

        val source = url to state.headers.toMap()
        if (subtitleSource == source) return

        clearSubtitles()
        subtitleSource = source

        subtitleJob = scope.launch {
            val cues = try {
                withTimeoutOrNull(30.seconds) {
                    val text = loadSubtitleContent(url, source.second)
                    withContext(Dispatchers.Default) {
                        val content = text.removePrefix("\uFEFF").trim()

                        when {
                            content.startsWith("WEBVTT") -> ParserWEBVTT.parse(content)
                            content.lineSequence().any { it.trim().equals("[Events]", ignoreCase = true) } -> ParserASS.parse(content)
                            else -> ParserSRT.parse(content)
                        }
                    }
                } ?: emptyList()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                emptyList()
            }

            ensureActive()
            subtitleCues = cues
            if (cues.isEmpty()) subtitleSource = null
        }
    }

    private fun clearSubtitles() {
        subtitleJob?.cancel()
        subtitleJob = null
        subtitleSource = null
        subtitleCues = emptyList()
    }

    override fun enterPIP() {
        val pip = pipController ?: return
        if (!pip.isPictureInPictureActive() && pip.isPictureInPicturePossible()) {
            pip.startPictureInPicture()
        }
    }

    internal fun attachPlayerLayer(layer: AVPlayerLayer) {
        if (released || playerLayer == layer) return

        clearPictureInPicture()
        playerLayer = layer

        if (AVPictureInPictureController.isPictureInPictureSupported()) {
            pipController = AVPictureInPictureController(layer).apply {
                delegate = pipDelegate
                canStartPictureInPictureAutomaticallyFromInline = true
            }
        }
    }

    internal fun detachPlayerLayer(layer: AVPlayerLayer) {
        if (playerLayer == layer) clearPictureInPicture()
    }

    private fun clearPictureInPicture() {
        val pip = pipController
        pipController = null
        playerLayer = null
        pipStarting = false

        if (pip != null) {
            pip.delegate = null
            pip.stopPictureInPicture()
        }
    }

    private fun replaceItem(newItem: AVPlayerItem?) {
        val previous = item
        if (previous != null) {
            for (key in itemKeys) {
                previous.removeObserver(playerObserver, key)
            }
            previous.cancelPendingSeeks()
        }

        item = newItem
        player.replaceCurrentItemWithPlayerItem(newItem)

        if (newItem != null) {
            for (key in itemKeys) {
                newItem.addObserver(playerObserver, key, NSKeyValueObservingOptionNew, null)
            }
        }
    }

    private fun updateItemState() {
        val currentItem = item ?: return
        if (currentItem.status == AVPlayerItemStatusFailed) {
            fail()
            return
        }

        if (!isReady && currentItem.status == AVPlayerItemStatusReadyToPlay) {
            isReady = true

            currentItem.asset.loadValuesAsynchronouslyForKeys(
                listOf("availableMediaCharacteristicsWithMediaSelectionOptions")
            ) {
                scope.launch {
                    if (item != currentItem) return@launch

                    val index = state.audioTrackIndex
                    if (index != null) onLoadAudioTrack(index)
                    disableEmbeddedSubtitles()
                }
            }

            if (pendingSeek != null) seek()
            if (playRequested) {
                player.play()
                player.rate = state.speed
            }
        }

        val size = currentItem.presentationSize.useContents {
            if (width > 0 && height > 0) Size(width.toFloat(), height.toFloat())
            else Size.Unspecified
        }
        videoSize = size

        if (size != Size.Unspecified) {
            val height = size.height.roundToInt()
            if (state.qualityList.size <= 1) state.qualityList = listOf(height)
            state.currentQuality = height
        }

        updatePlaybackState()
        updateProgress()
    }

    private fun disableEmbeddedSubtitles() {
        val currentItem = item ?: return
        if (!isReady) return

        val group = currentItem.asset.mediaSelectionGroupForMediaCharacteristic(AVMediaCharacteristicLegible) ?: return
        if (group.allowsEmptySelection) currentItem.selectMediaOption(null, group)
    }

    private fun seek() {
        val currentItem = item ?: return
        val target = pendingSeek ?: return
        val tolerance = CMTimeMakeWithSeconds(0.1, 1000)

        player.seekToTime(
            time = CMTimeMakeWithSeconds(target, 1000),
            toleranceBefore = tolerance,
            toleranceAfter = tolerance,
            completionHandler = { finished ->
                scope.launch {
                    if (finished && item == currentItem && pendingSeek == target) {
                        pendingSeek = null
                        updateProgress()
                    }
                }
            }
        )
    }

    private fun fail() {
        clearSubtitles()
        player.pause()
        replaceItem(null)

        isReady = false
        pendingSeek = null

        updatePlaying(false)
        playNext()
    }

    private fun updatePlaybackState() {
        if (released) return

        val status = player.timeControlStatus
        if (isReady && status == AVPlayerTimeControlStatusPaused) {
            playRequested = false
        }

        state.isLoading = item != null && !state.isVideoEnded &&
            (status == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate || !isReady && playRequested)

        val playing = status == AVPlayerTimeControlStatusPlaying && !state.isVideoEnded
        if (playing && !wasPlaying && player.rate != state.speed) {
            player.rate = state.speed
        }

        updatePlaying(playing)
    }

    private fun updatePlaying(playing: Boolean) {
        state.isPlaying = playing
        if (wasPlaying == playing) return
        wasPlaying = playing

        onEvent(if (playing) PlayerEvent.Play else PlayerEvent.Pause)
    }

    private fun updateProgress() {
        val currentItem = item ?: return
        val current = CMTimeGetSeconds(player.currentTime())
        val duration = CMTimeGetSeconds(currentItem.duration)

        if (duration.isFinite() && duration > 0) {
            state.totalTime = duration.toFloat()
        }

        if (pendingSeek == null && current.isFinite() && current >= 0) {
            state.currentTime = if (state.totalTime > 0f) current.toFloat().coerceAtMost(state.totalTime)
            else current.toFloat()
        }

        if (!duration.isFinite() || duration <= 0 || !current.isFinite() || current < 0) {
            updateBuffer(0f)
            return
        }

        var bufferEnd = current
        for (value in currentItem.loadedTimeRanges) {
            val range = value as? NSValue ?: continue
            range.CMTimeRangeValue.useContents {
                val start = CMTimeGetSeconds(this.start.readValue())
                val end = start + CMTimeGetSeconds(this.duration.readValue())
                if (start <= current + 0.1 && end >= current) {
                    bufferEnd = maxOf(bufferEnd, end)
                }
            }
        }

        updateBuffer((bufferEnd / duration).toFloat())
    }

    @OptIn(BetaInteropApi::class)
    private suspend fun loadSubtitleContent(url: String, headers: Map<String, String>): String {
        val nsUrl = NSURL.URLWithString(url) ?: throw IOException()
        val request = NSMutableURLRequest.requestWithURL(nsUrl)
        request.setTimeoutInterval(30.0)

        for ((name, value) in headers) {
            request.setValue(value, forHTTPHeaderField = name)
        }

        return suspendCancellableCoroutine { continuation ->
            val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
                if (!continuation.isActive) return@dataTaskWithRequest

                val text = try {
                    if (error != null) throw IOException(error.localizedDescription)

                    val httpResponse = response as? NSHTTPURLResponse ?: throw IOException()
                    if (httpResponse.statusCode !in 200L..299L) {
                        throw IOException("Subtitle request failed: HTTP ${httpResponse.statusCode}")
                    }

                    if (data == null) throw IOException()

                    var encoding = NSUTF8StringEncoding
                    val name = httpResponse.textEncodingName
                    if (name != null) {
                        val charset = CFStringCreateWithCString(null, name, kCFStringEncodingUTF8)
                        if (charset != null) {
                            try {
                                val value = CFStringConvertIANACharSetNameToEncoding(charset)
                                if (value != kCFStringEncodingInvalidId) {
                                    encoding = CFStringConvertEncodingToNSStringEncoding(value)
                                }
                            } finally {
                                CFRelease(charset)
                            }
                        }
                    }

                    NSString.create(data, encoding)?.toString() ?: throw IOException()
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                    return@dataTaskWithRequest
                }

                continuation.resume(text)
            }

            continuation.invokeOnCancellation { task.cancel() }
            task.resume()
        }
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
actual fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier) =
    BoxWithConstraints(modifier.clipToBounds(), Alignment.Center) {
        val boxWidth = constraints.maxWidth.toFloat()
        val boxHeight = constraints.maxHeight.toFloat()
        val maxSize = Size(boxWidth, boxHeight)

        val videoSize = controller.videoSize
        val hasVideo = videoSize != Size.Unspecified && videoSize.width > 0f && videoSize.height > 0f

        val fitScale = if (hasVideo) ContentScale.Fit.computeScaleFactor(videoSize, maxSize).scaleX
        else 1f

        val baseWidth = if (hasVideo) (videoSize.width * fitScale).roundToInt()
        else boxWidth.roundToInt()

        val baseHeight = if (hasVideo) (videoSize.height * fitScale).roundToInt()
        else boxHeight.roundToInt()

        val scaleValue = if (!controller.state.isZoomed || !hasVideo || fitScale <= 0f) 1f
        else ContentScale.Crop.computeScaleFactor(videoSize, maxSize).scaleX / fitScale

        val animatedScale by animateFloatAsState(
            targetValue = scaleValue,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        key(controller) {
            UIKitView(
                modifier = Modifier.layout { measurable, _ ->
                    val placeable = measurable.measure(
                        Constraints.fixed(
                            (baseWidth * animatedScale).roundToInt(),
                            (baseHeight * animatedScale).roundToInt()
                        )
                    )

                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                },
                onRelease = {
                    controller.detachPlayerLayer(it.playerLayer)
                    it.player = null
                },
                factory = {
                    PlayerUIView().apply {
                        player = controller.player
                        backgroundColor = UIColor.blackColor
                        controller.attachPlayerLayer(playerLayer)
                    }
                }
            )
        }

        ComposeSubtitleLayer(
            text = controller.subtitleText,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints.fixed(
                        width = (baseWidth * animatedScale)
                            .roundToInt()
                            .coerceAtMost(constraints.maxWidth),
                        height = (baseHeight * animatedScale)
                            .roundToInt()
                            .coerceAtMost(constraints.maxHeight)
                    )
                )

                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
        )
    }

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
}
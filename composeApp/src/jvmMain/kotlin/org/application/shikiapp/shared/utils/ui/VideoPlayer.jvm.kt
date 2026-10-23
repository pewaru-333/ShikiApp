package org.application.shikiapp.shared.utils.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import kotlinx.coroutines.*
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.vlc.CertificatesHelper
import org.application.shikiapp.shared.utils.vlc.SkiaImageVideoSurface
import org.application.shikiapp.shared.utils.vlc.SubtitlePicker
import org.application.shikiapp.shared.utils.vlc.hls.AudioTrack
import org.application.shikiapp.shared.utils.vlc.hls.SegmentProxy
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.TrackType
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.TimeApi
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

actual class VideoPlayerController : VideoPlayer() {
    private val executor = Executors.newSingleThreadExecutor {
        Thread(it).apply { isDaemon = true }
    }
    private val playerDispatcher = executor.asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    private val factory = MediaPlayerFactory(
        null,
        listOf(
            "--gnutls-dir-trust=${CertificatesHelper.directory.absolutePath}",
            "--http-reconnect",
            "--network-caching=5000",
            "--no-stats"
        )
    )
    private val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()
    private val videoSurface = SkiaImageVideoSurface()

    private var created = false
    private var playbackScope: CoroutineScope? = null
    @Volatile
    private var eventPlayback: CoroutineScope? = null
    private var subtitleTracks: SubtitlePicker? = null
    private var isReady = false
    private var playRequested = true
    private var lastPlaybackTime = Long.MIN_VALUE
    private var lastProgressNanos = 0L
    private var audioTracks = emptyList<AudioTrack>()
    private var audioTrackId: String? = null
    private var segmentProxy: SegmentProxy? = null

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
                BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                Point(0, 0),
                BLANK
            )
        )
    }

    actual override fun create() {
        if (created || !scope.isActive) return
        created = true

        scope.launch(playerDispatcher) {
            mediaPlayer.videoSurface().set(videoSurface)
            mediaPlayer.events().addMediaPlayerEventListener(playerEventListener)
        }
    }

    actual override fun release() {
        if (!scope.isActive) return
        eventPlayback = null
        scope.cancel()

        playbackScope = null
        subtitleTracks = null
        httpClient.shutdownNow()

        executor.execute {
            try {
                segmentProxy?.close()
                segmentProxy = null

                mediaPlayer.events().removeMediaPlayerEventListener(playerEventListener)
                mediaPlayer.controls().stop()
                mediaPlayer.time().startWatching(TimeApi.FINE_MINIMUM_UPDATE_PERIOD)
                mediaPlayer.release()
            } finally {
                try {
                    factory.release()
                } finally {
                    videoSurface.close()
                    playerDispatcher.close()
                }
            }
        }
    }

    actual override fun onLoadVideo(url: String) {
        if (!scope.isActive) return
        eventPlayback = null
        playbackScope?.cancel()

        val playback = CoroutineScope(scope.coroutineContext + SupervisorJob(scope.coroutineContext.job))
        playbackScope = playback
        isReady = false
        playRequested = true
        lastPlaybackTime = Long.MIN_VALUE
        lastProgressNanos = System.nanoTime()

        updateBuffer(0f)
        videoSurface.clear()
        subtitleTracks = SubtitlePicker(
            player = mediaPlayer,
            dispatcher = playerDispatcher,
            scope = playback,
            subtitles = state.subtitles,
            onSelectionChanged = ::updateSubtitleTrack,
            initialIndex = state.subtitles
                .indexOfFirst { it.name == state.selectedSubtitlesTrack }
                .coerceAtLeast(0)
        )

        val headers = state.headers
        val audioIndex = state.audioTrackIndex

        playback.launch {
            var proxy: SegmentProxy? = null

            try {
                withContext(playerDispatcher) {
                    eventPlayback = null

                    segmentProxy?.close()
                    segmentProxy = null

                    audioTracks = emptyList()
                    audioTrackId = null

                    mediaPlayer.controls().stop()
                }

                val audioTracks = if (audioIndex == null || audioIndex < 0) emptyList()
                else try {
                    withTimeoutOrNull(10.seconds) {
                        runInterruptible(Dispatchers.IO) {
                            val request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10))
                            headers.forEach { (key, value) ->
                                if (!key.equals("Range", ignoreCase = true)) {
                                    request.header(key, value)
                                }
                            }

                            val response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofInputStream())
                            response.body().use { body ->
                                if (response.statusCode() !in 200..299) return@runInterruptible emptyList()

                                val prefix = body.readNBytes(10).toString(Charsets.UTF_8)
                                if (!prefix.removePrefix("\uFEFF").trimStart().startsWith("#EXTM3U")) {
                                    return@runInterruptible emptyList()
                                }

                                AudioTrack.fromManifest(prefix + body.bufferedReader().readText())
                            }
                        }
                    } ?: emptyList()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    currentCoroutineContext().ensureActive()
                    emptyList()
                }

                proxy = SegmentProxy.open(httpClient, url, headers)
                val options = buildList {
                    headers.forEach { (key, value) ->
                        when (key.lowercase()) {
                            "user-agent" -> add(":http-user-agent=$value")
                            "referer" -> add(":http-referrer=$value")
                        }
                    }

                    add(":sub-language=none")
                    if (audioTracks.isNotEmpty()) add(":audio-language=none")
                    else audioIndex
                        ?.takeIf { it >= 0 }
                        ?.let { add(":audio-track=$it") }
                }

                val started = withContext(playerDispatcher) {
                    this@VideoPlayerController.audioTracks = audioTracks
                    eventPlayback = playback
                    segmentProxy = proxy
                    proxy = null

                    mediaPlayer.media().play(segmentProxy?.url ?: url, *options.toTypedArray())
                }

                if (!started) {
                    fail(playback)
                    return@launch
                }

                val startedNano = System.nanoTime()
                while (isActive) {
                    delay(250.milliseconds)

                    if (!isReady && System.nanoTime() - startedNano >= 10.seconds.inWholeNanoseconds) {
                        fail(playback)
                        return@launch
                    }

                    state.isLoading = playRequested &&
                            !state.isVideoEnded &&
                            (!isReady || System.nanoTime() - lastProgressNanos >= 2_000_000_000L)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                fail(playback)
            } finally {
                proxy?.close()
            }
        }
    }

    actual override fun onPlay() {
        playRequested = true
        lastProgressNanos = System.nanoTime()

        if (isReady) {
            submit { mediaPlayer.controls().play() }
        }
    }

    actual override fun onPause() {
        playRequested = false
        state.isLoading = false

        if (isReady) {
            submit { mediaPlayer.controls().setPause(true) }
        }
    }

    actual override fun onSetVolume(volume: Float) {
        updateVolume(volume)

        submit { mediaPlayer.audio().setVolume((volume * 100).toInt()) }
    }

    actual override fun onSetSpeed(speed: Float) {
        if (!isReady) updateSpeed(speed)

        submit { mediaPlayer.controls().setRate(speed) }
    }

    actual override fun onSeek(millis: Float) {
        lastProgressNanos = System.nanoTime()

        if (isReady) {
            submit { mediaPlayer.controls().setTime((millis * 1_000_000.0).toLong()) }
        }
    }

    actual override fun onLoadAudioTrack(index: Int) = submit {
        val expected = audioTracks.getOrNull(index)
        if (expected == null && audioTracks.isNotEmpty()) return@submit

        if (expected != null) {
            val selected = mediaPlayer.tracks().selectedAudioTrack()
            try {
                if (expected.matches(selected?.name())) return@submit
            } finally {
                selected?.release()
            }
        }

        val trackList = mediaPlayer.tracks().audioTracks() ?: return@submit
        try {
            val tracks = trackList.tracks()
            val track = if (expected == null) tracks.getOrNull(index)
            else tracks.firstOrNull { expected.matches(it.name()) && expected.isPrimary(it.name()) }
                ?: tracks.firstOrNull { expected.matches(it.name()) }

            if (track == null || (expected == null && track.selected())) return@submit

            val trackId = track.trackId()
            if (trackId == audioTrackId) return@submit

            audioTrackId = trackId
            mediaPlayer.tracks().selectTrack(track)
        } finally {
            trackList.release()
        }
    }

    actual override fun onLoadSubtitleTrack(index: Int) {
        subtitleTracks?.select(index)
    }

    internal fun <R> withImage(block: (Image?) -> R): R = videoSurface.withImage(block)

    internal fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    private fun submit(block: () -> Unit) {
        playbackScope?.launch(playerDispatcher) { block() }
    }

    private fun fail(playback: CoroutineScope) {
        if (!playback.isActive) return
        eventPlayback = null
        playback.cancel()

        isReady = false
        state.isPlaying = false
        state.isLoading = false

        scope.launch(playerDispatcher) {
            segmentProxy?.close()
            segmentProxy = null
            mediaPlayer.controls().stop()
        }

        playNext()
    }

    private val playerEventListener = object : MediaPlayerEventAdapter() {
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
            event {
                if (isReady) return@event
                isReady = true
                lastProgressNanos = System.nanoTime()
                state.isLoading = false

                val volume = state.volume
                val speed = state.speed
                val paused = !playRequested

                submit {
                    mediaPlayer.audio().setVolume((volume * 100).toInt())
                    mediaPlayer.controls().setRate(speed)
                    if (paused) mediaPlayer.controls().setPause(true)
                }

                state.currentTime.takeIf { it > 0f }?.let(::onSeek)
                state.audioTrackIndex?.let(::onLoadAudioTrack)
                subtitleTracks?.start()
            }

            refreshVideoTracks()
        }

        override fun opening(mediaPlayer: MediaPlayer) = event {
            state.isPlaying = false
            state.isLoading = true
        }

        override fun playing(mediaPlayer: MediaPlayer) = event {
            lastProgressNanos = System.nanoTime()
            state.isLoading = false
            state.isVideoEnded = false

            if (!state.isPlaying) {
                state.isPlaying = true
                onEvent(PlayerEvent.Play)
            }
        }

        override fun paused(mediaPlayer: MediaPlayer) = event {
            state.isLoading = false

            if (state.isPlaying) {
                state.isPlaying = false
                onEvent(PlayerEvent.Pause)
            }
        }

        override fun finished(mediaPlayer: MediaPlayer) = event {
            state.isPlaying = false
            state.isLoading = false
            state.isVideoEnded = true

            onEvent(PlayerEvent.Ended)
        }

        override fun error(mediaPlayer: MediaPlayer) = event(::fail)

        override fun positionChanged(mediaPlayer: MediaPlayer, newTime: Long, newPosition: Double) =
            event {
                if (!isReady || newTime < 0) return@event

                if (lastPlaybackTime == Long.MIN_VALUE || abs(newTime - lastPlaybackTime) >= 100_000L) {
                    lastPlaybackTime = newTime
                    lastProgressNanos = System.nanoTime()

                    if (playRequested) state.isLoading = false
                }

                state.currentTime = (newTime / 1_000_000f).coerceAtLeast(0f)
            }

        override fun volumeChanged(mediaPlayer: MediaPlayer, volume: Float) = event {
            if (isReady) updateVolume(volume)
        }

        override fun rateChanged(mediaPlayer: MediaPlayer, newRate: Float) = event {
            if (isReady) updateSpeed(newRate)
        }

        override fun lengthChanged(mediaPlayer: MediaPlayer, newLength: Long) = event {
            state.totalTime = (newLength / 1_000_000f).coerceAtLeast(0f)
        }

        override fun trackSelectionChanged(mediaPlayer: MediaPlayer, type: TrackType, unselectedTrackId: String?, selectedTrackId: String?) {
            if (type == TrackType.AUDIO && selectedTrackId != null) {
                val playback = eventPlayback ?: return

                playback.launch(playerDispatcher) {
                    if (audioTrackId == selectedTrackId) audioTrackId = null
                }
            }
        }

        override fun trackAdded(mediaPlayer: MediaPlayer, type: TrackType, trackId: String) = when (type) {
            TrackType.VIDEO -> refreshVideoTracks()
            TrackType.AUDIO -> event { state.audioTrackIndex?.let(::onLoadAudioTrack) }
            TrackType.TEXT -> event { subtitleTracks?.onTrackAdded(trackId) }
            else -> Unit
        }

        override fun trackUpdated(mediaPlayer: MediaPlayer, type: TrackType, trackId: String) {
            if (type == TrackType.VIDEO) refreshVideoTracks()
        }

        private fun event(block: suspend (CoroutineScope) -> Unit) {
            val playback = eventPlayback ?: return

            playback.launch { block(playback) }
        }

        private fun refreshVideoTracks() = event {
            val height = withContext(playerDispatcher) {
                val track = mediaPlayer.tracks().selectedVideoTrack() ?: return@withContext null
                try {
                    track.height().takeIf { it > 0 }
                } finally {
                    track.release()
                }
            } ?: return@event

            if (state.qualityList.size <= 1) state.qualityList = listOf(height)
            state.currentQuality = height
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

    Canvas(modifier.fillMaxSize()) {
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
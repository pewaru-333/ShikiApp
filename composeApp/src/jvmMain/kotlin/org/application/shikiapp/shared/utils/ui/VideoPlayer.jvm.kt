package org.application.shikiapp.shared.utils.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.data.CertificatesHelper
import org.jetbrains.skia.*
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.MediaSlaveType
import uk.co.caprica.vlcj.media.TrackType
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.StandardBufferFormat
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.lang.foreign.MemorySegment
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

actual class VideoPlayerController : VideoPlayer() {
    private val scheduler = Executors.newSingleThreadScheduledExecutor {
        Thread(it).apply {
            isDaemon = true
        }
    }
    private val vlcArgs = listOf(
        "--gnutls-dir-trust=${CertificatesHelper.directory.absolutePath}",
        "--http-reconnect",
        "--network-caching=5000",
        "--no-stats"
    )

    private val factory = MediaPlayerFactory(null, vlcArgs) // vlc-4.0-25062026
    private val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()
    private val videoSurface = SkiaImageVideoSurface()

    internal var isFullscreen by mutableStateOf(false)
        private set

    private var videoType: VideoType? = null

    private var isReady = false
    private var openingTask: ScheduledFuture<*>? = null


    override val feature = object : VideoPlayerFeature {
        override val isTV = false
        override val pictureInPicture = null
        override val showPlayPause = false
        override val visibilityDelay = 3000L
        override val pointerIcon = PointerIcon(
            Toolkit.getDefaultToolkit().createCustomCursor(
                BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                Point(0, 0),
                BLANK
            )
        )
    }


    override fun onLoadVideo(url: String) {
        try {
            val httpClient = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Range", "bytes=0-0")

            state.headers.forEach { (key, value) ->
                request.header(key, value)
            }

            httpClient
                .sendAsync(request.build(), HttpResponse.BodyHandlers.discarding())
                .thenAccept { response ->
                    val status = response.statusCode()
                    if (status == 200 || status == 206) {
                        val contentType = response.headers().firstValue("Content-Type").orElse(null)
                        videoType = contentType?.let(VideoType::getType)


                        if (videoType == null || videoType == VideoType.DASH) {
                            playNext()
                            return@thenAccept
                        }
                    }

                    CertificatesHelper.install(url)
                    val options = state.headers.mapNotNull { (key, value) ->
                        when (key.lowercase()) {
                            "user-agent" -> ":http-user-agent=$value"
                            "referer" -> ":http-referrer=$value"
                            else -> null
                        }
                    }

                    val stopped = !mediaPlayer.status().isPlaying || mediaPlayer.controls().stop()
                    val prepared = mediaPlayer.media().prepare(url, *options.toTypedArray()) && stopped

                    if (prepared) {
                        for (i in 1 until state.subtitles.size) {
                            val subtitleUrl = state.subtitles[i].url

                            if (subtitleUrl.isNotEmpty()) {
                                mediaPlayer.media().addSlave(MediaSlaveType.SUBTITLE, subtitleUrl, false)
                            }
                        }
                    }

                    mediaPlayer.controls().play()
                }.exceptionally {
                    playNext()
                    null
                }
        } catch (_: Exception) {

        }
    }

    override fun create() {
        mediaPlayer.videoSurface().set(videoSurface)
        mediaPlayer.events().addMediaPlayerEventListener(playerEventListener)
    }

    override fun release() {
        openingTask?.cancel(true)
        scheduler.shutdownNow()

        mediaPlayer.release()
        factory.release()
    }

    override fun onPlay() {
        mediaPlayer.controls().play()
    }

    override fun onPause() {
        mediaPlayer.controls().pause()
    }

    override fun onSetVolume(volume: Float) {
        mediaPlayer.audio().setVolume((volume * 100).toInt())
    }

    override fun onSetSpeed(speed: Float) {
        val success = mediaPlayer.controls().setRate(speed)

        if (success) {
            updateSpeed(speed)
        }
    }

    override fun onSeek(millis: Float) {
        mediaPlayer.controls().setTime((millis * 1000).toLong())
    }

    override fun onLoadAudioTrack(index: Int) {
        for (track in mediaPlayer.tracks().audioTracks().tracks()) {
            val name = track.name()
            val charIndex = name.lastIndexOf('-')
            if (charIndex == -1) continue

            var end = charIndex - 1
            while (end >= 0 && name[end].isWhitespace()) {
                end--
            }

            var start = end
            while (start >= 0 && name[start].isDigit()) {
                start--
            }

            if (start < end) {
                val parsedIndex = name.substring(start + 1, end + 1).toIntOrNull()
                if (parsedIndex == index) {
                    mediaPlayer.tracks().selectTrack(track)

                    return
                }
            }
        }
    }

    override fun onLoadSubtitleTrack(index: Int) {
        if (index == 0) {
            mediaPlayer.tracks().deselect(TrackType.TEXT)
        } else {
            val track = mediaPlayer.tracks().textTracks().tracks().getOrNull(index - 1)
            if (track != null) {
                mediaPlayer.tracks().selectTrack(track)
            }
        }
    }

    internal fun <R> withImage(block: (Image?) -> R): R = videoSurface.withImage(block)

    internal fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    private val playerEventListener = object : MediaPlayerEventAdapter() {
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
            isReady = true

            openingTask?.cancel(false)
            openingTask = null

            state.isPlaying = true
            state.isLoading = false

            state.audioTrackIndex?.let(::onLoadAudioTrack)
            restoreSubtitles()
        }

        override fun opening(mediaPlayer: MediaPlayer?) {
            openingTask?.cancel(false)
            openingTask = scheduler.schedule(
                /* command = */ { if (!isReady) error(mediaPlayer) },
                /* delay = */ 10,
                /* unit = */ TimeUnit.SECONDS
            )

            state.isPlaying = false
            state.isLoading = true
        }

        override fun playing(mediaPlayer: MediaPlayer?) {
            state.isPlaying = true

            onEvent(PlayerEvent.Play)
        }

        override fun paused(mediaPlayer: MediaPlayer?) {
            state.isPlaying = false

            onEvent(PlayerEvent.Pause)
        }

        override fun finished(mediaPlayer: MediaPlayer?) {
            state.isPlaying = false
            state.isVideoEnded = true

            onEvent(PlayerEvent.Ended)
        }

        override fun error(mediaPlayer: MediaPlayer?) {
            openingTask?.cancel(false)

            state.isPlaying = false
            playNext()
        }

        override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
            state.isLoading = newCache < 100f

            updateBuffer(newCache / 100f)
        }

        override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
            if (mediaPlayer != null && mediaPlayer.status().isPlaying) {
                state.currentTime = newTime / 1000f
            }
        }

        override fun volumeChanged(mediaPlayer: MediaPlayer?, volume: Float) {
            updateVolume(volume)
        }

        override fun lengthChanged(mediaPlayer: MediaPlayer?, newLength: Long) {
            state.totalTime = newLength / 1000f
        }

        override fun elementaryStreamSelected(mediaPlayer: MediaPlayer, type: TrackType?, unselectedStreamId: String?, selectedStreamId: String?) {
            if (type != TrackType.TEXT) return

            val subtitles = mediaPlayer.tracks().textTracks()?.tracks() ?: return

            if (unselectedStreamId == subtitles.lastOrNull()?.trackId()) {
                updateSubtitleTrack(null)
                return
            }

            if (selectedStreamId == null) {
                return
            }

            subtitles
                .indexOfFirst { it.trackId() == selectedStreamId }
                .takeIf { index -> index > -1 }
                ?.let { index -> updateSubtitleTrack(index + 1) }
        }

        override fun elementaryStreamUpdated(mediaPlayer: MediaPlayer, type: TrackType, id: Int, streamId: String) {
            if (type != TrackType.VIDEO) return

            val videoTracks = mediaPlayer.tracks().videoTracks().tracks() ?: return
            if (videoTracks.isEmpty()) return

            val isAdaptive = videoType == VideoType.DASH || videoType == VideoType.HLS

            for (track in videoTracks) {
                val height = track.height()
                if (height <= 0) continue

                if (isAdaptive && state.qualityList.size <= 1) {
                    state.qualityList = listOf(height)
                }

                if (track.selected()) {
                    state.currentQuality = height
                }
            }
        }
    }

    class SkiaImageVideoSurface : VideoSurface(VideoSurfaceAdapters.getVideoSurfaceAdapter()) {
        private val videoSurface = SkiaImageCallbackVideoSurface()
        private val skiaImage = mutableStateOf<Image?>(null)
        private val lock = ReentrantLock()
        private lateinit var pixmap: Pixmap

        internal fun <R> withImage(block: (Image?) -> R): R = lock.withLock {
            block(skiaImage.value)
        }

        override fun attach(mediaPlayer: MediaPlayer?) {
            videoSurface.attach(mediaPlayer)
        }

        private inner class SkiaImageBufferFormatCallback : BufferFormatCallback {
            private var width = 0
            private var height = 0

            override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) = Unit
            override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
                width = sourceWidth
                height = sourceHeight

                return StandardBufferFormat(sourceWidth, sourceHeight)
            }

            override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
                val buffer = buffers[0]
                val pointer = MemorySegment.ofBuffer(buffer).address()
                val imageInfo = ImageInfo(
                    width = width,
                    height = height,
                    colorType = ColorType.RGBA_8888,
                    alphaType = ColorAlphaType.PREMUL
                )

                pixmap = Pixmap.make(imageInfo, pointer, width * 4)
            }
        }

        private inner class SkiaImageRenderCallback : RenderCallback {
            override fun lock(mediaPlayer: MediaPlayer?) = Unit
            override fun unlock(mediaPlayer: MediaPlayer?) = Unit
            override fun display(mediaPlayer: MediaPlayer, nativeBuffers: Array<out ByteBuffer>, bufferFormat: BufferFormat, displayWidth: Int, displayHeight: Int) {
               lock.withLock {
                   skiaImage.value?.close()
                   skiaImage.value = Image.makeFromPixmap(pixmap)
               }
            }
        }

        private inner class SkiaImageCallbackVideoSurface : CallbackVideoSurface(SkiaImageBufferFormatCallback(), SkiaImageRenderCallback(), true)
    }

    private enum class VideoType {
        MP4, HLS, DASH, UNKNOWN;

        companion object {
            fun getType(type: String): VideoType {
                val value = type.lowercase()

                return when {
                    value.startsWith("video/mp4") -> MP4
                    value.startsWith("application/vnd.apple.mpegurl") -> HLS
                    value.startsWith("application/dash+xml") -> DASH
                    else -> UNKNOWN
                }
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

@Composable
actual fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier) {
    val windowManager = LocalWindowManager.current
    val zoom by animateFloatAsState(
        targetValue = if (controller.state.isZoomed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(controller.isFullscreen) {
        if (controller.isFullscreen != windowManager.isFullscreen) {
            windowManager.toggleFullscreen()
        }
    }

    DisposableEffect(controller.state) {
        onDispose {
            windowManager.exitFullscreen()
        }
    }

    Canvas(modifier.fillMaxSize()) {
        controller.withImage { image ->
            image?.let { img ->
                val canvasWidth = size.width
                val canvasHeight = size.height

                val imageWidth = img.width.toFloat()
                val imageHeight = img.height.toFloat()

                val fit = minOf(canvasWidth / imageWidth, canvasHeight / imageHeight)
                val crop = maxOf(canvasWidth / imageWidth, canvasHeight / imageHeight)

                val maxScale = if (fit > 0f) crop / fit else 1f
                val scale = 1f + zoom * (maxScale - 1f)

                val scaleCanvas = minOf(canvasWidth / imageWidth, canvasHeight / imageHeight)
                val scaledWidth = imageWidth * scaleCanvas
                val scaledHeight = imageHeight * scaleCanvas

                val xOffset = (canvasWidth - scaledWidth) / 2
                val yOffset = (canvasHeight - scaledHeight) / 2

                withTransform(
                    transformBlock = {
                        scale(
                            scaleX = scale,
                            scaleY = scale
                        )
                    },
                    drawBlock = {
                        drawIntoCanvas { canvas ->
                            canvas.save()
                            canvas.translate(xOffset, yOffset)
                            canvas.scale(scaleCanvas, scaleCanvas)
                            canvas.skiaCanvas.drawImage(img, 0f, 0f)
                            canvas.restore()
                        }
                    }
                )
            }
        }
    }
}
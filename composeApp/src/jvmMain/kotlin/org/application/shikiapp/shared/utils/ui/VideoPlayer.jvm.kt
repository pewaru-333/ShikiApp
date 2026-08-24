package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.data.CertificatesHelper
import org.jetbrains.skia.*
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.TrackType
import uk.co.caprica.vlcj.player.base.AudioTrack
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
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration.Companion.seconds

class VideoPlayerController(private val state: VideoPlayerState) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val vlcArgs = listOf(
        "--gnutls-dir-trust=${CertificatesHelper.directory.absolutePath}",
        "--http-reconnect",
        "--network-caching=5000"
    )

    private val factory = MediaPlayerFactory(null, vlcArgs) // vlc-4.0-25062026
    private val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()
    private val videoSurface = SkiaImageVideoSurface()

    private val cachedSubtitles = mutableSetOf<String>()

    private var isReady = false

    private var openingJob: Job? = null

    internal suspend fun play() {
        val url = state.url ?: return

        try {
            val response = Network.watchClient.get(url) {
                header(HttpHeaders.Range, "bytes=0-0")
            }

            val status = response.status
            if (status == HttpStatusCode.OK || status == HttpStatusCode.PartialContent) {
                response.headers[HttpHeaders.ContentType]?.let { contentType ->
                    if (contentType.startsWith("application/dash+xml", ignoreCase = true)) {
                        state.playNext()
                        return
                    }
                }
            }
        } catch (_: Exception) {
            state.playNext()
            return
        }

        val options = state.headers.mapNotNull { (key, value) ->
            when (key.lowercase()) {
                "user-agent" -> ":http-user-agent=$value"
                "referer" -> ":http-referrer=$value"
                else -> null
            }
        }

        CertificatesHelper.installCertificates(url)
        mediaPlayer.media().play(url, *options.toTypedArray())
    }

    internal fun create() {
        mediaPlayer.videoSurface().set(videoSurface)
        mediaPlayer.events().addMediaPlayerEventListener(playerEventListener)
    }

    internal fun release() {
        mediaPlayer.release()
        factory.release()
        coroutineScope.cancel()
    }

    internal fun <R> withImage(block: (Image?) -> R): R = videoSurface.withImage(block)

    internal fun togglePlayPause() {
        if (state.isPlaying) mediaPlayer.controls().play()
        else mediaPlayer.controls().pause()
    }

    internal fun setVolume() {
        mediaPlayer.audio().setVolume((state.volume * 100).toInt())
    }

    internal fun setSpeed() {
        mediaPlayer.controls().setRate(state.speed)
    }

    internal fun seek() {
        state.seekTrigger?.let { seconds ->
            if (state.totalTime > 0f) {
                mediaPlayer.controls().setTime((seconds * 1000).toLong())
            }
        }
    }

    internal fun loadAudioTrack() {
        val index = state.audioTrackIndex ?: return

        var audioTrack: AudioTrack? = null

        for (track in mediaPlayer.tracks().audioTracks().tracks()) {
            val description = track.name()
            val charIndex = description.lastIndexOf('-')
            if (charIndex == -1) continue

            var end = charIndex - 1
            while (end >= 0 && description[end].isWhitespace()) {
                end--
            }

            var start = end
            while (start >= 0 && description[start].isDigit()) {
                start--
            }

            if (start < end) {
                val parsedIndex = description.substring(start + 1, end + 1).toIntOrNull()
                if (parsedIndex == index) {
                    audioTrack = track
                    break
                }
            }
        }

        audioTrack?.let(mediaPlayer.tracks()::selectTrack)
    }

    internal fun loadSubtitles() {
        if (state.selectedSubtitlesTrack == null) {
            mediaPlayer.tracks().deselect(TrackType.TEXT)
        } else {
            state.subtitles
                .find { it.name == state.selectedSubtitlesTrack }
                ?.let { subtitleTrack ->
                    if (cachedSubtitles.add(subtitleTrack.url)) {
                        mediaPlayer.subpictures().setSubTitleUri(subtitleTrack.url)
                    } else {
                        val index = cachedSubtitles.indexOf(subtitleTrack.url)
                        val subtitle = mediaPlayer.tracks().textTracks().tracks()[index]

                        mediaPlayer.tracks().selectTrack(subtitle)
                    }
                }
        }
    }

    private val playerEventListener = object : MediaPlayerEventAdapter() {
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
            isReady = true
            openingJob?.cancel()
        }

        override fun opening(mediaPlayer: MediaPlayer) {
            openingJob?.cancel()
            openingJob = coroutineScope.launch {
                delay(10.seconds)
                if (!isReady) {
                    error(mediaPlayer)
                }
            }
        }

        override fun finished(mediaPlayer: MediaPlayer) {
            state.isVideoEnded = true
            state.isPlaying = false
        }

        override fun error(mediaPlayer: MediaPlayer) {
            openingJob?.cancel()
            state.isLoading = false
            state.playNext()
        }

        override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) {
            state.isLoading = newCache < 100f
            state.updateBuffer(newCache / 100f)
        }

        override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
            val total = mediaPlayer.status().length() / 1000f
            if (total > 0f && mediaPlayer.status().isPlaying) {
                state.updateTime(newTime / 1000f, total)
            }
        }

        override fun elementaryStreamAdded(mediaPlayer: MediaPlayer, type: TrackType, id: Int, streamId: String) {
            if (type == TrackType.TEXT) {
                mediaPlayer.tracks().select(type, streamId)
            }
        }

        override fun elementaryStreamUpdated(mediaPlayer: MediaPlayer, type: TrackType, id: Int, streamId: String) {
            if (type != TrackType.VIDEO) return

            val videoTracks = mediaPlayer.tracks().videoTracks()
            val currentQuality = videoTracks.tracks()
                .firstOrNull { it.id() == mediaPlayer.tracks().selectedVideoTrack().id() }
                ?.height()
                ?.takeIf { it > 0 }

            val mergedQualities = hashSetOf<Int>().apply {
                addAll(state.qualityList)

                videoTracks.tracks().forEach { track ->
                    val height = track.height()
                    if (height > 0) {
                        add(height)
                    }
                }
            }.sortedDescending()


            if (mergedQualities.isNotEmpty() && state.qualityList != mergedQualities) {
                state.qualityList = mergedQualities
            }

            if (currentQuality != null && state.currentQuality != currentQuality) {
                state.currentQuality = currentQuality
            }

            state.tracksRevision++
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
}

@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val windowManager = LocalWindowManager.current
    val controller = remember(state) { VideoPlayerController(state) }

    LaunchedEffect(state.url) {
        controller.play()
    }

    LaunchedEffect(state.isPlaying) {
        controller.togglePlayPause()
    }

    LaunchedEffect(state.volume) {
        controller.setVolume()
    }

    LaunchedEffect(state.speed) {
        controller.setSpeed()
    }

    LaunchedEffect(state.seekTrigger, state.totalTime) {
        controller.seek()
    }

    LaunchedEffect(state.audioTrackIndex, state.tracksRevision) {
        controller.loadAudioTrack()
    }

    LaunchedEffect(state.selectedSubtitlesTrack) {
        controller.loadSubtitles()
    }

    LaunchedEffect(state.isFullscreen) {
        if (state.isFullscreen != windowManager.isFullscreen) {
            windowManager.toggleFullscreen()
        }
    }

    DisposableEffect(controller) {
        controller.create()

        onDispose {
            windowManager.exitFullscreen()
            controller.release()
        }
    }

    Canvas(modifier.fillMaxSize()) {
        controller.withImage { image ->
            image?.let { img ->
                val canvasWidth = size.width
                val canvasHeight = size.height
                val imageWidth = img.width.toFloat()
                val imageHeight = img.height.toFloat()

                val scale = minOf(canvasWidth / imageWidth, canvasHeight / imageHeight)
                val scaledWidth = imageWidth * scale
                val scaledHeight = imageHeight * scale

                val xOffset = (canvasWidth - scaledWidth) / 2
                val yOffset = (canvasHeight - scaledHeight) / 2

                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(xOffset, yOffset)
                    canvas.scale(scale, scale)
                    canvas.skiaCanvas.drawImage(img, 0f, 0f)
                    canvas.restore()
                }
            }
        }
    }
}

actual class VideoPlayerUtils actual constructor(context: PlatformContext) {
    actual constructor() : this(DesktopContext())

    actual val isTV = false
    actual val showPlayPause = false
    actual val visibilityDelay = 3000L
    actual val pointerIcon by lazy {
        val cursor = Toolkit.getDefaultToolkit().createCustomCursor(
            /* cursor = */ BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
            /* hotSpot = */ Point(0, 0),
            /* name = */ BLANK
        )

        PointerIcon(cursor)
    }
}
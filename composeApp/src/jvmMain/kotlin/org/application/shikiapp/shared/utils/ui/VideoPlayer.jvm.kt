package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.utils.BLANK
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.TrackDescription
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters.getVideoSurfaceAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import kotlin.time.Duration.Companion.seconds

class VideoPlayerController(private val state: VideoPlayerState) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val factory = MediaPlayerFactory()

    private val cachedSubtitles = mutableSetOf<String>()

    val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()

    private var isReady = false

    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    internal fun play(url: String) {
        val options = state.headers.mapNotNull { (key, value) ->
            when (key.lowercase()) {
                "user-agent" -> ":http-user-agent=$value"
                "referer" -> ":http-referrer=$value"
                else -> null
            }
        }

        mediaPlayer.media().play(url, *options.toTypedArray())
    }

    internal fun create() {
        mediaPlayer.events().addMediaPlayerEventListener(playerEventListener)
        mediaPlayer.videoSurface().set(
            CallbackVideoSurface(
                /* bufferFormatCallback = */ FormatCallback,
                /* renderCallback = */ VideoRenderCallback(),
                /* lockBuffers = */ true,
                /* videoSurfaceAdapter = */ getVideoSurfaceAdapter()
            )
        )
    }

    internal fun release() {
        coroutineScope.launch {
            try {
                if (state.isPlaying || mediaPlayer.status().isPlaying) {
                    mediaPlayer.controls().stop()
                }
                mediaPlayer.release()
                factory.release()
            } catch (_: Exception) {

            } finally {
                cancel()
            }
        }
    }

    internal fun loadAudioTrack(index: Int) {
        var audioTrack: TrackDescription? = null

        for (track in mediaPlayer.audio().trackDescriptions()) {
            val description = track.description()
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

        if (audioTrack != null) {
            mediaPlayer.audio().setTrack(audioTrack.id())
        }
    }

    internal fun loadSubtitles() {
        if (state.selectedSubtitlesTrack == null) {
            mediaPlayer.subpictures().setTrack(-1)
        } else {
            state.subtitles
                .find { it.name == state.selectedSubtitlesTrack }
                ?.let { subtitleTrack ->
                    if (cachedSubtitles.add(subtitleTrack.url)) {
                        mediaPlayer.subpictures().setSubTitleUri(subtitleTrack.url)
                    } else {
                        val index = cachedSubtitles.indexOf(subtitleTrack.url) + 1
                        val subtitle = mediaPlayer.subpictures().trackDescriptions()[index]
                        mediaPlayer.subpictures().setTrack(subtitle.id())
                    }
                }
        }
    }

    private val playerEventListener = object : MediaPlayerEventAdapter() {
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
            isReady = true
        }

        override fun opening(mediaPlayer: MediaPlayer) {
            coroutineScope.launch {
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

        override fun videoOutput(mediaPlayer: MediaPlayer, newCount: Int) {
            var quality: Int? = null
            for (videoTrack in mediaPlayer.media().info().videoTracks()) {
                for (track in mediaPlayer.video().trackDescriptions()) {
                    if (videoTrack.id() == track.id()) {
                        quality = videoTrack.height()
                        break
                    }
                }
            }

            if (quality != null && state.currentQuality != quality) {
                state.currentQuality = quality
            }

            state.tracksRevision++
        }
    }

    private object FormatCallback : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int) = RV32BufferFormat(sourceWidth, sourceHeight)
        override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) = Unit
        override fun allocatedBuffers(buffers: Array<ByteBuffer>) = Unit
    }

    private inner class VideoRenderCallback : RenderCallback {
        private var bufferA = ByteArray(0)
        private var bufferB = ByteArray(0)
        private var useBufferA = true

        private var lastWidth = 0
        private var lastHeight = 0
        private var lastImageInfo = ImageInfo.DEFAULT

        override fun lock(mediaPlayer: MediaPlayer) = Unit
        override fun unlock(mediaPlayer: MediaPlayer) = Unit
        override fun display(
            mediaPlayer: MediaPlayer,
            nativeBuffers: Array<ByteBuffer>,
            bufferFormat: BufferFormat,
            displayWidth: Int,
            displayHeight: Int
        ) {
            val buffer = nativeBuffers[0]
            val width = bufferFormat.width
            val height = bufferFormat.height
            val size = width * height * 4

            if (lastWidth != width || lastHeight != height) {
                lastWidth = width
                lastHeight = height

                if (bufferA.size < size) {
                    bufferA = ByteArray(size)
                    bufferB = ByteArray(size)
                }

                lastImageInfo = ImageInfo(
                    width = width,
                    height = height,
                    colorInfo = ColorInfo(
                        alphaType = ColorAlphaType.OPAQUE,
                        colorType = ColorType.BGRA_8888,
                        colorSpace = ColorSpace.sRGB
                    )
                )
            }

            val writeBuffer = if (useBufferA) bufferA else bufferB
            buffer.get(writeBuffer, 0, size)
            buffer.rewind()

            imageBitmap = Bitmap().apply {
                installPixels(lastImageInfo, writeBuffer, width * 4)
            }.asComposeImageBitmap()

            useBufferA = !useBufferA
        }
    }
}

@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val fullscreenHandler = LocalFullscreenHandler.current

    val controller = remember(state) { VideoPlayerController(state) }
    val mediaPlayer = controller.mediaPlayer

    LaunchedEffect(state.url) {
        state.url?.let(controller::play)
    }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) mediaPlayer.controls().play()
        else mediaPlayer.controls().pause()
    }

    LaunchedEffect(state.volume) {
        mediaPlayer.audio().setVolume((state.volume * 100).toInt())
    }

    LaunchedEffect(state.speed) {
        mediaPlayer.controls().setRate(state.speed)
    }

    LaunchedEffect(state.seekTrigger, state.totalTime) {
        state.seekTrigger?.let { seconds ->
            if (state.totalTime > 0f) {
                mediaPlayer.controls().setTime((seconds * 1000).toLong())
            }
        }
    }

    LaunchedEffect(state.audioTrackIndex, state.tracksRevision) {
        state.audioTrackIndex?.let(controller::loadAudioTrack)
    }

    LaunchedEffect(state.selectedSubtitlesTrack) {
        controller.loadSubtitles()
    }

    LaunchedEffect(state.isFullscreen) {
        if (state.isFullscreen != fullscreenHandler.isFullscreen) {
            fullscreenHandler.toggle()
        }
    }

    DisposableEffect(controller) {
        controller.create()

        onDispose {
            controller.release()
        }
    }

    controller.imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = if (state.isZoomed) ContentScale.Crop else ContentScale.Fit
        )
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
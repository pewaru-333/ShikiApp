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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.utils.BLANK
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.VideoTrackInfo
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters.getVideoSurfaceAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import uk.co.caprica.vlcj.subs.Spus
import uk.co.caprica.vlcj.subs.parser.SrtParser
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.Reader
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer

class VideoPlayerController(private val state: VideoPlayerState) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val factory = MediaPlayerFactory()

    val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()

    private var parsedSpus: Spus? = null
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    init {
        mediaPlayer.events().addMediaPlayerEventListener(PlayerEventListener())
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
        mediaPlayer.controls().stop()
        mediaPlayer.release()
        factory.release()
    }

    internal fun loadSubtitles(url: String) {
        coroutineScope.launch {
            try {
                val parser = SrtParser()

                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

                if (response.statusCode() == 200) {
                    var reader: Reader = response.body().reader(Charsets.UTF_8)

                    if (url.contains(".vtt", ignoreCase = true)) {
                        val vttLines = reader.readLines()
                        val srtContent = convertVttToSrt(vttLines)

                        reader = StringReader(srtContent)
                    }

                    parsedSpus = parser.parse(reader)
                }

            } catch (_: Exception) {
                parsedSpus = null
                state.currentCues = emptyList()
            }
        }
    }

    private fun convertVttToSrt(vttLines: List<String>): String {
        val srtBuilder = StringBuilder()
        var sequenceNumber = 1
        var inCue = false

        for (line in vttLines) {
            val trimmed = line.trim()

            if (trimmed == "WEBVTT" || trimmed.startsWith("X-TIMESTAMP") || trimmed.startsWith("NOTE")) {
                continue
            }

            if (trimmed.contains("-->")) {
                srtBuilder.append(sequenceNumber++).append("\n")

                val fixedTimeLine = trimmed.split("-->").joinToString(" --> ") { time ->
                    val srtTime = time.trim().replace(".", ",")

                    if (srtTime.count { it == ':' } == 1) "00:$srtTime" else srtTime
                }

                srtBuilder.append(fixedTimeLine).append("\n")
                inCue = true
                continue
            }

            if (trimmed.isEmpty()) {
                if (inCue) {
                    srtBuilder.append("\n")
                    inCue = false
                }

                continue
            }

            if (inCue) {
                val cleanText = trimmed.replace(Regex("<[^>]*>"), BLANK)
                srtBuilder.append(cleanText).append("\n")
            }
        }

        return srtBuilder.toString()
    }

    private inner class PlayerEventListener : MediaPlayerEventAdapter() {
        override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) {
            state.isLoading = newCache < 100f
            state.updateBuffer(newCache / 100f)
        }

        override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
            val total = mediaPlayer.status().length() / 1000f
            if (total > 0f && mediaPlayer.status().isPlaying) {
                state.updateTime(newTime / 1000f, total)
            }

            val newCues = (parsedSpus?.get(newTime)?.value()?.toString().orEmpty())
                .split("\n")
                .map(String::trim)
                .filter(String::isNotEmpty)

            if (state.currentCues != newCues) {
                state.currentCues = newCues
            }
        }

        override fun error(mediaPlayer: MediaPlayer) {
            state.isLoading = false
            state.playNext()
        }

        override fun finished(mediaPlayer: MediaPlayer) {
            state.isVideoEnded = true
            state.isPlaying = false
        }

        override fun playing(mediaPlayer: MediaPlayer) {
            val videoTracks = mediaPlayer.media().info().videoTracks()
            if (videoTracks.isNullOrEmpty()) {
                state.tracksRevision++
                return
            }

            val isAdaptive = state.url?.let {
                it.contains(".m3u8", ignoreCase = true) ||
                        it.contains(".mpd", ignoreCase = true)
            }

            if (isAdaptive == true) {
                val qualities = videoTracks
                    .asSequence()
                    .map(VideoTrackInfo::height)
                    .filter { it > 0 }
                    .distinct()
                    .sortedDescending()
                    .toList()

                if (qualities.isNotEmpty()) {
                    state.qualityList = qualities
                }
            }

            val currentQuality = videoTracks
                .firstOrNull { it.id() == mediaPlayer.video().track() }
                ?.height()

            if (currentQuality != null && currentQuality > 0 && state.currentQuality != currentQuality) {
                state.currentQuality = currentQuality
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

            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            buffer.rewind()

            val imageInfo = ImageInfo(
                width = width,
                height = height,
                colorType = ColorType.BGRA_8888,
                alphaType = ColorAlphaType.OPAQUE
            )

            imageBitmap = Image.makeRaster(imageInfo, bytes, width * 4).toComposeImageBitmap()
        }
    }
}

@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val fullscreenHandler = LocalFullscreenHandler.current

    val controller = remember(state) { VideoPlayerController(state) }
    val mediaPlayer = controller.mediaPlayer

    LaunchedEffect(state.url) {
        state.url?.let { url ->
            val options = buildList {
                add(":http-reconnect=true")

                state.headers.forEach { (key, value) ->
                    when (key.lowercase()) {
                        "user-agent" -> add(":http-user-agent=$value")
                        "origin" -> add(":http-origin=$value")
                        "referer" -> add(":http-referrer=$value")
                    }
                }
            }

            mediaPlayer.media().play(url, *options.toTypedArray())

            state.subtitles.forEach {
                mediaPlayer.subpictures().setSubTitleUri(it.url)
            }

            if (!state.isPlaying) {
                mediaPlayer.controls().pause()
            }
        }
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

    LaunchedEffect(state.currentQuality, state.tracksRevision) {
        state.currentQuality?.let(mediaPlayer.video()::setTrack)
    }

    LaunchedEffect(state.audioTrackIndex, state.tracksRevision) {
        state.audioTrackIndex?.let { index ->
            mediaPlayer.audio().trackDescriptions()?.elementAtOrNull(index)?.let { track ->
                mediaPlayer.audio().setTrack(track.id())
            }
        }
    }

    LaunchedEffect(state.selectedSubtitlesTrack) {
        if (state.selectedSubtitlesTrack == null) {
            mediaPlayer.subpictures().setTrack(-1)
        } else {
            mediaPlayer.subpictures().trackDescriptions()
                ?.firstOrNull { it.description() == state.selectedSubtitlesTrack }
                ?.let { mediaPlayer.subpictures().setTrack(it.id()) }

            state.selectedSubtitlesTrack?.let { track ->
                controller.loadSubtitles(state.subtitles.find { it.name == track }!!.url)
            }
        }
    }

    LaunchedEffect(state.isFullscreen) {
        if (state.isFullscreen != fullscreenHandler.isFullscreen) {
            fullscreenHandler.toggle()
        }
    }

    DisposableEffect(controller) {
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
            /* hotSpot = */ Point(0,0),
            /* name = */ BLANK
        )

        PointerIcon(cursor)
    }
}
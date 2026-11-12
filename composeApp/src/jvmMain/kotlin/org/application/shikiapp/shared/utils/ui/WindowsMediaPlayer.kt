package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.player.MediaProxy
import org.application.shikiapp.shared.utils.player.Player
import org.jetbrains.skia.*
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

internal class WindowsMediaPlayer(private val state: VideoPlayerState, private val onEvent: (PlayerEvent) -> Unit) : Player {
    private interface Api : Library {
        fun wm_create(): Pointer?
        fun wm_destroy(handle: Pointer)
        fun wm_load(handle: Pointer, url: String, headers: String, audio: Int): Int
        fun wm_control(handle: Pointer, command: Int, value: Double): Int
        fun wm_status(handle: Pointer, status: DoubleArray): Int
        fun wm_resize(handle: Pointer, width: Int, height: Int): Int
        fun wm_subtitle(handle: Pointer, bytes: ByteArray?, size: Int, request: Int): Int
        fun wm_subtitle_ass(handle: Pointer, bytes: ByteArray, size: Int, request: Int): Int
        fun wm_subtitle_status(handle: Pointer, request: Int): Int
        fun wm_frame(handle: Pointer, pixels: Pointer?, capacity: Int, dimensions: IntArray): Int
    }

    private val api = Native.load(
        getLibraryPath(),
        Api::class.java,
        mapOf(Library.OPTION_STRING_ENCODING to "UTF-8")
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    private var subtitleJob: Job? = null
    private var subtitleSelection = 0
    private var subtitleIndex = 0

    private val frameLock = Any()

    private var image: Image? = null
    private var position: Float? = null
    private var viewportWidth = 1920
    private var viewportHeight = 1080
    private var revision by mutableIntStateOf(0)

    private var playback: Job? = null
    private var handle: Pointer? = null
    private var proxy: MediaProxy? = null

    @Volatile
    private var generation = 0
    private var toPlay = true
    private var failed = false
    private var ready = false

    override fun load(url: String) {
        if (!scope.isActive) return
        playback?.cancel()

        val expected = ++generation
        position = state.currentTime.takeIf { it > 0f }
        toPlay = true
        failed = false
        ready = false
        state.isVideoEnded = false
        clearFrame()
        subtitleJob?.cancel()
        subtitleJob = null
        subtitleSelection++
        subtitleIndex = state.subtitles.indexOfFirst { it.name == state.selectedSubtitlesTrack }.coerceAtLeast(0)
        state.selectedSubtitlesTrack = null

        val headers = state.headers
        val audio = state.audioTrackIndex ?: -1
        val width = viewportWidth
        val height = viewportHeight

        playback = scope.launch {
            try {
                proxy?.close()
                val isDash = url.substringBefore('?').endsWith(".mpd", ignoreCase = true)
                proxy = if (headers.isEmpty() || isDash) null else MediaProxy(url, headers)

                val source = proxy?.url ?: url
                val nativeHeaders = if (isDash) {
                    JsonObject(headers.mapValues { JsonPrimitive(it.value) }).toString()
                } else {
                    "{}"
                }

                withContext(dispatcher) {
                    ensureActive()
                    if (expected != generation) throw CancellationException()
                    val player = handle ?: checkNotNull(api.wm_create()).also { handle = it }
                    check(api.wm_load(player, source, nativeHeaders, audio) >= 0)
                    ensureActive()
                    if (expected != generation) throw CancellationException()
                    check(api.wm_resize(player, width, height) >= 0)
                }

                val status = DoubleArray(8)
                val dimensions = IntArray(2)
                var imageInfo = ImageInfo(0, 0, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
                var outputWidth = 0
                var outputHeight = 0
                val started = System.nanoTime()

                while (isActive && expected == generation) {
                    val frameStarted = System.nanoTime()
                    val canRender = ready
                    val viewportWidth = viewportWidth
                    val viewportHeight = viewportHeight
                    val zoomed = state.isZoomed
                    var frame: Image? = null
                    try {
                        withContext(dispatcher) {
                            ensureActive()
                            val player = checkNotNull(handle)
                            check(api.wm_status(player, status) >= 0)
                            check(status[1] >= 0)

                            if (status[0] != 0.0) {
                                val sourceWidth = status[6].toInt()
                                val sourceHeight = status[7].toInt()
                                if (sourceWidth in 1..8192 && sourceHeight in 1..8192) {
                                    val horizontal = viewportWidth.toDouble() / sourceWidth
                                    val vertical = viewportHeight.toDouble() / sourceHeight
                                    val scale = minOf(1.0, if (zoomed) maxOf(horizontal, vertical) else minOf(horizontal, vertical))
                                    val width = (sourceWidth * scale).roundToInt().coerceAtLeast(1)
                                    val height = (sourceHeight * scale).roundToInt().coerceAtLeast(1)
                                    if (width != outputWidth || height != outputHeight) {
                                        check(api.wm_resize(player, width, height) >= 0)
                                        outputWidth = width
                                        outputHeight = height
                                    }
                                }
                            }
                            if (canRender && status[0] != 0.0) {
                                val size = api.wm_frame(player, null, 0, dimensions)
                                check(size >= 0)
                                if (size > 0) {
                                    check(
                                        dimensions[0] in 1..8192 &&
                                                dimensions[1] in 1..8192 &&
                                                dimensions[0].toLong() * dimensions[1] * 4 == size.toLong()
                                    )
                                    if (imageInfo.width != dimensions[0] || imageInfo.height != dimensions[1]) {
                                        imageInfo = ImageInfo(dimensions[0], dimensions[1], ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
                                    }
                                    Data.makeUninitialized(size).use { pixels ->
                                        val copied = api.wm_frame(player, Pointer(pixels.writableData()), size, dimensions)
                                        check(copied >= 0)
                                        if (copied == size && imageInfo.width == dimensions[0] && imageInfo.height == dimensions[1]) {
                                            frame = Image.makeRaster(imageInfo, pixels, imageInfo.minRowBytes)
                                        }
                                    }
                                }
                            }
                        }
                        ensureActive()
                        if (expected != generation) break
                        frame?.let { next ->
                            synchronized(frameLock) {
                                image?.close()
                                image = next
                                revision++
                            }
                            frame = null
                        }
                    } finally {
                        frame?.close()
                    }
                    if (!ready && status[0] != 0.0) {
                        ready = true
                        control(COMMAND_SET_VOLUME, state.volume.toDouble())
                        control(COMMAND_SET_SPEED, state.speed.toDouble())
                        control(COMMAND_SELECT_AUDIO, (state.audioTrackIndex ?: -1).toDouble())
                        position?.let { control(COMMAND_SEEK, it.toDouble()) }
                        position = null
                        control(if (toPlay) COMMAND_PLAY else COMMAND_PAUSE)
                        subtitle(subtitleIndex)
                    }

                    check(ready || System.nanoTime() - started < TimeUnit.SECONDS.toNanos(30))
                    state.currentTime = status[4].toFloat().coerceAtLeast(0f)
                    state.totalTime = status[5].toFloat().coerceAtLeast(0f)
                    val playbackState = status[3].toInt()
                    val ended = status[2] != 0.0
                    state.isLoading = !ended && (!ready || playbackState == STATE_OPENING || playbackState == STATE_BUFFERING)
                    val playing = !ended && playbackState == STATE_PLAYING
                    if (state.isPlaying != playing) {
                        state.isPlaying = playing
                        onEvent(if (playing) PlayerEvent.Play else PlayerEvent.Pause)
                        ensureActive()
                        if (expected != generation) break
                    }
                    val height = status[7].toInt()
                    if (height > 0) {
                        state.currentQuality = height
                    }
                    val wasEnded = state.isVideoEnded
                    state.isVideoEnded = ended
                    if (ended && !wasEnded) {
                        toPlay = false
                        onEvent(PlayerEvent.Ended)
                    }
                    val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - frameStarted)
                    val interval = if (ready && !toPlay) IDLE_INTERVAL_MILLIS else FRAME_INTERVAL_MILLIS
                    delay((interval - elapsed).coerceAtLeast(1L).milliseconds)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (isActive) fail(expected)
            } catch (_: LinkageError) {
                if (isActive) fail(expected)
            }
        }
    }

    override fun play() {
        toPlay = true
        control(COMMAND_PLAY)
    }

    override fun pause() {
        toPlay = false
        control(COMMAND_PAUSE)
    }

    override fun volume(value: Float) {
        state.volume = value
        control(COMMAND_SET_VOLUME, value.toDouble())
    }

    override fun speed(value: Float) {
        state.speed = value
        control(COMMAND_SET_SPEED, value.toDouble())
    }

    override fun seek(seconds: Float) {
        if (ready) control(COMMAND_SEEK, seconds.toDouble())
        else position = seconds
    }

    override fun audio(index: Int) {
        state.audioTrackIndex = index.takeIf { it >= 0 }
        control(COMMAND_SELECT_AUDIO, index.toDouble())
    }

    override fun subtitle(index: Int) {
        if (!scope.isActive) return
        val track = state.subtitles.getOrNull(index)?.takeIf { index > 0 && it.url.isNotBlank() }
        val selected = if (track == null) 0 else index
        if (selected == subtitleIndex && subtitleJob?.isActive == true) return
        if (track != null && track.name == state.selectedSubtitlesTrack && subtitleJob?.isActive != true) return

        subtitleIndex = selected
        subtitleJob?.cancel()
        val request = ++subtitleSelection
        val expected = generation
        if (track == null) state.selectedSubtitlesTrack = null
        if (!ready) return
        val headers = state.headers
        val baseUrl = state.url

        subtitleJob = scope.launch {
            var completed = false
            try {
                withContext(dispatcher) {
                    ensureActive()
                    if (expected != generation || request != subtitleSelection) throw CancellationException()
                    check(api.wm_subtitle(checkNotNull(handle), null, if (track == null) 0 else -1, request) >= 0)
                }
                if (track == null || track.name == state.selectedSubtitlesTrack) {
                    completed = true
                    return@launch
                }

                val uri = URI.create(track.url)
                val url = if (uri.isAbsolute) uri else URI.create(checkNotNull(baseUrl)).resolve(uri)
                val builder = HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(15))
                headers.forEach { (name, value) ->
                    if (name.equals(HttpHeaders.Origin, true) || name.equals(HttpHeaders.Referrer, true) ||
                        name.equals(HttpHeaders.UserAgent, true)
                    ) builder.header(name, value)
                }
                val response = client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofByteArray()).await()
                check(HttpStatusCode.fromValue(response.statusCode()).isSuccess())
                val bytes = response.body()
                check(bytes.isNotEmpty() && bytes.size <= MAX_SUBTITLE_BYTES)
                val content = when {
                    bytes.size >= 2 && bytes[0] == 0xff.toByte() && bytes[1] == 0xfe.toByte() -> bytes.toString(Charsets.UTF_16LE)
                    bytes.size >= 2 && bytes[0] == 0xfe.toByte() && bytes[1] == 0xff.toByte() -> bytes.toString(Charsets.UTF_16BE)
                    else -> bytes.toString(Charsets.UTF_8)
                }.removePrefix("\uFEFF")
                val isAss = url.path.endsWith(".ass", true) || url.path.endsWith(".ssa", true) ||
                    (content.lineSequence().any { it.trim().equals("[Script Info]", true) } &&
                        content.lineSequence().any { it.trim().equals("[Events]", true) })
                withContext(dispatcher) {
                    ensureActive()
                    if (expected != generation || request != subtitleSelection) throw CancellationException()
                    val player = checkNotNull(handle)
                    if (isAss) {
                        val utf8 = content.toByteArray(Charsets.UTF_8)
                        check(api.wm_subtitle_ass(player, utf8, utf8.size, request) >= 0)
                    } else {
                        check(api.wm_subtitle(player, bytes, bytes.size, request) >= 0)
                    }
                }

                val started = System.nanoTime()
                while (true) {
                    val result = withContext(dispatcher) {
                        ensureActive()
                        if (expected != generation || request != subtitleSelection) throw CancellationException()
                        api.wm_subtitle_status(checkNotNull(handle), request)
                    }
                    check(result >= 0)
                    if (result == 1) break
                    check(System.nanoTime() - started < TimeUnit.SECONDS.toNanos(15))
                    delay(FRAME_INTERVAL_MILLIS.milliseconds)
                }
                ensureActive()
                if (expected == generation && request == subtitleSelection) {
                    state.selectedSubtitlesTrack = track.name
                    completed = true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            } catch (_: LinkageError) {
                if (isActive) fail(expected)
            } finally {
                if (!completed && scope.isActive) {
                    withContext(NonCancellable + dispatcher) {
                        if (expected == generation && request == subtitleSelection) {
                            try {
                                handle?.let { api.wm_subtitle(it, null, -1, request) }
                            } catch (_: Exception) {
                            } catch (_: LinkageError) {
                            }
                        }
                    }
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        if (width > 0 && height > 0 && !(width == viewportWidth && height == viewportHeight)) {
            viewportWidth = width
            viewportHeight = height
        }
    }

    private fun control(command: Int, value: Double = 0.0) {
        if (!ready || !scope.isActive) return
        val expected = generation
        scope.launch {
            try {
                withContext(dispatcher) {
                    ensureActive()
                    if (expected == generation) {
                        handle?.let {
                            check(api.wm_control(it, command, value) >= 0)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (isActive && (command == COMMAND_PLAY || command == COMMAND_PAUSE)) {
                    fail(expected)
                }
            }
        }
    }

    override fun <R> withImage(block: (Image?) -> R): R = synchronized(frameLock) {
        revision
        block(image)
    }

    private suspend fun fail(expected: Int) {
        if (expected != generation || failed || !scope.isActive) return
        failed = true
        ready = false

        withContext(dispatcher) {
            if (expected == generation) {
                try {
                    handle?.let { api.wm_control(it, COMMAND_PAUSE, 0.0) }
                } catch (_: Exception) {
                } catch (_: LinkageError) {
                }
            }
        }
        if (expected != generation) {
            return
        }

        proxy?.close()
        proxy = null
        onEvent(PlayerEvent.Error)
        subtitleJob?.cancel()
        playback?.cancel()
    }

    private fun clearFrame() {
        val imageToClose = synchronized(frameLock) {
            val currentImage = image
            image = null
            revision++
            currentImage
        }

        imageToClose?.close()
    }

    override fun close() {
        if (!scope.isActive) {
            return
        }

        scope.cancel()
        client.shutdownNow()
        proxy?.close()
        proxy = null

        dispatcher.executor.execute {
            try {
                handle?.let(api::wm_destroy)
            } catch (_: Exception) {
                return@execute
            } finally {
                handle = null
                dispatcher.use { clearFrame() }
            }
        }
    }

    private fun getLibraryPath(): String {
        val property = System.getProperty("compose.application.resources.dir")
        if (property != null) {
            val file = File(property, "player/windows/player.dll")
            if (file.isFile) {
                return file.absolutePath
            }
        }

        var file = File("desktopApp/files/player/windows/player.dll")
        if (file.isFile) {
            return file.absolutePath
        }

        file = File("files/player/windows/player.dll")
        if (file.isFile) {
            return file.absolutePath
        }

        throw RuntimeException()
    }

    companion object {
        private const val MAX_SUBTITLE_BYTES = 16 * 1024 * 1024
        private const val FRAME_INTERVAL_MILLIS = 16L
        private const val IDLE_INTERVAL_MILLIS = 100L

        private const val COMMAND_PLAY = 0
        private const val COMMAND_PAUSE = 1
        private const val COMMAND_SET_VOLUME = 2
        private const val COMMAND_SET_SPEED = 3
        private const val COMMAND_SEEK = 4
        private const val COMMAND_SELECT_AUDIO = 5

        private const val STATE_OPENING = 1
        private const val STATE_BUFFERING = 2
        private const val STATE_PLAYING = 3
    }
}
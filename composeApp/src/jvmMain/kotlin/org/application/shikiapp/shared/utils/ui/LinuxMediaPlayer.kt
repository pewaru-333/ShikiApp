package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.*
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.player.MediaProxy
import org.application.shikiapp.shared.utils.player.Player
import org.jetbrains.skia.*
import java.io.File
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

internal class LinuxMediaPlayer(
    private val state: VideoPlayerState,
    private val onEvent: (PlayerEvent) -> Unit
) : Player {
    private interface Api : Library {
        fun lp_create(): Pointer?
        fun lp_destroy(handle: Pointer)
        fun lp_load(handle: Pointer, url: String, names: Array<String>, values: Array<String>, count: Int, audio: Int, subtitle: String?): Int
        fun lp_control(handle: Pointer, command: Int, value: Double): Int
        fun lp_subtitle(handle: Pointer, url: String?): Int
        fun lp_resize(handle: Pointer, width: Int, height: Int): Int
        fun lp_status(handle: Pointer, status: DoubleArray): Int
        fun lp_frame(handle: Pointer, pixels: Pointer?, capacity: Int, dimensions: IntArray): Int
    }

    private val api by lazy {
        Native.load(getLibraryPath(), Api::class.java, mapOf(Library.OPTION_STRING_ENCODING to "UTF-8"))
    }
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val frameLock = Any()
    private var revision by mutableIntStateOf(0)
    private var image: Image? = null
    private var handle: Pointer? = null
    private var playback: Job? = null
    private var proxy: MediaProxy? = null
    private var ready = false
    private var failed = false
    private var wantsPlay = true
    private var pendingPosition: Float? = null
    private var subtitleIndex = 0
    private var appliedSubtitle = 0
    private var viewportWidth = 1920
    private var viewportHeight = 1080

    @Volatile
    private var generation = 0

    override fun load(url: String) {
        if (!scope.isActive) return
        playback?.cancel()
        val expected = ++generation
        ready = false
        failed = false
        wantsPlay = true
        pendingPosition = state.currentTime.takeIf { it > 0f }
        subtitleIndex = state.subtitles.indexOfFirst { it.name == state.selectedSubtitlesTrack }.coerceAtLeast(0)
        state.isVideoEnded = false
        clearFrame()
        val headers = state.headers
        val audio = state.audioTrackIndex ?: -1
        val initialSubtitle = subtitleIndex
        appliedSubtitle = initialSubtitle
        val nativeHeaders = headers.filterKeys {
            it.equals(HttpHeaders.Origin, true) || it.equals(HttpHeaders.Referrer, true) ||
                    it.equals(HttpHeaders.UserAgent, true)
        }
        playback = scope.launch {
            try {
                proxy?.close()
                val dash = URI.create(url).path.orEmpty().endsWith(".mpd", true)
                proxy = if (headers.isEmpty() || dash) null else MediaProxy(url, headers)
                val source = proxy?.url ?: url
                val subtitle = subtitleUrl(initialSubtitle)
                withContext(dispatcher) {
                    ensureActive()
                    if (expected != generation) throw CancellationException()
                    val player = handle ?: checkNotNull(api.lp_create()).also { handle = it }
                    check(api.lp_load(player, source, nativeHeaders.keys.toTypedArray(),
                        nativeHeaders.values.toTypedArray(), nativeHeaders.size, audio, subtitle) >= 0)
                }
                val status = DoubleArray(9)
                val dimensions = IntArray(2)
                var outputWidth = 0
                var outputHeight = 0
                var opened = false
                var waitingSince = System.nanoTime()
                while (isActive && expected == generation) {
                    val frameStarted = System.nanoTime()
                    val viewportWidth = viewportWidth
                    val viewportHeight = viewportHeight
                    val zoomed = state.isZoomed
                    var frame: Image? = null
                    try {
                        withContext(dispatcher) {
                            ensureActive()
                            if (expected != generation) throw CancellationException()
                            val player = checkNotNull(handle)
                            check(api.lp_status(player, status) >= 0 && status[1] >= 0)
                            if (status[0] != 0.0) {
                                val width = status[6].toInt()
                                val height = status[7].toInt()
                                if (width in 1..8192 && height in 1..8192) {
                                    val horizontal = viewportWidth.toDouble() / width
                                    val vertical = viewportHeight.toDouble() / height
                                    val scale = minOf(1.0, if (zoomed) maxOf(horizontal, vertical) else minOf(horizontal, vertical))
                                    val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
                                    val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)
                                    if (targetWidth != outputWidth || targetHeight != outputHeight) {
                                        check(api.lp_resize(player, targetWidth, targetHeight) >= 0)
                                        outputWidth = targetWidth
                                        outputHeight = targetHeight
                                    }
                                }
                                val size = api.lp_frame(player, null, 0, dimensions)
                                check(size >= 0)
                                if (size > 0) {
                                    check(dimensions[0] in 1..8192 && dimensions[1] in 1..8192 &&
                                            dimensions[0].toLong() * dimensions[1] * 4 == size.toLong())
                                    val info = ImageInfo(dimensions[0], dimensions[1], ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
                                    Data.makeUninitialized(size).use { pixels ->
                                        check(api.lp_frame(player, Pointer(pixels.writableData()), size, dimensions) == size)
                                        frame = Image.makeRaster(info, pixels, info.minRowBytes)
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
                    ready = status[0] != 0.0
                    if (ready && !opened) {
                        opened = true
                        control(COMMAND_SET_VOLUME, state.volume.toDouble())
                        control(COMMAND_SET_SPEED, state.speed.toDouble())
                        control(COMMAND_SELECT_AUDIO, (state.audioTrackIndex ?: -1).toDouble())
                        pendingPosition?.let { control(COMMAND_SEEK, it.toDouble()) }
                        pendingPosition = null
                        control(if (wantsPlay) COMMAND_PLAY else COMMAND_PAUSE)
                    }
                    if (ready && subtitleIndex != appliedSubtitle) subtitle(subtitleIndex)
                    if (ready) waitingSince = System.nanoTime()
                    check(ready || System.nanoTime() - waitingSince < TimeUnit.SECONDS.toNanos(30))
                    val ended = status[2] != 0.0
                    state.currentTime = status[4].toFloat().coerceAtLeast(0f)
                    state.totalTime = status[5].toFloat().coerceAtLeast(0f)
                    state.bufferPercentage = status[8].toFloat().coerceIn(0f, 1f)
                    state.isLoading = !ended && (!ready || status[8] < 1.0)
                    if (status[7] > 0) state.currentQuality = status[7].toInt()
                    val playing = !ended && status[3] != 0.0
                    if (state.isPlaying != playing) {
                        state.isPlaying = playing
                        onEvent(if (playing) PlayerEvent.Play else PlayerEvent.Pause)
                        ensureActive()
                        if (expected != generation) break
                    }
                    val wasEnded = state.isVideoEnded
                    state.isVideoEnded = ended
                    if (ended && !wasEnded) {
                        wantsPlay = false
                        onEvent(PlayerEvent.Ended)
                    }
                    val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - frameStarted)
                    delay(((if (wantsPlay) 16L else 100L) - elapsed).coerceAtLeast(1L).milliseconds)
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
        wantsPlay = true
        control(COMMAND_PLAY)
    }

    override fun pause() {
        wantsPlay = false
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
        else pendingPosition = seconds
    }

    override fun audio(index: Int) {
        state.audioTrackIndex = index.takeIf { it >= 0 }
        control(COMMAND_SELECT_AUDIO, index.toDouble())
    }

    override fun subtitle(index: Int) {
        if (!scope.isActive || failed || (index != 0 && index !in state.subtitles.indices)) return
        subtitleIndex = index
        val url = subtitleUrl(index)
        if (!ready) {
            if (index == 0) state.selectedSubtitlesTrack = null
            return
        }
        if (index == appliedSubtitle) return
        appliedSubtitle = index
        val expected = generation
        scope.launch {
            try {
                withContext(dispatcher) {
                    ensureActive()
                    if (expected != generation || index != subtitleIndex) throw CancellationException()
                    check(api.lp_subtitle(checkNotNull(handle), url) >= 0)
                }
                if (expected == generation && index == subtitleIndex) {
                    state.selectedSubtitlesTrack = if (url == null) null else state.subtitles[index].name
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

    private fun subtitleUrl(index: Int): String? {
        val track = state.subtitles.getOrNull(index)?.takeIf { index > 0 && it.url.isNotBlank() } ?: return null
        val uri = URI.create(track.url)
        return if (uri.isAbsolute) uri.toString() else URI.create(checkNotNull(state.url)).resolve(uri).toString()
    }

    override fun resize(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            viewportWidth = width
            viewportHeight = height
        }
    }

    private fun control(command: Int, value: Double = 0.0) {
        if (!ready || !scope.isActive || failed) return
        val expected = generation
        scope.launch {
            try {
                withContext(dispatcher) {
                    ensureActive()
                    if (expected == generation) handle?.let { check(api.lp_control(it, command, value) >= 0) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (isActive && (command == COMMAND_PLAY || command == COMMAND_PAUSE)) fail(expected)
            } catch (_: LinkageError) {
                if (isActive) fail(expected)
            }
        }
    }

    private suspend fun fail(expected: Int) {
        if (expected != generation || failed || !scope.isActive) return
        failed = true
        ready = false
        withContext(dispatcher) {
            try {
                if (expected == generation) handle?.let { api.lp_control(it, COMMAND_PAUSE, 0.0) }
            } catch (_: Exception) {
            } catch (_: LinkageError) {
            }
        }
        if (expected != generation) return
        proxy?.close()
        proxy = null
        onEvent(PlayerEvent.Error)
        playback?.cancel()
    }

    override fun <R> withImage(block: (Image?) -> R): R = synchronized(frameLock) {
        revision
        block(image)
    }

    private fun clearFrame() {
        val previous = synchronized(frameLock) {
            val previous = image
            image = null
            revision++
            previous
        }
        previous?.close()
    }

    override fun close() {
        if (!scope.isActive) return
        scope.cancel()
        proxy?.close()
        proxy = null
        dispatcher.executor.execute {
            try {
                handle?.let { api.lp_destroy(it) }
            } finally {
                handle = null
                clearFrame()
                dispatcher.close()
            }
        }
    }

    private fun getLibraryPath(): String {
        val property = System.getProperty("compose.application.resources.dir")
        if (property != null) {
            val file = File(property, "player/linux/libplayer.so")
            if (file.isFile) {
                return file.absolutePath
            }
        }

        var file = File("desktopApp/files/player/linux/libplayer.so")
        if (file.isFile) {
            return file.absolutePath
        }

        file = File("files/player/linux/libplayer.so")
        if (file.isFile) {
            return file.absolutePath
        }

        throw RuntimeException()
    }

    companion object {
        private const val COMMAND_PLAY = 0
        private const val COMMAND_PAUSE = 1
        private const val COMMAND_SET_VOLUME = 2
        private const val COMMAND_SET_SPEED = 3
        private const val COMMAND_SEEK = 4
        private const val COMMAND_SELECT_AUDIO = 5
    }
}

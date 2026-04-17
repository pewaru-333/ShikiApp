package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameFilter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.log10

@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val fullscreenHandler = LocalFullscreenHandler.current

    val currentFrame = remember { mutableStateOf<ImageBitmap?>(null) }
    var soundLine by remember { mutableStateOf<SourceDataLine?>(null) }

    LaunchedEffect(state.volume, soundLine) {
        soundLine?.let { line ->
            try {
                if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    val gainControl = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                    val dB = (log10(state.volume.toDouble().coerceIn(0.0001, 1.0)) * 20.0).toFloat()
                    gainControl.value = dB.coerceIn(gainControl.minimum, gainControl.maximum)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {

            }
        }
    }

    LaunchedEffect(state.isFullscreen) {
        if (fullscreenHandler.isFullscreen != state.isFullscreen) {
            fullscreenHandler.toggle()
        }
    }

    LaunchedEffect(state.url) {
        currentFrame.value = null
        val url = state.url

        if (url == null) {
            state.isLoading = false
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            var localGrabber: FFmpegFrameGrabber? = null
            var localSoundLine: SourceDataLine? = null
            var audioFilter: FFmpegFrameFilter? = null

            val converter = Java2DFrameConverter()
            val audioChannel = Channel<ByteArray>(capacity = 100)
            val videoChannel = Channel<Pair<ImageBitmap?, Long>>(capacity = 10)

            state.isLoading = true
            var totalSec = 0f

            val audioJob = launch(Dispatchers.IO) {
                for (audioBytes in audioChannel) {
                    while (isActive && (!state.isPlaying || state.isLoading)) {
                        delay(10)
                    }
                    if (!isActive) break

                    try {
                        localSoundLine?.write(audioBytes, 0, audioBytes.size)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {

                    }
                }
            }

            val videoJob = launch(Dispatchers.Default) {
                var startRealTimeMicros = System.nanoTime() / 1000
                var startVideoTimeMicros = 0.0
                var lastSpeed = state.speed
                var needsClockSync = true
                var lastUpdateTime = 0f

                for (frameData in videoChannel) {
                    while (isActive && (!state.isPlaying || state.isLoading)) {
                        delay(10)
                        needsClockSync = true
                    }
                    if (!isActive) break

                    val timestamp = frameData.second

                    if (timestamp < 0) {
                        needsClockSync = true
                        continue
                    }

                    val image = frameData.first

                    if (needsClockSync || lastSpeed != state.speed) {
                        startRealTimeMicros = System.nanoTime() / 1000
                        startVideoTimeMicros = timestamp.toDouble()
                        lastSpeed = state.speed
                        needsClockSync = false
                    }

                    val currentSpeed = state.speed.coerceAtLeast(0.1f)
                    val nowMicros = System.nanoTime() / 1000
                    val elapsedMicros = nowMicros - startRealTimeMicros
                    val expectedMicros = startVideoTimeMicros + (elapsedMicros * currentSpeed)
                    val delayMicros = timestamp - expectedMicros
                    val delayMicrosAdjusted = (delayMicros / currentSpeed).toLong()

                    if (delayMicrosAdjusted > 3000) {
                        val sleepMs = delayMicrosAdjusted / 1000
                        if (sleepMs > 20) {
                            delay(sleepMs - 10)
                        }

                        val nowAgain = System.nanoTime() / 1000
                        val remainingMicros = timestamp - (startVideoTimeMicros + ((nowAgain - startRealTimeMicros) * currentSpeed))
                        val remainingNanos = (remainingMicros / currentSpeed * 1000).toLong()

                        if (remainingNanos > 0) {
                            java.util.concurrent.locks.LockSupport.parkNanos(remainingNanos)
                        }
                    } else if (delayMicrosAdjusted < -30_000) {
                        if (delayMicrosAdjusted < -1_000_000) {
                            needsClockSync = true
                        }
                    }

                    if (image != null) {
                        currentFrame.value = image
                    }

                    val currentTimeSec = timestamp / 1_000_000f
                    if (abs(currentTimeSec - lastUpdateTime) > 0.06f) {
                        state.updateTime(currentTimeSec, totalSec)
                        lastUpdateTime = currentTimeSec
                    }
                }
            }

            try {
                localGrabber = FFmpegFrameGrabber(url).apply {
                    setOption("reconnect", "1")
                    setOption("reconnect_streamed", "1")
                    sampleMode = FrameGrabber.SampleMode.SHORT
                    start()
                }

                if (localGrabber.audioChannels > 0) {
                    val format = AudioFormat(
                        localGrabber.sampleRate.toFloat(),
                        16,
                        localGrabber.audioChannels,
                        true,
                        false
                    )
                    val info = DataLine.Info(SourceDataLine::class.java, format)
                    localSoundLine = AudioSystem.getLine(info) as SourceDataLine
                    localSoundLine.open(format)
                    localSoundLine.start()
                    soundLine = localSoundLine
                }

                state.isLoading = false

                totalSec = try {
                    localGrabber.lengthInTime / 1_000_000f
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    0f
                }

                var wasPlaying = state.isPlaying
                var lastFilterSpeed = 1f

                val sendAudioSamples: suspend (ShortBuffer) -> Unit = { samples ->
                    samples.rewind()
                    val chunk = ByteArray(samples.capacity() * 2)
                    ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(samples)
                    audioChannel.send(chunk)
                }

                while (isActive) {
                    if (state.isLoading) {
                        delay(16)
                        continue
                    }

                    state.seekTrigger?.let { seekTime ->
                        try {
                            localGrabber.timestamp = (seekTime * 1_000_000).toLong()

                            localSoundLine?.stop()
                            localSoundLine?.flush()

                            while (audioChannel.tryReceive().getOrNull() != null) {}
                            while (videoChannel.tryReceive().getOrNull() != null) {}

                            videoChannel.send(Pair(null, -1L))

                            if (state.isPlaying) {
                                localSoundLine?.start()
                            }

                            audioFilter?.stop()
                            audioFilter?.release()
                            audioFilter = null
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {

                        } finally {
                            state.clearSeekTrigger()
                        }
                    }

                    if (!state.isPlaying) {
                        if (wasPlaying) {
                            localSoundLine?.stop()
                            localSoundLine?.flush()
                            wasPlaying = false
                        }
                        delay(16)
                        continue
                    } else {
                        if (!wasPlaying) {
                            localSoundLine?.start()
                            wasPlaying = true

                            audioFilter?.stop()
                            audioFilter?.release()
                            audioFilter = null
                        }
                    }

                    val frame = try {
                        localGrabber.grabFrame()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        delay(10)
                        continue
                    }

                    if (frame == null) {
                        state.isPlaying = false
                        continue
                    }

                    if (frame.samples != null && state.volume > 0f) {
                        val currentSpeed = state.speed

                        if (currentSpeed == 1f) {
                            if (audioFilter != null) {
                                try { audioFilter.stop() } catch (_: Exception) {}
                                try { audioFilter.release() } catch (_: Exception) {}
                                audioFilter = null
                            }
                            sendAudioSamples(frame.samples[0] as ShortBuffer)
                        } else {
                            try {
                                if (audioFilter == null || lastFilterSpeed != currentSpeed) {
                                    try { audioFilter?.stop() } catch (_: Exception) {}
                                    try { audioFilter?.release() } catch (_: Exception) {}

                                    val safeSpeed = currentSpeed.coerceIn(0.5f, 2.0f)
                                    audioFilter = FFmpegFrameFilter("atempo=$safeSpeed", localGrabber.audioChannels).apply {
                                        sampleRate = localGrabber.sampleRate
                                        sampleFormat = localGrabber.sampleFormat
                                        start()
                                    }
                                    lastFilterSpeed = currentSpeed
                                }

                                audioFilter.push(frame)
                                while (true) {
                                    val filteredFrame = audioFilter.pull() ?: break
                                    if (filteredFrame.samples != null) {
                                        sendAudioSamples(filteredFrame.samples[0] as ShortBuffer)
                                    }
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (_: Exception) {
                                sendAudioSamples(frame.samples[0] as ShortBuffer)
                            }
                        }
                    }

                    if (frame.image != null) {
                        converter.getBufferedImage(frame)?.let { bufferedImage ->
                            videoChannel.send(Pair(bufferedImage.toComposeImageBitmap(), frame.timestamp))
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {

            } finally {
                withContext(NonCancellable) {
                    audioJob.cancel()
                    videoJob.cancel()
                    audioChannel.close()
                    videoChannel.close()
                    soundLine = null

                    try { audioFilter?.stop() } catch (_: Exception) {}
                    try { audioFilter?.release() } catch (_: Exception) {}

                    try { localSoundLine?.stop() } catch (_: Exception) {}
                    try { localSoundLine?.close() } catch (_: Exception) {}

                    try { localGrabber?.stop() } catch (_: Exception) {}
                    try { localGrabber?.release() } catch (_: Exception) {}

                    try { converter.close() } catch (_: Exception) {}
                }
            }
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val frame = currentFrame.value ?: return@Canvas

            val scale = if (state.isZoomed) {
                maxOf(size.width / frame.width, size.height / frame.height)
            } else {
                minOf(size.width / frame.width, size.height / frame.height)
            }

            val sw = frame.width * scale
            val sh = frame.height * scale
            val left = (size.width - sw) / 2f
            val top = (size.height - sh) / 2f

            drawImage(
                image = frame,
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize(sw.toInt(), sh.toInt())
            )
        }
    }
}
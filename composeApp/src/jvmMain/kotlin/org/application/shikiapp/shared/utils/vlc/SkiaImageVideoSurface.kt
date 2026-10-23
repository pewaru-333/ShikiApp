package org.application.shikiapp.shared.utils.vlc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.skia.*
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.StandardBufferFormat
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class SkiaImageVideoSurface : VideoSurface(VideoSurfaceAdapters.getVideoSurfaceAdapter()), AutoCloseable {
    private val lock = ReentrantLock()
    private val videoSurface = CallbackVideoSurface(VideoBufferFormatCallback(), VideoRenderCallback(), true)
    private var frame by mutableIntStateOf(0)
    private var image: Image? = null
    private var closed = false

    fun <R> withImage(block: (Image?) -> R): R = lock.withLock {
        frame
        block(image)
    }

    fun clear() {
        lock.withLock {
            image.also {
                image = null
                frame++
            }
        }?.close()
    }

    override fun attach(mediaPlayer: MediaPlayer) = videoSurface.attach(mediaPlayer)

    override fun close() {
        lock.withLock {
            closed = true
            image.also {
                image = null
                frame++
            }
        }?.close()
    }

    private class VideoBufferFormatCallback : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int) = StandardBufferFormat(sourceWidth, sourceHeight)
        override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) = Unit
        override fun allocatedBuffers(buffers: Array<out ByteBuffer>) = Unit
    }

    private inner class VideoRenderCallback : RenderCallback {
        private var imageInfo: ImageInfo? = null

        override fun lock(mediaPlayer: MediaPlayer) = Unit
        override fun unlock(mediaPlayer: MediaPlayer) = Unit
        override fun display(
            mediaPlayer: MediaPlayer,
            nativeBuffers: Array<out ByteBuffer>,
            bufferFormat: BufferFormat,
            displayWidth: Int,
            displayHeight: Int
        ) {
            val width = if (displayWidth > 0) displayWidth.coerceAtMost(bufferFormat.width)
            else bufferFormat.width

            val height = if (displayHeight > 0) displayHeight.coerceAtMost(bufferFormat.height)
            else bufferFormat.height

            val rowBytes = bufferFormat.pitches[0]
            val byteCount = rowBytes * height

            var info = imageInfo
            if (info == null || info.width != width || info.height != height) {
                info = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.OPAQUE)
                imageInfo = info
            }

            val nextImage = Data.makeUninitialized(byteCount).use { pixels ->
                val size = byteCount.toLong()
                val source = MemorySegment.ofBuffer(nativeBuffers[0])
                val destination = MemorySegment.ofAddress(pixels.writableData()).reinterpret(size)

                MemorySegment.copy(source, 0, destination, 0, size)
                Image.makeRaster(info, pixels, rowBytes)
            }

            lock.withLock {
                if (closed) {
                    nextImage
                } else {
                    image.also {
                        image = nextImage
                        frame++
                    }
                }
            }?.close()
        }
    }
}
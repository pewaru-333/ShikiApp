package org.application.shikiapp.shared.utils.player

import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.utils.ui.LinuxMediaPlayer
import org.application.shikiapp.shared.utils.ui.VideoPlayerState
import org.application.shikiapp.shared.utils.ui.WindowsMediaPlayer
import org.jetbrains.skia.Image

internal interface Player : AutoCloseable {
    fun load(url: String)
    fun play()
    fun pause()
    fun volume(value: Float)
    fun speed(value: Float)
    fun seek(seconds: Float)
    fun audio(index: Int)
    fun subtitle(index: Int)
    fun resize(width: Int, height: Int) = Unit
    fun <R> withImage(block: (Image?) -> R): R

    object Uninitialized : Player {
        override fun load(url: String) = Unit
        override fun play() = Unit
        override fun pause() = Unit
        override fun volume(value: Float) = Unit
        override fun speed(value: Float) = Unit
        override fun seek(seconds: Float) = Unit
        override fun audio(index: Int) = Unit
        override fun subtitle(index: Int) = Unit
        override fun <R> withImage(block: (Image?) -> R): R = block(null)
        override fun close() = Unit
    }

    companion object {
        fun create(state: VideoPlayerState, onEvent: (PlayerEvent) -> Unit): Player {
            val systemName = System.getProperty("os.name")
            return try {
                if (systemName.startsWith(prefix = "windows", ignoreCase = true)) {
                    WindowsMediaPlayer(state, onEvent)
                } else if (systemName.startsWith(prefix = "linux", ignoreCase = true)) {
                    LinuxMediaPlayer(state, onEvent)
                } else {
                    throw RuntimeException()
                }
            } catch (_: Exception) {
                Uninitialized
            }
        }
    }
}
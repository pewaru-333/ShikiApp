package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import kotlinx.coroutines.delay
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Color

@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val mediaPlayerComponent = remember { EmbeddedMediaPlayerComponent() }
    val mediaPlayer = mediaPlayerComponent.mediaPlayer()

    LaunchedEffect(state.url) {
        state.url?.let {
            mediaPlayer.media().play(it)
            if (!state.isPlaying) mediaPlayer.controls().pause()
        }
    }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) mediaPlayer.controls().play() else mediaPlayer.controls().pause()
    }

    LaunchedEffect(state.volume) {
        mediaPlayer.audio().setVolume((state.volume * 100).toInt())
    }

    LaunchedEffect(state.speed) {
        mediaPlayer.controls().setRate(state.speed)
    }

    LaunchedEffect(state.seekTrigger) {
        state.seekTrigger?.let {
            mediaPlayer.controls().setTime((it * 1000).toLong())
            state.clearSeekTrigger()
        }
    }

    LaunchedEffect(state.isZoomed) {
        if (state.isZoomed) {
            mediaPlayer.video().setAspectRatio("16:9")
        } else {
            mediaPlayer.video().setAspectRatio(null)
        }
    }

    LaunchedEffect(mediaPlayer) {
        mediaPlayer.events().addMediaPlayerEventListener(
            object : MediaPlayerEventAdapter() {
                override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
                    state.isLoading = newCache < 100f
                }
            }
        )

        while (true) {
            if (mediaPlayer.status().isPlaying) {
                state.updateTime(
                    mediaPlayer.status().time() / 1000f,
                    mediaPlayer.status().length() / 1000f
                )
            }
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.controls().stop()
            mediaPlayer.release()
            mediaPlayerComponent.release()
        }
    }

    SwingPanel(
        modifier = modifier,
        background = androidx.compose.ui.graphics.Color.Black,
        factory = {
            mediaPlayerComponent.apply {
                videoSurfaceComponent().background = Color.BLACK
            }
        }
    )
}
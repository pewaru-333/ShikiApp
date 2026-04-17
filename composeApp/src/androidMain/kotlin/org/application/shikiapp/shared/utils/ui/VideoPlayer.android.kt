package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import kotlinx.coroutines.delay

@UnstableApi
@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val context = LocalContext.current

    val exoPlayer = remember(context.applicationContext) {
        ExoPlayer.Builder(context).build().apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        state.isLoading = playbackState == Player.STATE_BUFFERING
                    }
                }
            )
        }
    }

    LaunchedEffect(state.url) {
        state.url?.let {
            exoPlayer.setMediaItem(MediaItem.fromUri(it))
            exoPlayer.prepare()
        }
    }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) exoPlayer.play() else exoPlayer.pause()
    }

    LaunchedEffect(state.volume) {
        exoPlayer.volume = state.volume
    }

    LaunchedEffect(state.speed) {
        exoPlayer.setPlaybackSpeed(state.speed)
    }

    LaunchedEffect(state.seekTrigger) {
        state.seekTrigger?.let {
            exoPlayer.seekTo((it * 1000).toLong())
            state.clearSeekTrigger()
        }
    }

    LaunchedEffect(state.isZoomed) {
        exoPlayer.videoScalingMode = if (state.isZoomed) {
            C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        } else {
            C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            val total = exoPlayer.duration.coerceAtLeast(0) / 1000f

            if (total > 0f) {
                if (exoPlayer.isPlaying) {
                    val current = exoPlayer.currentPosition / 1000f
                    state.updateTime(current, total)
                }
                state.updateBuffer(exoPlayer.bufferedPercentage / 100f)
            }

            delay(500L)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        exoPlayer.pause()
        state.pause()
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }

    ContentFrame(
        player = exoPlayer,
        modifier = modifier,
        contentScale = if (state.isZoomed) ContentScale.FillBounds else ContentScale.Fit
    )
}
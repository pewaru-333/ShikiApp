package org.application.shikiapp.shared.utils.ui

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.text.Cue
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.compose.ContentFrame
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.utils.BLANK

@UnstableApi
class VideoPlayerController(private val context: Context, private val state: VideoPlayerState) {
    val exoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
        addListener(PlayerEventListener())
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            state.isLoading = playbackState == Player.STATE_BUFFERING

            if (playbackState == Player.STATE_ENDED) {
                state.isVideoEnded = true
                state.isPlaying = false
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            state.isLoading = false
            state.playNext()
        }

        override fun onCues(cueGroup: CueGroup) {
            state.currentCues = cueGroup.cues.mapNotNull(Cue::text)
        }

        override fun onTracksChanged(tracks: Tracks) {
            val isAdaptive = state.url?.let {
                it.contains(".m3u8", ignoreCase = true) ||
                        it.contains(".mpd", ignoreCase = true)
            }

            if (isAdaptive == true) {
                val qualities = tracks.groups
                    .asSequence()
                    .filter { it.type == C.TRACK_TYPE_VIDEO }
                    .flatMap { group -> (0 until group.length).map { group.getTrackFormat(it).height } }
                    .filter { it > 0 }
                    .distinct()
                    .sortedDescending()
                    .toList()

                if (qualities.isNotEmpty()) {
                    state.qualityList = qualities
                }
            }

            val quality = tracks.groups
                .asSequence()
                .filter { it.type == C.TRACK_TYPE_VIDEO }
                .firstNotNullOfOrNull { group ->
                    (0 until group.length)
                        .firstOrNull { group.isTrackSelected(it) }
                        ?.let { group.getTrackFormat(it).height }
                }

            if (quality != null && state.currentQuality != quality) {
                state.currentQuality = quality
            }

            state.tracksRevision++
        }
    }

    internal fun loadVideo() {
        val currentUrl = state.url ?: return

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(state.headers.getOrDefault("User-Agent", BLANK))
            .setDefaultRequestProperties(state.headers)
            .setAllowCrossProtocolRedirects(true)

        val subtitleConfigs = state.subtitles.map { subtitleTrack ->
            val mimeType = when (MimeTypeMap.getFileExtensionFromUrl(subtitleTrack.url)) {
                "srt" -> MimeTypes.APPLICATION_SUBRIP
                "vtt" -> MimeTypes.TEXT_VTT
                "ttml", "xml" -> MimeTypes.APPLICATION_TTML
                "ssa", "ass" -> MimeTypes.TEXT_SSA
                else -> MimeTypes.TEXT_UNKNOWN
            }

            MediaItem.SubtitleConfiguration.Builder(subtitleTrack.url.toUri())
                .setMimeType(mimeType)
                .setLabel(subtitleTrack.name)
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()
        }

        val mediaItem = MediaItem.Builder()
            .setUri(currentUrl)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()

        val mediaSource = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)
            .createMediaSource(mediaItem)

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = state.isPlaying
    }

    internal fun setQuality(quality: Int) {
        exoPlayer.currentTracks.groups
            .asSequence()
            .filter { it.type == C.TRACK_TYPE_VIDEO }
            .firstNotNullOfOrNull { group ->
                (0 until group.length)
                    .firstOrNull { i -> group.getTrackFormat(i).height == quality }
                    ?.let { index -> TrackSelectionOverride(group.mediaTrackGroup, index) }
            }
            ?.let { override ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setOverrideForType(override)
                    .build()
            }
    }

    internal fun setAudioTrack(audioTrackIndex: Int) {
        exoPlayer.currentTracks.groups
            .asSequence()
            .filter { it.type == C.TRACK_TYPE_AUDIO }
            .elementAtOrNull(audioTrackIndex)
            ?.let { group ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, 0))
                    .build()
            }
    }

    internal fun setSubtitleTrack() {
        val builder = exoPlayer.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_TEXT)
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, state.selectedSubtitlesTrack == null)

        if (state.selectedSubtitlesTrack != null) {
            exoPlayer.currentTracks.groups
                .firstOrNull { it.type == C.TRACK_TYPE_TEXT && it.getTrackFormat(0).label == state.selectedSubtitlesTrack }
                ?.let { builder.setOverrideForType(TrackSelectionOverride(it.mediaTrackGroup, 0)) }
        }

        exoPlayer.trackSelectionParameters = builder.build()
    }
}

@UnstableApi
@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val context = LocalContext.current

    val controller = remember(context.applicationContext) { VideoPlayerController(context, state) }
    val exoPlayer = controller.exoPlayer

    LaunchedEffect(state.url) {
        controller.loadVideo()
    }

    LaunchedEffect(state.currentQuality, state.tracksRevision) {
        state.currentQuality?.let(controller::setQuality)
    }

    LaunchedEffect(state.audioTrackIndex, state.tracksRevision) {
        state.audioTrackIndex?.let(controller::setAudioTrack)
    }

    LaunchedEffect(state.selectedSubtitlesTrack) {
        controller.setSubtitleTrack()
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

    LaunchedEffect(state.seekTrigger, state.totalTime) {
        state.seekTrigger?.let { seconds ->
            if (state.totalTime > 0f) {
                exoPlayer.seekTo((seconds * 1000).toLong())
            }
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

actual class VideoPlayerUtils actual constructor(private val context: Context) {
    actual constructor() : this(AppContext.app.context)

    actual val isTV: Boolean
        get() {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
                    context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        }

    actual val showPlayPause = true
    actual val visibilityDelay = if (isTV) 6000L else 3000L
    actual val pointerIcon = PointerIcon.Default
}
package org.application.shikiapp.shared.utils.ui

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.webkit.MimeTypeMap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.AudioAttributes
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
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.ui.compose.ContentFrame
import io.github.peerless2012.ass.media.AssHandler
import io.github.peerless2012.ass.media.kt.withAssMkvSupport
import io.github.peerless2012.ass.media.kt.withAssSupport
import io.github.peerless2012.ass.media.parser.AssSubtitleParserFactory
import io.github.peerless2012.ass.media.type.AssRenderType
import io.github.peerless2012.ass.media.widget.AssSubtitleView
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
class VideoPlayerController(private val context: Context, private val state: VideoPlayerState) {
    val assHandler = AssHandler(AssRenderType.EFFECTS_OPEN_GL)
    private val assParserFactory = AssSubtitleParserFactory(assHandler)
    private val extractorsFactory = DefaultExtractorsFactory().withAssMkvSupport(assParserFactory, assHandler)
    private val mediaSourceFactory = DefaultMediaSourceFactory(context, extractorsFactory).setSubtitleParserFactory(assParserFactory)
    private val renderersFactory = DefaultRenderersFactory(context).withAssSupport(assHandler)

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()

    val exoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setAudioAttributes(audioAttributes, true)
        .setMediaSourceFactory(mediaSourceFactory)
        .setRenderersFactory(renderersFactory)
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply {
            addListener(PlayerEventListener())
            assHandler.init(this)
        }

    var cues by mutableStateOf<List<Cue>>(emptyList())
        private set

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
            cues = cueGroup.cues
        }

        override fun onTracksChanged(tracks: Tracks) {
            val isAdaptive = state.url?.let {
                it.contains(".m3u8", ignoreCase = true) ||
                        it.contains(".mpd", ignoreCase = true)
            }

            if (isAdaptive == true) {
                val qualities = HashSet<Int>()
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_VIDEO) {
                        for (i in 0 until group.length) {
                            val height = group.getTrackFormat(i).height
                            if (height > 0) {
                                qualities.add(height)
                            }
                        }
                    }
                }

                if (qualities.isNotEmpty()) {
                    state.qualityList = qualities.sortedDescending()
                }
            }

            var quality: Int? = null
            search@ for (group in tracks.groups) {
                if (group.type == C.TRACK_TYPE_VIDEO) {
                    for (i in 0 until group.length) {
                        if (group.isTrackSelected(i)) {
                            quality = group.getTrackFormat(i).height
                            break@search
                        }
                    }
                }
            }

            if (quality != null && state.currentQuality != quality) {
                state.currentQuality = quality
            }

            state.tracksRevision++
        }
    }

    internal fun loadVideo(url: String) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(state.headers.getOrDefault("User-Agent", BLANK))
            .setDefaultRequestProperties(state.headers)
            .setAllowCrossProtocolRedirects(true)

        val subtitleConfigs = state.subtitles.mapIndexed { index, subtitleTrack ->
            val mimeType = when (MimeTypeMap.getFileExtensionFromUrl(subtitleTrack.url)) {
                "vtt" -> MimeTypes.TEXT_VTT
                "ssa", "ass" -> MimeTypes.TEXT_SSA
                "srt" -> MimeTypes.APPLICATION_SUBRIP
                "ttml", "xml" -> MimeTypes.APPLICATION_TTML
                else -> MimeTypes.TEXT_UNKNOWN
            }

            MediaItem.SubtitleConfiguration.Builder(subtitleTrack.url.toUri())
                .setId((index + 1024).toString())
                .setMimeType(mimeType)
                .setLabel(subtitleTrack.name)
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()
        }

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()

        val mediaSource = DefaultMediaSourceFactory(context, extractorsFactory)
            .setDataSourceFactory(dataSourceFactory)
            .setSubtitleParserFactory(assParserFactory)
            .createMediaSource(mediaItem)

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = state.isPlaying
    }

    internal fun setQuality(quality: Int) {
        for (group in exoPlayer.currentTracks.groups) {
            if (group.type == C.TRACK_TYPE_VIDEO) {
                for (i in 0 until group.length) {
                    if (group.getTrackFormat(i).height == quality) {
                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                            .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, i))
                            .build()

                        return
                    }
                }
            }
        }
    }

    internal fun setAudioTrack(audioTrackIndex: Int) {
        var index = 0

        for (group in exoPlayer.currentTracks.groups) {
            if (group.type == C.TRACK_TYPE_AUDIO) {
                if (index == audioTrackIndex) {
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                        .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, 0))
                        .build()

                    return
                }

                index++
            }
        }
    }

    internal fun setSubtitleTrack() {
        val builder = exoPlayer.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_TEXT)
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, state.selectedSubtitlesTrack == null)

        if (state.selectedSubtitlesTrack != null) {
            exoPlayer.currentTracks.groups
                .find { it.type == C.TRACK_TYPE_TEXT && it.getTrackFormat(0).label == state.selectedSubtitlesTrack }
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
        state.url?.let(controller::loadVideo)
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

            delay(500.milliseconds)
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

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        update = { it.cues = controller.cues },
        factory = { context ->
            SubtitleView(context).apply {
                addView(AssSubtitleView(context, controller.assHandler))
                setUserDefaultStyle()
                setUserDefaultTextSize()
                viewType = SubtitleView.VIEW_TYPE_WEB
            }
        }
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
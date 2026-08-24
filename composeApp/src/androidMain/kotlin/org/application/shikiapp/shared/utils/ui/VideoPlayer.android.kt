package org.application.shikiapp.shared.utils.ui

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.webkit.MimeTypeMap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.*
import androidx.media3.common.text.Cue
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.ui.compose.PlayerSurface
import io.github.peerless2012.ass.media.AssHandler
import io.github.peerless2012.ass.media.kt.withAssMkvSupport
import io.github.peerless2012.ass.media.kt.withAssSupport
import io.github.peerless2012.ass.media.parser.AssSubtitleParserFactory
import io.github.peerless2012.ass.media.type.AssRenderType
import io.github.peerless2012.ass.media.widget.AssSubtitleView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
class VideoPlayerController(private val context: Context, private val state: VideoPlayerState) {
    val assHandler = AssHandler(AssRenderType.EFFECTS_OPEN_GL)
    private val assParserFactory = AssSubtitleParserFactory(assHandler)
    private val extractorsFactory = DefaultExtractorsFactory().withAssMkvSupport(assParserFactory, assHandler)
    private val renderersFactory = DefaultRenderersFactory(context).withAssSupport(assHandler)

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()

    internal val player: Player
        field = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setRenderersFactory(renderersFactory)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(PlayerEventListener())
                assHandler.init(this)
            }

    internal var cues by mutableStateOf<List<Cue>>(emptyList())
        private set

    internal var videoSize by mutableStateOf(Size.Unspecified)
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
                val type = Util.inferContentType(it.toUri())

                type == C.CONTENT_TYPE_HLS || type == C.CONTENT_TYPE_DASH
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

                if (qualities.isNotEmpty() && (qualities.size > 1 || state.qualityList.isEmpty())) {
                    state.qualityList = qualities.sortedDescending()
                }
            }

            var quality: Int? = null
            search@ for (group in tracks.groups) {
                if (group.type == C.TRACK_TYPE_VIDEO) {
                    for (i in 0 until group.length) {
                        if (group.isTrackSelected(i)) {
                            val trackFormat = group.getTrackFormat(i)

                            val width = trackFormat.width
                            val height = trackFormat.height
                            val rotation = trackFormat.rotationDegrees

                            videoSize = if (rotation == 90 || rotation == 270) {
                                Size(height.toFloat(), width.toFloat())
                            } else {
                                Size(width.toFloat(), height.toFloat())
                            }

                            quality = height
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

    internal fun loadVideo() {
        val url = state.url ?: return

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

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = state.isPlaying
    }

    internal fun play() {
        if (state.isPlaying) player.play() else player.pause()
    }

    internal fun pause() {
        player.pause()
        state.pause()
    }

    internal fun release() {
        player.stop()
        player.clearMediaItems()
        player.release()
    }

    internal fun setVolume() {
        player.volume = state.volume
    }

    internal fun setSpeed() {
        player.setPlaybackSpeed(state.speed)
    }

    internal fun seek() {
        state.seekTrigger?.let { seconds ->
            if (state.totalTime > 0f) {
                player.seekTo((seconds * 1000).toLong())
            }
        }
    }

    internal suspend fun updateBuffer() {
        val total = player.duration.coerceAtLeast(0) / 1000f

        if (total > 0f) {
            if (player.isPlaying) {
                val current = player.currentPosition / 1000f
                state.updateTime(current, total)
            }
            state.updateBuffer(player.bufferedPercentage / 100f)
        }

        delay(if (state.isPlaying) 1000.milliseconds else 3000.milliseconds)
    }

    internal fun setQuality() {
        val quality = state.currentQuality ?: return

        for (group in player.currentTracks.groups) {
            if (group.type == C.TRACK_TYPE_VIDEO) {
                for (i in 0 until group.length) {
                    if (group.getTrackFormat(i).height == quality) {
                        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                            .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, i))
                            .build()

                        return
                    }
                }
            }
        }
    }

    internal fun setAudioTrack() {
        val audioTrackIndex = state.audioTrackIndex ?: return
        var index = 0

        for (group in player.currentTracks.groups) {
            if (group.type == C.TRACK_TYPE_AUDIO) {
                if (index == audioTrackIndex) {
                    player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                        .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, 0))
                        .build()

                    return
                }

                index++
            }
        }
    }

    internal fun setSubtitleTrack() {
        val builder = player.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_TEXT)
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, state.selectedSubtitlesTrack == null)

        if (state.selectedSubtitlesTrack != null) {
            player.currentTracks.groups
                .find { it.type == C.TRACK_TYPE_TEXT && it.getTrackFormat(0).label == state.selectedSubtitlesTrack }
                ?.let { builder.setOverrideForType(TrackSelectionOverride(it.mediaTrackGroup, 0)) }
        }

        player.trackSelectionParameters = builder.build()
    }
}

@UnstableApi
@Composable
actual fun VideoPlayer(state: VideoPlayerState, modifier: Modifier) {
    val context = LocalContext.current
    val controller = remember(context) { VideoPlayerController(context, state) }

    LaunchedEffect(state.url) {
        controller.loadVideo()
    }

    LaunchedEffect(state.currentQuality, state.tracksRevision) {
        controller.setQuality()
    }

    LaunchedEffect(state.audioTrackIndex, state.tracksRevision) {
        controller.setAudioTrack()
    }

    LaunchedEffect(state.selectedSubtitlesTrack, state.tracksRevision) {
        controller.setSubtitleTrack()
    }

    LaunchedEffect(state.isPlaying) {
        controller.play()
    }

    LaunchedEffect(state.volume) {
        controller.setVolume()
    }

    LaunchedEffect(state.speed) {
        controller.setSpeed()
    }

    LaunchedEffect(state.seekTrigger, state.totalTime) {
        controller.seek()
    }

    LaunchedEffect(controller) {
        while (isActive) {
            controller.updateBuffer()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        controller.pause()
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    BoxWithConstraints(modifier) {
        val scaleValue = remember(state.isZoomed, controller.videoSize, constraints) {
            if (!state.isZoomed) return@remember 1f

            val videoSize = controller.videoSize
            if (videoSize == Size.Unspecified) return@remember 1f

            val maxSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            val fit = ContentScale.Fit.computeScaleFactor(videoSize, maxSize).scaleX

            if (fit <= 0f) return@remember 1f

            val crop = ContentScale.Crop.computeScaleFactor(videoSize, maxSize).scaleX

            crop / fit
        }

        val scale by animateFloatAsState(
            targetValue = scaleValue,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        PlayerSurface(
            player = controller.player,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )

        AndroidView(
            modifier = Modifier.matchParentSize(),
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
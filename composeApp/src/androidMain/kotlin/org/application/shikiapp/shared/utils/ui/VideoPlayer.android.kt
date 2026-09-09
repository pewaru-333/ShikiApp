package org.application.shikiapp.shared.utils.ui

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.app.UiModeManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Consumer
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
import org.application.shikiapp.shared.R
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.extensions.toBitmap
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@UnstableApi
actual class VideoPlayerController(private val activity: ComponentActivity): VideoPlayer(), VideoPlayer.VideoPlayerPictureInPicture {
    val assHandler = AssHandler(AssRenderType.OVERLAY_OPEN_GL)
    private val assParserFactory = AssSubtitleParserFactory(assHandler)
    private val extractorsFactory = DefaultExtractorsFactory().withAssMkvSupport(assParserFactory, assHandler)
    private val renderersFactory = DefaultRenderersFactory(activity).withAssSupport(assHandler)

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()

    private val pipActionPlay = RemoteAction(
        Icon.createWithBitmap(Icons.PlayCircle.toBitmap(activity)),
        activity.getString(R.string.pip_action_play),
        activity.getString(R.string.pip_action_play),
        createPendingIntentPiP(EXTRA_CONTROL_PLAY)
    )

    private val pipActionPause = RemoteAction(
        Icon.createWithBitmap(Icons.PauseCircle.toBitmap(activity)),
        activity.getString(R.string.pip_action_pause),
        activity.getString(R.string.pip_action_pause),
        createPendingIntentPiP(EXTRA_CONTROL_PAUSE)
    )

    private val pipActionSeekLeft = RemoteAction(
        Icon.createWithBitmap(Icons.TenSecondsLeft.toBitmap(activity)),
        activity.getString(R.string.pip_action_seek_left),
        activity.getString(R.string.pip_action_seek_left),
        createPendingIntentPiP(EXTRA_CONTROL_SEEK_LEFT)
    )

    private val pipActionSeekRight = RemoteAction(
        Icon.createWithBitmap(Icons.TenSecondsRight.toBitmap(activity)),
        activity.getString(R.string.pip_action_seek_right),
        activity.getString(R.string.pip_action_seek_right),
        createPendingIntentPiP(EXTRA_CONTROL_SEEK_RIGHT)
    )

    private val broadcastReceiverPIP = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != PLAYER_ACTION_BROADCAST) {
                return
            }

            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, EXTRA_CONTROL_PLAY)) {
                EXTRA_CONTROL_PLAY -> playPiP()
                EXTRA_CONTROL_PAUSE -> pausePiP()
                EXTRA_CONTROL_SEEK_LEFT -> seek(-10f)
                EXTRA_CONTROL_SEEK_RIGHT -> seek(10f)
            }
        }
    }

    internal val player: Player
        field = ExoPlayer.Builder(activity)
            .setAudioAttributes(audioAttributes, true)
            .setRenderersFactory(renderersFactory)
            .setHandleAudioBecomingNoisy(true)
            .build()


    internal var cues by mutableStateOf<List<Cue>>(emptyList())
        private set

    internal var videoSize by mutableStateOf(Size.Unspecified)
        private set

    override val feature = object : VideoPlayerFeature {
        override val isTV: Boolean
            get() {
                val manager = activity.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

                return manager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
                        activity.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
            }

        override val pictureInPicture = if (isTV) null else this@VideoPlayerController
        override val showPlayPause = true
        override val visibilityDelay = if (isTV) 6000L else 3000L
        override val pointerIcon = PointerIcon.Default
    }

    private val playerEventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            state.isLoading = playbackState == Player.STATE_BUFFERING
            state.isVideoEnded = playbackState == Player.STATE_ENDED

            if (playbackState == Player.STATE_ENDED) {
                onEvent(PlayerEvent.Ended)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            state.isPlaying = isPlaying

            onEvent(if (isPlaying) PlayerEvent.Play else PlayerEvent.Pause)
        }

        override fun onPlayerError(error: PlaybackException) {
            playNext()
        }

        override fun onCues(cueGroup: CueGroup) {
            cues = cueGroup.cues
        }

        override fun onVolumeChanged(volume: Float) {
            updateVolume(volume)
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            updateSpeed(playbackParameters.speed)
        }

        override fun onTracksChanged(tracks: Tracks) {
            state.audioTrackIndex?.let(::onLoadAudioTrack)

            if (!tracks.isTypeSelected(C.TRACK_TYPE_TEXT)) updateSubtitleTrack(null)
            else tracks.groups
                .find { it.type == C.TRACK_TYPE_TEXT && it.isSelected }
                ?.let { updateSubtitleTrack(it.getTrackFormat(0).label) }

            getAdaptiveQuality(tracks)
        }
    }

    override fun onLoadVideo(url: String) {
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
                .build()
        }

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()

        val mediaSource = DefaultMediaSourceFactory(activity, extractorsFactory)
            .setDataSourceFactory(dataSourceFactory)
            .setSubtitleParserFactory(assParserFactory)
            .createMediaSource(mediaItem)


        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }

    override fun create() {
        player.addListener(playerEventListener)
        assHandler.init(player)
    }

    override fun release() {
        player.stop()
        player.clearMediaItems()
        player.release()
    }

    override fun onPlay() {
        player.play()
    }

    override fun onPause() {
        player.pause()
    }

    override fun onSetVolume(volume: Float) {
        player.volume = volume
    }

    override fun onSetSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun onSeek(millis: Float) {
        player.seekTo((millis * 1000).toLong())
    }

    override fun enterPIP() {
        if (activity.isInPictureInPictureMode) return

        activity.enterPictureInPictureMode(createPipParams())
        controls.hideControls()
    }

    override fun onLoadAudioTrack(index: Int) {
        var searchIndex = 0

        for (group in player.currentTracks.groups) {
            if (group.type == C.TRACK_TYPE_AUDIO) {
                if (searchIndex == index) {
                    if (group.isSelected) return

                    player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                        .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, 0))
                        .build()

                    return
                }

                searchIndex++
            }
        }
    }

    override fun onLoadSubtitleTrack(index: Int) {
        val builder = player.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_TEXT)
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, index == 0)

        if (index > 0) {
            var searchIndex = 0

            for (group in player.currentTracks.groups) {
                if (group.type == C.TRACK_TYPE_TEXT) {
                    if (searchIndex == index) {
                        if (group.isSelected) return

                        builder.setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, 0))

                        break
                    }

                    searchIndex++
                }
            }
        }

        player.trackSelectionParameters = builder.build()
    }

    internal fun playPiP() {
        onPlay()
        activity.setPictureInPictureParams(createPipParams())
    }

    internal fun pausePiP() {
        onPause()
        activity.setPictureInPictureParams(createPipParams())
    }

    internal suspend fun updateBuffer() {
        val duration = player.duration
        val isPlaying = player.isPlaying

        if (isPlaying && duration != C.TIME_UNSET) {
            val total = duration / 1000f
            val current = player.currentPosition / 1000f

            state.currentTime = current
            state.totalTime = total
        }

        updateBuffer(player.bufferedPercentage / 100f)

        delay(if (isPlaying) 1.seconds else 3.seconds)
    }

    internal fun registerBroadcastReceiverPIP() {
        ContextCompat.registerReceiver(
            activity,
            broadcastReceiverPIP,
            IntentFilter(PLAYER_ACTION_BROADCAST),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    internal fun unregisterBroadcastReceiverPIP() {
        activity.unregisterReceiver(broadcastReceiverPIP)
    }

    private fun createPipParams() = PictureInPictureParams.Builder()
        .setActions(createPipActions())
        .build()

    private fun createPipActions() = buildList {
        add(pipActionSeekLeft)
        add(if (state.isPlaying) pipActionPause else pipActionPlay)
        add(pipActionSeekRight)
    }

    private fun createPendingIntentPiP(actionCode: Int) = PendingIntent.getBroadcast(
        activity,
        actionCode,
        Intent(PLAYER_ACTION_BROADCAST).apply {
            `package` = activity.packageName
            putExtra(EXTRA_CONTROL_TYPE, actionCode)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun getAdaptiveQuality(tracks: Tracks) {
        val url = state.url ?: return
        val type = Util.inferContentType(url.toUri())
        val isAdaptive = type == C.CONTENT_TYPE_HLS || type == C.CONTENT_TYPE_DASH

        for (group in tracks.groups) {
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

                        if (height <= 0) continue

                        if (isAdaptive && state.qualityList.size <= 1) {
                            state.qualityList = listOf(height)
                        }

                        if (group.isTrackSelected(i)) {
                            state.currentQuality = height
                        }
                    }
                }
            }
        }
    }

    companion object {
        internal const val PLAYER_ACTION_BROADCAST = "player_action_playback_control"
        internal const val EXTRA_CONTROL_TYPE = "control_type"
        internal const val EXTRA_CONTROL_PLAY = 1
        internal const val EXTRA_CONTROL_PAUSE = 2
        internal const val EXTRA_CONTROL_SEEK_LEFT = 3
        internal const val EXTRA_CONTROL_SEEK_RIGHT = 4
    }
}

@OptIn(UnstableApi::class)
@Composable
actual fun rememberVideoPlayerController(onEvent: (PlayerEvent) -> Unit): VideoPlayerController {
    val activity = LocalActivity.current as? ComponentActivity ?: throw ActivityNotFoundException()
    val currentEvent by rememberUpdatedState(onEvent)

    return remember(activity) {
        VideoPlayerController(activity).apply {
            eventListener = currentEvent
        }
    }
}

@UnstableApi
@Composable
actual fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier) {
    val isPiP = rememberIsInPipMode()

    LaunchedEffect(controller) {
        while (isActive) {
            controller.updateBuffer()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        controller.pause()
    }

    DisposableEffect(controller.player, isPiP) {
        if (isPiP) {
            controller.registerBroadcastReceiverPIP()

            onDispose { controller.unregisterBroadcastReceiverPIP() }
        }

        onDispose { }
    }

    BoxWithConstraints(modifier, Alignment.Center) {
        val boxWidth = constraints.maxWidth.toFloat()
        val boxHeight = constraints.maxHeight.toFloat()
        val maxSize = Size(boxWidth, boxHeight)

        val videoSize = controller.videoSize
        val hasVideo = videoSize != Size.Unspecified && videoSize.width > 0f && videoSize.height > 0f

        val fitScale = if (hasVideo) ContentScale.Fit.computeScaleFactor(videoSize, maxSize).scaleX
        else 1f

        val baseWidth = if (hasVideo) (videoSize.width * fitScale).roundToInt()
        else boxWidth.roundToInt()

        val baseHeight = if (hasVideo) (videoSize.height * fitScale).roundToInt()
        else boxHeight.roundToInt()

        val scaleValue = remember(controller.state.isZoomed, controller.videoSize, constraints) {
            if (!controller.state.isZoomed || !hasVideo || fitScale <= 0f) {
                1f
            } else {
                ContentScale.Crop.computeScaleFactor(videoSize, maxSize).scaleX / fitScale
            }
        }

        val animatedScale by animateFloatAsState(
            targetValue = scaleValue,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        val overflowHeight = baseHeight * animatedScale - boxHeight
        val offsetY = if (overflowHeight > 0f) -overflowHeight / 2f else 0f

        val scaleModifier = Modifier.graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
            translationY = offsetY
        }

        PlayerSurface(
            player = controller.player,
            modifier = scaleModifier.layout { measurable, _ ->
                val placeable = measurable.measure(Constraints.fixed(baseWidth, baseHeight))

                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
        )

        AndroidView(
            modifier = scaleModifier.layout { measurable, _ ->
                val placeable = measurable.measure(Constraints.fixed(baseWidth, baseHeight))

                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            },
            update = { it.cues = controller.cues },
            factory = { context ->
                SubtitleView(context).apply {
                    addView(AssSubtitleView(context, controller.assHandler))
                    setUserDefaultStyle()
                    setUserDefaultTextSize()
                    viewType = SubtitleView.VIEW_TYPE_CANVAS
                }
            }
        )
    }
}

@Composable
fun rememberIsInPipMode(): Boolean {
    val activity = LocalActivity.current as? ComponentActivity ?: throw ActivityNotFoundException()

    var pipMode by remember { mutableStateOf(activity.isInPictureInPictureMode) }

    DisposableEffect(activity) {
        val observer = Consumer<PictureInPictureModeChangedInfo> { info ->
            pipMode = info.isInPictureInPictureMode
        }

        activity.addOnPictureInPictureModeChangedListener(observer)

        onDispose { activity.removeOnPictureInPictureModeChangedListener(observer) }
    }

    return pipMode
}
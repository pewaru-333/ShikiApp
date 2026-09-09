package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import kotlin.ranges.coerceIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Stable
class VideoPlayerState {
    val speedList = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    // Ссылка, статус, время
    var url by mutableStateOf<String?>(null)
        internal set
    var isPlaying by mutableStateOf(true)
        internal set
    var isLoading by mutableStateOf(true)
        internal set
    var isVideoEnded by mutableStateOf(false)
        internal set
    var currentTime by mutableFloatStateOf(0f)
        internal set
    var totalTime by mutableFloatStateOf(0f)
        internal set

    // Громкость, скорость, качество
    var volume by mutableFloatStateOf(1f)
        internal set
    var speed by mutableFloatStateOf(speedList[2])
        internal set
    var currentQuality by mutableStateOf<Int?>(null)
        internal set
    var qualityList by mutableStateOf<List<Int>>(emptyList())
        internal set

    // Звук и субтитры
    var audioTrackIndex by mutableStateOf<Int?>(null)
        internal set
    var subtitles by mutableStateOf<List<SubtitleTrack>>(emptyList())
        internal set
    var selectedSubtitlesTrack by mutableStateOf<String?>(null)
        internal set

    // Масштабирование
    var isZoomed by mutableStateOf(false)
        internal set

    // Запасные ссылки и заголовки
    var headers by mutableStateOf<Map<String, String>>(emptyMap())
        internal set
    var fallbackUrls by mutableStateOf<List<String>>(emptyList())
        internal set
    var bufferPercentage by mutableFloatStateOf(0f)
        internal set
}

abstract class VideoPlayer {
    val state = VideoPlayerState()
    val controls = VideoPlayerControls()

    abstract val feature: VideoPlayerFeature

    internal var eventListener: ((PlayerEvent) -> Unit)? = null


    abstract fun create()
    abstract fun release()

    protected abstract fun onLoadVideo(url: String)


    protected abstract fun onPlay()
    protected abstract fun onPause()

    protected abstract fun onSetVolume(volume: Float)
    protected abstract fun onSetSpeed(speed: Float)
    protected abstract fun onSeek(millis: Float)

    protected abstract fun onLoadAudioTrack(index: Int)
    protected abstract fun onLoadSubtitleTrack(index: Int)


    fun onEvent(event: PlayerEvent) {
        when (event) {
            PlayerEvent.Play -> Unit
            PlayerEvent.Pause -> Unit
            PlayerEvent.Ended -> Unit

            is PlayerEvent.ChangeQuality -> controls.hideControls()

            is PlayerEvent.SelectEpisode -> {
                state.totalTime = 0f
                state.currentTime = 0f
                state.bufferPercentage = 0f
                controls.onSetSliderValue(0f)

                state.isLoading = true
                state.isPlaying = false
                state.isVideoEnded = false

                state.currentQuality = null

                controls.hideControls()
            }
        }

        eventListener?.invoke(event)
    }


    fun loadVideo(
        url: String,
        fallback: List<String> = emptyList(),
        qualityList: List<Int> = emptyList(),
        trackIndex: Int? = null,
        subtitles: List<SubtitleTrack> = emptyList(),
        headers: Map<String, String> = emptyMap()
    ) {
        state.url = url
        state.fallbackUrls = fallback
        state.headers = headers
        state.audioTrackIndex = trackIndex
        state.subtitles = subtitles

        state.totalTime = 0f
        state.currentTime = 0f
        state.bufferPercentage = 0f
        controls.onSetSliderValue(0f)

        state.isLoading = true
        state.isPlaying = false
        state.isVideoEnded = false

        state.selectedSubtitlesTrack = null
        state.currentQuality = null
        state.qualityList = qualityList

        controls.hideControls()

        onLoadVideo(url)
        onLoadSubtitleTrack(0)
    }

    fun loadVideo(url: String) {
        controls.hideControls()

        onLoadVideo(url)
        seekTo(state.currentTime)
    }

//    fun play() {
//        onPlay()
//    }

    fun pause() {
        onPause()
    }

    fun togglePlayPause() {
        if (state.isPlaying) onPause() else onPlay()
    }

    fun setVolume(volume: Float) {
        val value = volume.coerceIn(0f, 1f)

        onSetVolume(value)
    }

    fun toggleSpeed() {
        val nextIndex = (state.speedList.indexOf(state.speed) + 1) % state.speedList.size
        val newSpeed = state.speedList[nextIndex]

        onSetSpeed(newSpeed)
    }

    fun seekTo(seconds: Float) {
        val target = seconds.coerceIn(0f, state.totalTime.takeIf { it > 0f } ?: Float.MAX_VALUE)
        state.currentTime = target

        onSeek(target)
    }

    fun seek(seconds: Float) {
        seekTo(state.currentTime + seconds)
    }

    fun showSubtitles() {
        if (state.subtitles.isEmpty()) return

        val currentIndex = if (state.selectedSubtitlesTrack == null) 0
        else state.subtitles.indexOfFirst { it.name == state.selectedSubtitlesTrack }.coerceAtLeast(0)

        val nextIndex = (currentIndex + 1) % state.subtitles.size
        showSubtitles(nextIndex)
    }

    fun showSubtitles(index: Int) {
        controls.hideSubtitles()

        onLoadSubtitleTrack(index)
    }

    fun restoreSubtitles() {
        if (state.subtitles.isEmpty() || state.selectedSubtitlesTrack == null) return

        val index = state.subtitles.indexOfFirst { it.name == state.selectedSubtitlesTrack }
        showSubtitles(index)
    }

    fun scale() {
        state.isZoomed = !state.isZoomed
    }

    fun playNext() {
        val nextUrl = state.fallbackUrls.firstOrNull() ?: return

        state.fallbackUrls = state.fallbackUrls.drop(1)

        loadVideo(
            url = nextUrl,
            fallback = state.fallbackUrls,
            qualityList = state.qualityList,
            trackIndex = state.audioTrackIndex,
            subtitles = state.subtitles,
            headers = state.headers
        )
    }


    protected fun updateBuffer(percent: Float) {
        state.bufferPercentage = percent.coerceIn(0f, 1f)
    }

    protected fun updateVolume(volume: Float) {
        state.volume = volume.coerceIn(0f, 1f)
    }

    protected fun updateSpeed(speed: Float) {
        state.speed = speed

        controls.setSpeedLabel(speed)
    }

    protected fun updateSubtitleTrack(title: String?) {
        state.selectedSubtitlesTrack = title
    }

    protected fun updateSubtitleTrack(index: Int) {
        state.selectedSubtitlesTrack = if (index == 0) null
        else state.subtitles.getOrNull(index)?.name
    }

    @Stable
    inner class VideoPlayerControls {
        internal val speedLabels = state.speedList.map { "${it}x" }

        val sliderInteractionSource = MutableInteractionSource()
        val pointerHoverIcon: PointerIcon
            get() = if (isControlsVisible) PointerIcon.Default else feature.pointerIcon

        val isControlsFocusable: Boolean
            get() = !feature.isTV || !isControlsVisible

        var isControlsVisible by mutableStateOf(false)
            private set

        var expandedEpisodes by mutableStateOf(false)
            private set
        var expandedQuality by mutableStateOf(false)
            private set
        var expandedSubtitles by mutableStateOf(false)
            private set

        var isVolumeDragging by mutableStateOf(false)
            private set
        var isSliderDragging by mutableStateOf(false)
            private set

        var speedLabel by mutableStateOf(speedLabels[2])
            private set
        var sliderValue by mutableFloatStateOf(0f)
            private set
        var interactionMillis by mutableLongStateOf(0L)
            private set

        fun showControls() {
            isControlsVisible = true
        }

        fun toggleControls() {
            isControlsVisible = !isControlsVisible

            if (!isControlsVisible) hideControls()
        }

        fun toggleQuality() {
            expandedQuality = !expandedQuality
        }

        fun hideQuality() {
            expandedQuality = false
        }

        fun toggleEpisodes() {
            expandedEpisodes = !expandedEpisodes
        }

        fun hideEpisodes() {
            expandedEpisodes = false
        }

        fun toggleSubtitles() {
            expandedSubtitles = !expandedSubtitles
        }

        fun hideSubtitles() {
            expandedSubtitles = false
        }

        fun showVolume() {
            isVolumeDragging = true
        }

        fun hideVolume() {
            isVolumeDragging = false
        }

        internal fun setSpeedLabel(newSpeed: Float) {
            speedLabel = speedLabels[state.speedList.indexOf(newSpeed)]
        }

        fun onSetSliderValue(percent: Float) {
            sliderValue = percent
        }

        fun onSliderActionFinished() {
            seekTo(sliderValue * state.totalTime)
        }

        fun refreshInteractionMillis() {
            interactionMillis = Clock.System.now().toEpochMilliseconds()
        }

        internal fun hideControls() {
            isControlsVisible = false
            expandedEpisodes = false
            expandedSubtitles = false
            expandedQuality = false
        }

        @Composable
        fun ControlsVisibilityListener() {
            LaunchedEffect(sliderInteractionSource) {
                val dragInteractions = mutableListOf<DragInteraction.Start>()

                sliderInteractionSource.interactions.collect {
                    when (it) {
                        is DragInteraction.Start -> dragInteractions.add(it)
                        is DragInteraction.Stop -> dragInteractions.remove(it.start)
                        is DragInteraction.Cancel -> dragInteractions.remove(it.start)
                    }

                    isSliderDragging = dragInteractions.isNotEmpty()
                }
            }

            LaunchedEffect(interactionMillis) {
                if (!isControlsVisible) {
                    showControls()
                }
            }

            LaunchedEffect(state.volume) {
                if (isVolumeDragging) {
                    delay(2.seconds)
                    isVolumeDragging = false
                }
            }

            LaunchedEffect(expandedQuality, expandedSubtitles) {
                if (expandedQuality || expandedSubtitles) {
                    refreshInteractionMillis()
                }
            }

            LaunchedEffect(isControlsVisible, state.isPlaying, isSliderDragging, isVolumeDragging, expandedEpisodes, interactionMillis) {
                if (isControlsVisible && state.isPlaying && !isSliderDragging && !isVolumeDragging && !expandedEpisodes) {
                    delay(feature.visibilityDelay.milliseconds)
                    hideControls()
                }
            }

            LaunchedEffect(state.currentTime, state.totalTime, isSliderDragging) {
                if (!isSliderDragging && state.totalTime > 0f) {
                    sliderValue = (state.currentTime / state.totalTime).coerceIn(0f, 1f)
                }
            }
        }
    }

    interface VideoPlayerFeature {
        val isTV: Boolean
        val pictureInPicture: VideoPlayerPictureInPicture?
        val showPlayPause: Boolean
        val visibilityDelay: Long
        val pointerIcon: PointerIcon
    }

    interface VideoPlayerPictureInPicture {
        fun enterPIP()
    }
}

expect class VideoPlayerController : VideoPlayer

@Composable
expect fun VideoPlayer(controller: VideoPlayerController, modifier: Modifier = Modifier)

@Composable
expect fun rememberVideoPlayerController(onEvent: (PlayerEvent) -> Unit): VideoPlayerController
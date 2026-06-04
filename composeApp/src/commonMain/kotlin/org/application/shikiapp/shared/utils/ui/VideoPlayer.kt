package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.di.PlatformContext
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@Stable
class VideoPlayerState {
    val speedList = listOf(0.5f, 1f, 1.5f, 2f)
    val controls = Controls()

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
    var speed by mutableFloatStateOf(speedList[1])
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
    var currentCues by mutableStateOf<List<CharSequence>>(emptyList())
        internal set

    // Масштабирование
    var isZoomed by mutableStateOf(false)
        internal set
    var isFullscreen by mutableStateOf(false) // desktop only
        internal set

    // Запасные ссылки и заголовки
    var headers by mutableStateOf<Map<String, String>>(emptyMap())
        internal set
    var fallbackUrls by mutableStateOf<List<String>>(emptyList())
        internal set
    var bufferPercentage by mutableFloatStateOf(0f)
        internal set

    // Триггеры и обработчик событий
    var tracksRevision by mutableIntStateOf(0)
        internal set

    internal var seekTrigger by mutableStateOf<Float?>(null)

    internal var eventListener: ((PlayerEvent) -> Unit)? = null

    fun onEvent(event: PlayerEvent) {
        when (event) {
            PlayerEvent.Play -> isPlaying = true
            PlayerEvent.Pause -> isPlaying = false

            PlayerEvent.MarkEpisodeWatched -> Unit

            is PlayerEvent.UpdateProgress -> Unit
            is PlayerEvent.Seek -> seekTo(event.seconds)

            is PlayerEvent.LoadVideo -> Unit
            is PlayerEvent.LoadFallback -> loadUrl(event.url)

            is PlayerEvent.OnAutoQualityChanged -> Unit
            is PlayerEvent.ChangeQuality -> {
                controls.hideQuality()
                currentQuality = event.quality
                seekTrigger = currentTime
            }

            is PlayerEvent.SelectEpisode -> {
                controls.hideEpisodes()

                url = null
                seekTrigger = null

                isLoading = true
                isPlaying = false
            }
        }

        eventListener?.invoke(event)
    }

    fun loadUrl(
        newUrl: String,
        fallback: List<String> = fallbackUrls,
        trackIndex: Int? = audioTrackIndex,
        subs: List<SubtitleTrack> = subtitles,
        headerMap: Map<String, String> = headers
    ) {
        url = newUrl
        fallbackUrls = fallback
        headers = headerMap
        audioTrackIndex = trackIndex
        subtitles = subs

        totalTime = 0f
        currentTime = 0f
        bufferPercentage = 0f

        isLoading = true
        isPlaying = true
        isVideoEnded = false

        selectedSubtitlesTrack = null
        currentCues = emptyList()

        currentQuality = null
        qualityList = emptyList()

        controls.hideControls()
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
    }

    fun pause() {
        isPlaying = false
    }

    fun seekTo(seconds: Float) {
        val target = seconds.coerceIn(0f, totalTime.takeIf { it > 0f } ?: Float.MAX_VALUE)
        seekTrigger = target
        currentTime = target
    }

    fun playNext() {
        fallbackUrls.firstOrNull()?.let { nextUrl ->
            fallbackUrls = fallbackUrls.drop(1)
            loadUrl(nextUrl)
        }
    }

    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
    }

    fun toggleSpeed() {
        val nextIndex = (speedList.indexOf(speed) + 1) % speedList.size
        speed = speedList[nextIndex]

        controls.setSpeedLabel(speed)
    }

    fun showSubtitles() {
        if (subtitles.isEmpty()) return

        val currentIndex = if (selectedSubtitlesTrack != null) {
            subtitles.indexOfFirst { it.name == selectedSubtitlesTrack }.coerceAtLeast(0)
        } else 0

        val nextIndex = (currentIndex + 1) % subtitles.size
        showSubtitles(nextIndex)
    }

    fun showSubtitles(index: Int) {
        controls.hideSubtitles()

        if (index == 0) {
            selectedSubtitlesTrack = null
            currentCues = emptyList()
        } else {
            selectedSubtitlesTrack = subtitles.getOrNull(index)?.name
        }
    }

    fun toggleFullscreen() {
        isFullscreen = !isFullscreen
    }

    fun toggleZoom() {
        isZoomed = !isZoomed
    }

    internal fun updateTime(current: Float, total: Float) {
        currentTime = current
        totalTime = total
    }

    internal fun updateBuffer(percent: Float) {
        bufferPercentage = percent.coerceIn(0f, 1f)
    }

    @Stable
    inner class Controls {
        internal val utils = VideoPlayerUtils()
        internal val speedLabels = speedList.map { "${it}x" }

        val sliderInteractionSource = MutableInteractionSource()
        val pointerHoverIcon: PointerIcon
            get() = if (isControlsVisible) PointerIcon.Default else utils.pointerIcon

        val isControlsFocusable: Boolean
            get() = if (utils.isTV) !isControlsVisible else true

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

        var speedLabel by mutableStateOf(speedLabels[1])
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

        fun setSpeedLabel(newSpeed: Float) {
            speedLabel = speedLabels[speedList.indexOf(newSpeed)]
        }

        fun onSetSliderValue(percent: Float) {
            sliderValue = percent
        }

        fun onSliderActionFinished() {
            val target = (sliderValue * totalTime).coerceIn(0f, totalTime.takeIf { it > 0f } ?: Float.MAX_VALUE)

            seekTrigger = target
            currentTime = target
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

            LaunchedEffect(volume) {
                if (isVolumeDragging) {
                    delay(2000.milliseconds)
                    isVolumeDragging = false
                }
            }

            LaunchedEffect(expandedQuality, expandedSubtitles) {
                if (expandedQuality || expandedSubtitles) {
                    refreshInteractionMillis()
                }
            }

            LaunchedEffect(isControlsVisible, isPlaying, isSliderDragging, isVolumeDragging, expandedEpisodes, interactionMillis) {
                if (isControlsVisible && isPlaying && !isSliderDragging && !isVolumeDragging && !expandedEpisodes) {
                    delay(utils.visibilityDelay.milliseconds)
                    hideControls()
                }
            }

            LaunchedEffect(currentTime, totalTime, isSliderDragging) {
                if (!isSliderDragging && totalTime > 0f) {
                    sliderValue = (currentTime / totalTime).coerceIn(0f, 1f)
                }
            }
        }

        @Composable
        fun AutoQualityListener() = LaunchedEffect(currentQuality) {
            currentQuality?.let {
                onEvent(PlayerEvent.OnAutoQualityChanged(it, qualityList))
            }
        }

        @Composable
        fun QualityListener(qualities: List<Int>) = LaunchedEffect(qualities) {
            qualityList = qualities
            currentQuality = qualities.maxOrNull()
        }
    }
}

@Composable
fun rememberVideoPlayerState(onEvent: (PlayerEvent) -> Unit): VideoPlayerState {
    val currentEvent by rememberUpdatedState(onEvent)

    return remember(::VideoPlayerState).apply {
        eventListener = currentEvent
    }
}

@Composable
expect fun VideoPlayer(state: VideoPlayerState, modifier: Modifier = Modifier)

expect class VideoPlayerUtils(context: PlatformContext) {
    constructor()

    val isTV: Boolean
    val showPlayPause: Boolean
    val visibilityDelay: Long
    val pointerIcon: PointerIcon
}
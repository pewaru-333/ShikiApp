package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.ui.SubtitleTrack

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
                controls.expandedQuality = false
                currentQuality = event.quality
                seekTrigger = currentTime
            }

            is PlayerEvent.SelectEpisode -> {
                controls.expandedEpisodes = false

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
        controls.expandedSubtitles = false

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
        val sliderInteractionSource = MutableInteractionSource()
        val speedLabels = speedList.map { "${it}x" }

        var isControlsVisible by mutableStateOf(false)
            private set

        var expandedEpisodes by mutableStateOf(false)
            internal set
        var expandedQuality by mutableStateOf(false)
            internal set
        var expandedSubtitles by mutableStateOf(false)
            internal set

        var isVolumeDragging by mutableStateOf(false)
            internal set
        var isSliderDragging by mutableStateOf(false)
            internal set

        var speedLabel by mutableStateOf(speedLabels[1])
            internal set
        var sliderValue by mutableFloatStateOf(0f)
            internal set

        fun showControls() {
            isControlsVisible = !isControlsVisible
        }

        fun toggleControls() {
            isControlsVisible = !isControlsVisible
        }

        fun toggleQuality() {
            expandedQuality = !expandedQuality
        }

        fun toggleSubtitles() {
            expandedSubtitles = !expandedSubtitles
        }

        fun setSpeedLabel(newSpeed: Float) {
            speedLabel = speedLabels[speedList.indexOf(newSpeed)]
        }

        fun setSliderValue(percent: Float) {
            sliderValue = percent
        }

        fun onSliderActionFinished() {
            val target = (sliderValue * totalTime).coerceIn(0f, totalTime.takeIf { it > 0f } ?: Float.MAX_VALUE)

            seekTrigger = target
            currentTime = target
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

            LaunchedEffect(isControlsVisible, isPlaying, isSliderDragging, isVolumeDragging, expandedEpisodes) {
                if (isControlsVisible && isPlaying && !isSliderDragging && !isVolumeDragging && !expandedEpisodes) {
                    delay(3000L)
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
package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.data.NewRate
import org.application.shikiapp.shared.models.data.UserRate
import org.application.shikiapp.shared.models.states.WatchState
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.models.ui.mappers.toVideoVoice
import org.application.shikiapp.shared.models.ui.mappers.toVideoVoices
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.parser.CollapsParser
import org.application.shikiapp.shared.network.parser.CvhParser
import org.application.shikiapp.shared.network.parser.KodikParser
import org.application.shikiapp.shared.network.parser.KodikResultItem
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.VideoSource
import org.application.shikiapp.shared.utils.navigation.Screen

class WatchViewModel(saved: SavedStateHandle) : ViewModel() {
    private val contentId = saved.toRoute<Screen.Watch>().contentId

    private val _state = MutableStateFlow(WatchState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), WatchState())

    private var countTimeJob: Job? = null
    private var watchedSeconds = 0
    private var markedAsWatched = false

    fun selectSource(type: VideoSource) {
        _state.update {
            it.copy(
                currentSource = it.sources.find { source -> source.type == type }
            )
        }
    }

    fun clearSource() {
        _state.update {
            it.copy(
                currentSource = null,
                currentVoice = null,
                currentEpisode = null,
                isWatching = false,
                videoUrl = null,
                audioTrackIndex = null
            )
        }
    }

    fun selectVoice(id: Int) = _state.update {
        it.copy(
            currentVoice = it.currentSource?.voices?.find { voice -> voice.id == id }
        )
    }

    fun clearVoice() = _state.update {
        it.copy(
            currentVoice = null,
            currentEpisode = null,
            isWatching = false,
            videoUrl = null
        )
    }

    fun onEvent(event: PlayerEvent) = when (event) {
        PlayerEvent.Play -> startWatchTimer()
        PlayerEvent.Pause -> countTimeJob?.cancel()
        is PlayerEvent.Seek -> Unit

        is PlayerEvent.UpdateProgress -> {
            if (event.totalTime > 0f && event.currentTime >= (event.totalTime - 2f)) {
                setEpisodeWatched()
            } else {
                Unit
            }
        }

        PlayerEvent.MarkEpisodeWatched -> setEpisodeWatched()

        is PlayerEvent.SelectEpisode -> selectEpisode(event.number)

        is PlayerEvent.ChangeQuality -> changeQuality(event.quality)
        is PlayerEvent.OnAutoQualityChanged -> {
            _state.update {
                it.copy(
                    currentQuality = event.quality,
                    qualityList = event.qualityList.ifEmpty(it::qualityList)
                )
            }
        }

        is PlayerEvent.LoadVideo -> loadVideo(event.episodeModel)
        is PlayerEvent.LoadFallback -> {
            _state.update {
                it.copy(videoUrl = event.url)
            }
        }
    }

    fun loadVideo(episode: EpisodeModel) {
        resetWatchTracking()

        _state.update {
            it.copy(
                currentEpisode = episode.number,
                isVideoLoading = true,
                isWatching = true
            )
        }

        viewModelScope.launch {
            try {
                val parser = when (currentState.currentSource?.type) {
                    VideoSource.KODIK -> KodikParser.getInstance()
                    VideoSource.COLLAPS -> CollapsParser.getInstance()
                    VideoSource.CVH -> CvhParser.getInstance()
                    null -> return@launch
                }

                val result = parser.getPlaylistLink(episode.link)
                val qualityList = result.qualityList.sortedDescending()
                val subtitles = if (result.subtitles.isEmpty()) emptyList()
                else {
                    val noSubtitles = SubtitleTrack(
                        name = "Выкл",
                        url = BLANK
                    )

                    listOf(noSubtitles) + result.subtitles
                }

                _state.update {
                    it.copy(
                        isVideoLoading = false,
                        qualityList = qualityList,
                        videoUrl = result.url,
                        fallbackUrls = result.fallbackUrls,
                        audioTrackIndex = episode.audioIndex,
                        subtitles = subtitles,
                        videoHeaders = result.headers,
                        currentQuality = qualityList.firstOrNull()
                    )
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(isVideoLoading = false)
                }
            }
        }
    }

    fun stopWatching() {
        countTimeJob?.cancel()
        _state.update {
            it.copy(
                isWatching = false,
                currentEpisode = null,
                videoUrl = null,
                audioTrackIndex = null,
            )
        }
    }

    private fun loadData() {
        if (currentState.sources.isNotEmpty() || currentState.isLoading) return

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val kodik = KodikParser.getInstance()
                val collaps = CollapsParser.getInstance()
                val cvh = CvhParser.getInstance()

                val kodikResult = kodik.searchById(contentId)
                if (kodikResult.isEmpty()) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                val firstItem = kodikResult.first()
                val imdbId = firstItem.imdbId
                val targetSeason = firstItem.lastSeason ?: 1

                val sources = supervisorScope {
                    val collapsDef = async {
                        imdbId.takeIf(String::isNotEmpty)?.let { id ->
                            collaps.searchById(id)
                                .toVideoVoices(targetSeason)
                                .sortedWith(voiceComparator)
                                .takeIf { it.isNotEmpty() }
                                ?.let { VideoSourceData(VideoSource.COLLAPS, it) }
                        }
                    }

                    val kodikDef = async {
                        kodikResult.map(KodikResultItem::toVideoVoice)
                            .sortedWith(voiceComparator)
                            .takeIf { it.isNotEmpty() }
                            ?.let { VideoSourceData(VideoSource.KODIK, it) }
                    }

                    val cvhDef = async {
                        cvh.searchById(contentId)
                            .toVideoVoices(targetSeason)
                            .sortedWith(voiceComparator)
                            .takeIf { it.isNotEmpty() }
                            ?.let { VideoSourceData(VideoSource.CVH, it) }
                    }

                    listOf(collapsDef, kodikDef, cvhDef).mapNotNull { deferred ->
                        runCatching { deferred.await() }.getOrNull()
                    }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        sources = replaceScreenshots(sources).sortedByDescending { source -> source.voices.size }
                    )
                }
            } catch (_: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun selectEpisode(number: Int) = currentState.currentVoice?.let { voice ->
        voice.episodes.find { it.number == number }?.let(::loadVideo)
    }

    private fun changeQuality(newQuality: Int) {
        val selectedSource = currentState.currentSource ?: return
        val selectedVoice = currentState.currentVoice ?: return
        val episodeNumber = currentState.currentEpisode ?: return
        val episode = selectedVoice.episodes.find { it.number == episodeNumber } ?: return

        _state.update { it.copy(currentQuality = newQuality) }

        if (selectedSource.type == VideoSource.COLLAPS) return

        _state.update { it.copy(isVideoLoading = false) }

        viewModelScope.launch {
            try {
                val parser = when (selectedSource.type) {
                    VideoSource.KODIK -> KodikParser.getInstance()
                    VideoSource.CVH -> CvhParser.getInstance()
                }

                val result = parser.getPlaylistLink(episode.link, newQuality)

                _state.update {
                    it.copy(
                        isVideoLoading = false,
                        videoUrl = result.url,
                        videoHeaders = result.headers,
                        qualityList = result.qualityList
                            .ifEmpty(it::qualityList)
                            .sortedDescending()
                    )
                }
            } catch (_: Exception) {
                stopWatching()
            }
        }
    }

    private fun startWatchTimer() {
        if (countTimeJob?.isActive == true) return

        countTimeJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                watchedSeconds++

                if (watchedSeconds >= 30) {
                    setEpisodeWatched()
                }
            }
        }
    }

    private fun resetWatchTracking() {
        countTimeJob?.cancel()
        watchedSeconds = 0
        markedAsWatched = false
    }

    private fun setEpisodeWatched() {
        if (markedAsWatched) return
        markedAsWatched = true

        if (Preferences.token != null && Preferences.episodeAutoAdd) {
            viewModelScope.launch {
                try {
                    val animeRate = Network.rates.getAnimeRate(contentId).firstOrNull()

                    if (animeRate == null) {
                        val newRate = Network.rates.createRate(
                            newRate = NewRate(
                                userId = Preferences.userId,
                                targetId = contentId.toLong(),
                                targetType = "Anime"
                            )
                        )

                        if (newRate.status == HttpStatusCode.Created) {
                            val newRate = newRate.body<UserRate>()
                            Network.rates.increment(newRate.id)
                        }
                    } else if (compareValues(animeRate.episodes, currentState.currentEpisode) < 0) {
                        Network.rates.increment(animeRate.id)
                    }
                } catch (_: Exception) {
                    markedAsWatched = false
                }
            }
        }
    }

    private fun replaceScreenshots(sources: List<VideoSourceData>): List<VideoSourceData> {
        val imageMap = mutableMapOf<Int, String>()

        sources.forEach { source ->
            source.voices.forEach { voice ->
                voice.episodes.forEach { episode ->
                    if (episode.screenshot != null && !imageMap.containsKey(episode.number)) {
                        imageMap[episode.number] = episode.screenshot
                    }
                }
            }
        }

        return if (imageMap.isEmpty()) sources else sources.map { source ->
            source.copy(
                voices = source.voices.map { voice ->
                    voice.copy(
                        episodes = voice.episodes.map { episode ->
                            if (episode.screenshot == null && imageMap.containsKey(episode.number)) {
                                episode.copy(screenshot = imageMap[episode.number])
                            } else {
                                episode
                            }
                        }
                    )
                }
            )
        }
    }

    private val voiceComparator = compareBy(VideoVoice::hasSubtitles)
        .thenByDescending(VideoVoice::episodesCount)
        .thenBy(VideoVoice::title)

    private val currentState: WatchState
        get() = _state.value
}
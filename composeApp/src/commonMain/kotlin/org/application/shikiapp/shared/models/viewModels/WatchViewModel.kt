package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.PlayerEvent
import org.application.shikiapp.shared.models.data.NewRate
import org.application.shikiapp.shared.models.data.UserRate
import org.application.shikiapp.shared.models.states.WatchState
import org.application.shikiapp.shared.models.states.isLib
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoSourceData
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.models.ui.mappers.toEpisodeVoices
import org.application.shikiapp.shared.models.ui.mappers.toVideoVoice
import org.application.shikiapp.shared.models.ui.mappers.toVideoVoices
import org.application.shikiapp.shared.network.client.AnimeLibAuth
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.parser.*
import org.application.shikiapp.shared.utils.enums.VideoSource
import org.application.shikiapp.shared.utils.navigation.Screen
import kotlin.time.Duration.Companion.seconds

class WatchViewModel(saved: SavedStateHandle) : ViewModel() {
    private val contentId = saved.toRoute<Screen.Watch>().contentId

    private val _state = MutableStateFlow(WatchState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), WatchState())

    private val _command = Channel<PlayerEvent.Command>()
    val command = _command.receiveAsFlow()

    private var videoJob: Job? = null
    private var watchJob: Job? = null
    private var markedAsWatched = false

    fun selectSource(type: VideoSource) = _state.update {
        it.copy(
            isEpisodesLoading = false,
            currentSource = it.sources.find { source -> source.type == type }
        )
    }

    fun clearSource() {
        videoJob?.cancel()
        cancelWatchTimer()

        _state.update {
            it.copy(
                currentSource = null,
                currentVoice = null,
                currentEpisode = null,
                isWatching = false,
                isVideoLoading = false,
                isVideoError = false,
                videoUrl = null,
                audioTrackIndex = null
            )
        }
    }

    fun selectVoice(id: String) {
        if (currentState.isLib) {
            loadAnimeLibVideo(id)
        } else {
            _state.update { state ->
                state.copy(currentVoice = state.currentSource?.voices?.find { it.id == id })
            }
        }
    }

    fun clearVoice() {
        videoJob?.cancel()
        cancelWatchTimer()

        _state.update {
            it.copy(
                currentVoice = null,
                currentEpisode = null,
                isWatching = false,
                isVideoLoading = false,
                isVideoError = false,
                videoUrl = null
            )
        }
    }

    fun onEvent(event: PlayerEvent) = when (event) {
        PlayerEvent.Play -> startWatchTimer()
        PlayerEvent.Pause -> cancelWatchTimer()
        PlayerEvent.Ended -> setEpisodeWatched()
        PlayerEvent.Error -> {
            videoJob?.cancel()
            cancelWatchTimer()
            _state.update { it.copy(isVideoLoading = false, isVideoError = true) }
        }

        is PlayerEvent.SelectEpisode -> selectEpisode(event.number)
        is PlayerEvent.ChangeQuality -> changeQuality(event.quality)
    }

    private fun loadVideo(episode: EpisodeModel) {
        val source = currentState.currentSource?.type ?: return

        videoJob?.cancel()
        cancelWatchTimer()
        markedAsWatched = false

        _state.update {
            it.copy(
                currentEpisode = episode.number,
                isVideoLoading = true,
                isVideoError = false,
                isWatching = true,
                videoUrl = null
            )
        }

        videoJob = viewModelScope.launch {
            try {
                val parser = when (source) {
                    VideoSource.KODIK -> KodikParser.instance
                    VideoSource.COLLAPS -> CollapsParser.instance
                    VideoSource.CVH -> CvhParser.instance
                    VideoSource.AKSOR -> AksorParser.instance
                    VideoSource.YANDEX -> YandexParser.instance
                    VideoSource.ANIMELIB -> AnimeLibParser.instance
                }

                val result = parser.getPlaylistLink(episode.link)
                val qualityList = result.qualityList.sortedDescending()
                val subtitles = if (result.subtitles.isEmpty()) emptyList()
                else buildList {
                    add(SubtitleTrack.off())
                    addAll(result.subtitles)
                }

                currentCoroutineContext().ensureActive()

                _command.send(
                    element = PlayerEvent.Command.LoadVideo(
                        episodeModel = episode.copy(
                            link = result.url,
                            fallback = result.fallbackUrls,
                            qualityList = qualityList,
                            subtitles = subtitles,
                            videoHeaders = result.headers
                        )
                    )
                )

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
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                _state.update { it.copy(isVideoLoading = false, isVideoError = true) }
            }
        }
    }

    fun retryVideo() {
        if (!currentState.isVideoError) return

        val quality = currentState.currentQuality
        if (currentState.videoUrl != null && quality != null) {
            changeQuality(quality)
            return
        }

        val voice = currentState.currentVoice ?: return
        if (currentState.isLib) {
            loadAnimeLibVideo(voice.id)
        } else {
            val episode = voice.episodes.find { it.number == currentState.currentEpisode } ?: return
            loadVideo(episode)
        }
    }

    fun stopWatching() {
        videoJob?.cancel()
        cancelWatchTimer()
        _state.update {
            it.copy(
                isWatching = false,
                isVideoLoading = false,
                isVideoError = false,
                currentEpisode = null,
                videoUrl = null,
                audioTrackIndex = null,
                subtitles = emptyList(),
                qualityList = emptyList(),
                fallbackUrls = emptyList(),
                currentVoice = if (currentState.currentSource?.type == VideoSource.ANIMELIB) null
                else currentState.currentVoice
            )
        }
    }

    private fun loadData() {
        if (currentState.sources.isNotEmpty() || currentState.isLoading) return

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val sources = supervisorScope {
                    val cvhDef = async { CvhParser.instance.searchById(contentId) }
                    val kodikDef = async { KodikParser.instance.searchById(contentId) }
                    val aksorDef = async {
                        AksorParser.instance.searchById(contentId)
                            .toVideoVoices()
                            .sortedWith(voiceComparator)
                            .takeIf { it.isNotEmpty() }
                            ?.let { VideoSourceData(VideoSource.AKSOR, it) }
                    }

                    val kodikResult = kodikDef.awaitOrNull().orEmpty()
                    val firstItem = kodikResult.firstOrNull()
                    val targetSeason = firstItem?.lastSeason ?: 1

                    val yandexDef = async {
                        val item = kodikResult.firstOrNull { (it.kinopoiskId?.toLongOrNull() ?: 0) > 0 }
                        val kinopoiskId = item?.kinopoiskId ?: return@async null
                        val season = item.lastSeason ?: 1
                        val seasonKey = season.toString()
                        val episodeCount = kodikResult.maxOfOrNull { result ->
                            if (result.kinopoiskId != kinopoiskId) return@maxOfOrNull 0

                            result.seasons?.get(seasonKey)?.episodes?.keys
                                ?.maxOfOrNull { it.toIntOrNull() ?: 0 }
                                ?: result.lastEpisode ?: result.episodesCount ?: 0
                        }?.takeIf { it > 0 }

                        YandexParser.instance
                            .searchById(kinopoiskId, season, episodeCount, item.type.endsWith("serial"))
                            .takeIf { it.voices.isNotEmpty() }
                    }

                    val collapsDef = async {
                        val imdbId = firstItem?.imdbId?.takeIf(String::isNotBlank) ?: return@async null

                        CollapsParser.instance.searchById(imdbId)
                            .toVideoVoices(targetSeason)
                            .sortedWith(voiceComparator)
                            .takeIf { it.isNotEmpty() }
                            ?.let { VideoSourceData(VideoSource.COLLAPS, it) }
                    }

                    val libDef = async {
                        val titleOrig = firstItem?.titleOrig.orEmpty()
                        val otherTitle = firstItem?.otherTitle.orEmpty()
                        if (titleOrig.isBlank() && otherTitle.isBlank()) return@async null

                        launch {
                            if (Preferences.libToken != null && AnimeLibAuth.validateToken() == false) {
                                AnimeLibAuth.refreshToken()
                            }
                        }

                        val anime = AnimeLibParser.instance.searchByTitle(contentId, "$titleOrig/$otherTitle") ?: return@async null

                        coroutineScope {
                            val teamsDef = async { AnimeLibParser.instance.getTeams(anime.slugUrl) }

                            AnimeLibParser.instance.searchById(anime.id.toString())
                                .toVideoVoices(teamsDef.await())
                                .takeIf { it.isNotEmpty() }
                                ?.let { VideoSourceData(VideoSource.ANIMELIB, it) }
                        }
                    }

                    val kodikVoices = kodikResult
                        .map(KodikResultItem::toVideoVoice)
                        .sortedWith(voiceComparator)

                    val cvhResult = cvhDef.awaitOrNull()
                    val cvhVoices = cvhResult
                        ?.toVideoVoices(targetSeason = if (firstItem == null) cvhResult.items.firstOrNull()?.season ?: 1 else targetSeason)
                        ?.sortedWith(voiceComparator)
                        .orEmpty()

                    listOfNotNull(
                        collapsDef.awaitOrNull(),
                        kodikVoices.takeIf { it.isNotEmpty() }?.let { VideoSourceData(VideoSource.KODIK, it) },
                        cvhVoices.takeIf { it.isNotEmpty() }?.let { VideoSourceData(VideoSource.CVH, it) },
                        aksorDef.awaitOrNull(),
                        libDef.awaitOrNull(),
                        yandexDef.awaitOrNull()
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        sources = fillEpisodeDetails(sources).sortedByDescending { source -> source.voices.size }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()

                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadAnimeLibVoices(episode: EpisodeModel) {
        videoJob?.cancel()

        _state.update {
            it.copy(
                isEpisodesLoading = true,
                selectedAnimelibEpisode = episode,
                episodeVoices = emptyList()
            )
        }

        videoJob = viewModelScope.launch {
            try {
                val detailResponse = AnimeLibParser.instance
                    .getEpisodeDetails(episode.link)
                    .toEpisodeVoices(episode.number)
                    .sortedWith(voiceComparator)

                _state.update {
                    it.copy(
                        isEpisodesLoading = false,
                        episodeVoices = detailResponse
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()

                _state.update {
                    it.copy(
                        isEpisodesLoading = false,
                        episodeVoices = null
                    )
                }
            }
        }
    }

    private fun loadAnimeLibVideo(teamId: String) {
        val episode = currentState.selectedAnimelibEpisode ?: return
        val voice = currentState.episodeVoices?.find { it.id == teamId } ?: return

        _state.update { it.copy(currentVoice = voice) }

        val videoEpisode = episode.copy(link = "${episode.link},$teamId")

        loadVideo(videoEpisode)
    }

    fun clearEpisodeVoices() {
        videoJob?.cancel()

        _state.update {
            it.copy(
                isEpisodesLoading = false,
                episodeVoices = null,
                selectedAnimelibEpisode = null
            )
        }
    }

    fun selectEpisode(episode: EpisodeModel) {
        if (currentState.isLib) loadAnimeLibVoices(episode)
        else loadVideo(episode)
    }

    private fun selectEpisode(number: Int) {
        val voice = currentState.currentVoice ?: return
        val episode = voice.episodes.find { it.number == number } ?: return

        selectEpisode(episode)
    }

    private fun changeQuality(newQuality: Int) {
        val selectedSource = currentState.currentSource ?: return
        val selectedVoice = currentState.currentVoice ?: return
        val episode = if (selectedSource.type == VideoSource.ANIMELIB) {
            currentState.selectedAnimelibEpisode
        } else {
            currentState.currentEpisode?.let { number ->
                selectedVoice.episodes.find { it.number == number }
            }
        } ?: return

        _state.update { it.copy(currentQuality = newQuality) }

        if (selectedSource.type == VideoSource.COLLAPS || selectedSource.type == VideoSource.YANDEX) return

        videoJob?.cancel()
        _state.update {
            it.copy(
                isVideoLoading = true,
                isVideoError = false
            )
        }

        videoJob = viewModelScope.launch {
            try {
                val parser = when (selectedSource.type) {
                    VideoSource.KODIK -> KodikParser.instance
                    VideoSource.CVH -> CvhParser.instance
                    VideoSource.AKSOR -> AksorParser.instance
                    VideoSource.YANDEX -> YandexParser.instance
                    VideoSource.ANIMELIB -> AnimeLibParser.instance
                }

                val parseUrl = if (selectedSource.type == VideoSource.ANIMELIB) {
                    "${episode.link},${selectedVoice.id}"
                } else {
                    episode.link
                }

                val result = parser.getPlaylistLink(parseUrl, newQuality)

                _command.send(PlayerEvent.Command.LoadQuality(result.url, result.headers))
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
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()

                _state.update {
                    it.copy(
                        isVideoLoading = false,
                        isVideoError = true
                    )
                }
            }
        }
    }

    fun toggleLogoutDialog() = _state.update { it.copy(showLogoutDialog = !it.showLogoutDialog) }

    fun logout() {
        videoJob?.cancel()
        cancelWatchTimer()
        AnimeLibAuth.signOut()

        _state.update {
            it.copy(
                showLogoutDialog = false,
                isWatching = false,
                isVideoLoading = false,
                isVideoError = false,
                currentSource = null,
                currentVoice = null,
                currentEpisode = null,
                episodeVoices = null,
                selectedAnimelibEpisode = null
            )
        }
    }

    private fun startWatchTimer() {
        if (markedAsWatched || watchJob?.isActive == true) return

        watchJob = viewModelScope.launch {
            delay(30.seconds)
            setEpisodeWatched()
            watchJob = null
        }
    }

    private fun cancelWatchTimer() {
        watchJob?.cancel()
        watchJob = null
    }

    private fun setEpisodeWatched() {
        if (Preferences.token != null && Preferences.episodeAutoAdd.value) {
            viewModelScope.launch {
                try {
                    val animeRate = Network.rates.getAnimeRate(contentId).firstOrNull()

                    if (animeRate == null) {
                        val newRate = Network.rates.createRate(
                            newRate = NewRate(
                                userId = Preferences.userId.value,
                                targetId = contentId.toLong(),
                                targetType = "Anime"
                            )
                        )

                        if (newRate.status == HttpStatusCode.Created) {
                            val newRate = newRate.body<UserRate>()
                            val response = Network.rates.increment(newRate.id)

                            markedAsWatched = response.status == HttpStatusCode.OK
                        }
                    } else if (compareValues(animeRate.episodes, currentState.currentEpisode) < 0) {
                        val response = Network.rates.increment(animeRate.id)

                        markedAsWatched = response.status == HttpStatusCode.OK
                    }
                } catch (_: Exception) {
                    markedAsWatched = false
                }
            }
        }
    }

    private fun fillEpisodeDetails(sources: List<VideoSourceData>): List<VideoSourceData> {
        val imageMap = HashMap<Int, String>()
        val titleMap = HashMap<Int, String>()

        sources.forEach { source ->
            source.voices.forEach { voice ->
                voice.episodes.forEach { episode ->
                    if (episode.screenshot != null && !imageMap.containsKey(episode.number)) {
                        imageMap[episode.number] = episode.screenshot
                    }

                    if (!episode.title.isNullOrBlank() && !titleMap.containsKey(episode.number)) {
                        titleMap[episode.number] = episode.title
                    }
                }
            }
        }

        return if (imageMap.isEmpty() && titleMap.isEmpty()) sources else sources.map { source ->
            source.copy(
                voices = source.voices.map { voice ->
                    voice.copy(
                        episodes = voice.episodes.map { episode ->
                            val screenshot = episode.screenshot ?: imageMap[episode.number]
                            val title = episode.title?.takeIf(String::isNotBlank) ?: titleMap[episode.number]

                            if (screenshot != episode.screenshot || title != episode.title) {
                                episode.copy(screenshot = screenshot, title = title)
                            } else {
                                episode
                            }
                        }
                    )
                }
            )
        }
    }

    private suspend fun <T> Deferred<T>.awaitOrNull(): T? = try {
        await()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        null
    }

    private val voiceComparator = compareBy(VideoVoice::hasSubtitles)
        .thenByDescending(VideoVoice::episodesCount)
        .thenBy(VideoVoice::title)

    private val currentState: WatchState
        get() = _state.value
}
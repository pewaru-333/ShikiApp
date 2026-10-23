package org.application.shikiapp.shared.utils.vlc

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import uk.co.caprica.vlcj.media.MediaSlaveType
import uk.co.caprica.vlcj.media.TrackType
import uk.co.caprica.vlcj.player.base.TextTrack
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import kotlin.time.Duration.Companion.seconds

internal class SubtitlePicker(
    private val player: EmbeddedMediaPlayer,
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    private val subtitles: List<SubtitleTrack>,
    initialIndex: Int,
    private val onSelectionChanged: (Int) -> Unit
) {
    private val indexList = Channel<Int>(Channel.CONFLATED)
    private val trackIds = mutableMapOf<String, String>()
    private var index = initialIndex
    private var started = false
    private var picked: Picked? = null

    fun select(index: Int) {
        if (index != 0 && index !in subtitles.indices) return
        this.index = index

        onSelectionChanged(index)
        indexList.trySend(index)

        if (started && index == 0) {
            scope.launch(dispatcher) {
                player.tracks().deselect(TrackType.TEXT)
            }
        }
    }

    fun start() {
        if (started) return
        started = true

        indexList.trySend(index)
        scope.launch {
            for (pickedIndex in indexList) {
                val subtitle = subtitles.getOrNull(pickedIndex)
                if (pickedIndex == 0 || subtitle == null) {
                    withContext(dispatcher) { player.tracks().deselect(TrackType.TEXT) }
                    continue
                }

                val trackId = load(subtitle.url)
                if (index != pickedIndex) continue
                if (trackId == null) {
                    index = 0
                    onSelectionChanged(0)

                    withContext(dispatcher) { player.tracks().deselect(TrackType.TEXT) }
                    continue
                }

                withContext(dispatcher) {
                    val track = player.tracks().track(trackId) ?: return@withContext
                    try {
                        player.tracks().selectTrack(track)
                    } finally {
                        track.release()
                    }
                }
            }
        }
    }

    fun onTrackAdded(trackId: String) {
        val pending = picked
        if (pending != null) {
            if (trackId !in pending.existingIds) {
                trackIds[pending.url] = trackId
                picked = null
                pending.result.complete(trackId)
            }
        } else if (started && trackId in trackIds.values) {
            indexList.trySend(index)
        }
    }

    private suspend fun load(url: String): String? {
        val cachedId = trackIds[url]
        if (cachedId != null) return cachedId

        val previous = picked
        if (previous != null) {
            withTimeoutOrNull(10.seconds) { previous.result.await() } ?: return null

            val loadedTrackId = trackIds[url]
            if (loadedTrackId != null) return loadedTrackId
        }

        val existingIds = withContext(dispatcher) {
            val tracks = player.tracks().textTracks() ?: return@withContext emptySet()
            try {
                tracks.tracks().mapTo(mutableSetOf(), TextTrack::trackId)
            } finally {
                tracks.release()
            }
        }

        val pending = Picked(url, existingIds)
        picked = pending

        val added = withContext(dispatcher) {
            player.media().addSlave(MediaSlaveType.SUBTITLE, url, false)
        }

        if (!added) {
            picked = null
            return null
        }

        return withTimeoutOrNull(10.seconds) { pending.result.await() }
    }

    private class Picked(val url: String, val existingIds: Set<String>) {
        val result = CompletableDeferred<String>()
    }
}

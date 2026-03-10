package org.application.shikiapp.shared.events

import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Score
import org.application.shikiapp.shared.utils.enums.WatchStatus

sealed interface RateEvent {
    data class SetRateId(val rateId: String) : RateEvent
    data class SetStatus(val status: WatchStatus, val type: LinkedType) : RateEvent
    data class SetScore(val score: Score) : RateEvent
    data class SetChapters(val chapters: String?) : RateEvent
    data class SetEpisodes(val episodes: String?) : RateEvent
    data class SetVolumes(val volumes: String?) : RateEvent
    data class SetRewatches(val rewatches: String?) : RateEvent
    data class SetText(val text: String?) : RateEvent
}
package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class WatchStatus(@StringRes val titleAnime: Int, @StringRes val titleManga: Int) {
    PLANNED(R.string.status_planned, R.string.status_planned),
    WATCHING(R.string.status_watching_anime, R.string.status_watching_manga),
    REWATCHING(R.string.status_rewatching_anime, R.string.status_rewatching_manga),
    COMPLETED(R.string.status_completed_anime, R.string.status_completed_manga),
    ON_HOLD(R.string.status_on_hold, R.string.status_on_hold),
    DROPPED(R.string.status_dropped, R.string.status_dropped)
}
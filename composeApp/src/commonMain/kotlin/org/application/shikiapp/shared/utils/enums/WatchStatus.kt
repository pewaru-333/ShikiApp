package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class WatchStatus(val titleAnime: StringResource, val titleManga: StringResource? = null) {
    PLANNED(Res.string.status_planned),
    WATCHING(Res.string.status_watching_anime, Res.string.status_watching_manga),
    REWATCHING(Res.string.status_rewatching_anime, Res.string.status_rewatching_manga),
    COMPLETED(Res.string.status_completed_anime, Res.string.status_completed_manga),
    ON_HOLD(Res.string.status_on_hold),
    DROPPED(Res.string.status_dropped)
}
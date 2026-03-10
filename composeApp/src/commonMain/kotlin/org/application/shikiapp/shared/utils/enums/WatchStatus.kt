package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.status_completed_anime
import shikiapp.composeapp.generated.resources.status_completed_manga
import shikiapp.composeapp.generated.resources.status_dropped
import shikiapp.composeapp.generated.resources.status_on_hold
import shikiapp.composeapp.generated.resources.status_planned
import shikiapp.composeapp.generated.resources.status_rewatching_anime
import shikiapp.composeapp.generated.resources.status_rewatching_manga
import shikiapp.composeapp.generated.resources.status_watching_anime
import shikiapp.composeapp.generated.resources.status_watching_manga

enum class WatchStatus(val titleAnime: StringResource, val titleManga: StringResource) {
    PLANNED(Res.string.status_planned, Res.string.status_planned),
    WATCHING(Res.string.status_watching_anime, Res.string.status_watching_manga),
    REWATCHING(Res.string.status_rewatching_anime, Res.string.status_rewatching_manga),
    COMPLETED(Res.string.status_completed_anime, Res.string.status_completed_manga),
    ON_HOLD(Res.string.status_on_hold, Res.string.status_on_hold),
    DROPPED(Res.string.status_dropped, Res.string.status_dropped)
}
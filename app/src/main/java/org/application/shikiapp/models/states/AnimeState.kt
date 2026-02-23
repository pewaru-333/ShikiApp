package org.application.shikiapp.models.states

data class AnimeState(
    val screenshot: Int = 0,
    val showAuthors: Boolean = false,
    val showCharacters: Boolean = false,
    val showComments: Boolean = false,
    val showFandubbers: Boolean = false,
    val showFansubbers: Boolean = false,
    val showLinks: Boolean = false,
    val showPoster: Boolean = false,
    val showRate: Boolean = false,
    val showRelated: Boolean = false,
    val showScreenshot: Boolean = false,
    val showScreenshots: Boolean = false,
    val showSheet: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false,
    val showVideo: Boolean = false
)

val AnimeState.showSheetContent: Boolean
    get() = showFansubbers || showFandubbers
package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Status(
    val types: List<LinkedType>,
    @StringRes val animeTitle: Int?,
    @StringRes val mangaTitle: Int
) {
    ANONS(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = R.string.status_anons,
        mangaTitle = R.string.status_anons
    ),
    ONGOING(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = R.string.status_ongoing_anime,
        mangaTitle = R.string.status_ongoing_manga
    ),
    RELEASED(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = R.string.status_released_anime,
        mangaTitle = R.string.status_released_manga
    ),
    PAUSED(
        types = listOf(LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = null,
        mangaTitle = R.string.status_paused_manga
    ),
    DISCONTINUED(
        types = listOf(LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = null,
        mangaTitle = R.string.status_paused_discontinued
    )
}
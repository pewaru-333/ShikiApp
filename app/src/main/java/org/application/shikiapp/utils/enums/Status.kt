package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
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
    );

    @StringRes
    fun getTitle(kind: Kind) = if (kind.linkedType == LinkedType.ANIME) animeTitle ?: R.string.text_unknown
    else mangaTitle
}

val Status.backgroundColor: Color
    get() = when (this) {
        Status.ANONS -> Color(0xFFF28F3B)
        Status.ONGOING -> Color(0xFFC6E0FF)
        Status.RELEASED -> Color(0xFF0CF574)
        Status.PAUSED -> Color(0xFFAEB7AF)
        Status.DISCONTINUED -> Color(0xFFEF2D56)
    }

val Status.textColor: Color
    get() = when (this) {
        Status.ANONS, Status.RELEASED, Status.PAUSED -> Color.Black
        Status.ONGOING -> Color(0xFF0E2239)
        Status.DISCONTINUED -> Color(0xFFEFEFEF)
    }
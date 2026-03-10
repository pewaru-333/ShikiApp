package org.application.shikiapp.shared.utils.enums

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.status_anons
import shikiapp.composeapp.generated.resources.status_ongoing_anime
import shikiapp.composeapp.generated.resources.status_ongoing_manga
import shikiapp.composeapp.generated.resources.status_paused_discontinued
import shikiapp.composeapp.generated.resources.status_paused_manga
import shikiapp.composeapp.generated.resources.status_released_anime
import shikiapp.composeapp.generated.resources.status_released_manga
import shikiapp.composeapp.generated.resources.text_unknown

enum class Status(
    val types: List<LinkedType>,
    val animeTitle: StringResource?,
    val mangaTitle: StringResource
) {
    ANONS(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = Res.string.status_anons,
        mangaTitle = Res.string.status_anons
    ),
    ONGOING(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = Res.string.status_ongoing_anime,
        mangaTitle = Res.string.status_ongoing_manga
    ),
    RELEASED(
        types = listOf(LinkedType.ANIME, LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = Res.string.status_released_anime,
        mangaTitle = Res.string.status_released_manga
    ),
    PAUSED(
        types = listOf(LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = null,
        mangaTitle = Res.string.status_paused_manga
    ),
    DISCONTINUED(
        types = listOf(LinkedType.MANGA, LinkedType.RANOBE),
        animeTitle = null,
        mangaTitle = Res.string.status_paused_discontinued
    );

    fun getTitle(kind: Kind) =
        if (kind.linkedType == LinkedType.ANIME) animeTitle ?: Res.string.text_unknown
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
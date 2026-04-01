package org.application.shikiapp.shared.utils.enums

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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

data class StatusColors(
    val background: Color,
    val text: Color
)

val Status.colors: StatusColors
    @Composable
    get() {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

        return when (this) {
            Status.ANONS -> if (isDark) {
                StatusColors(background = Color(0xFF5C330A), text = Color(0xFFFFDCC1))
            } else {
                StatusColors(background = Color(0xFFF28F3B), text = Color.Black)
            }

            Status.ONGOING -> if (isDark) {
                StatusColors(background = Color(0xFF0A2B4C), text = Color(0xFFC6E0FF))
            } else {
                StatusColors(background = Color(0xFFC6E0FF), text = Color(0xFF0E2239))
            }

            Status.RELEASED -> if (isDark) {
                StatusColors(background = Color(0xFF00421F), text = Color(0xFF67DF90))
            } else {
                StatusColors(background = Color(0xFF0CF574), text = Color.Black)
            }

            Status.PAUSED -> if (isDark) {
                StatusColors(background = Color(0xFF3C403D), text = Color(0xFFDFE4DF))
            } else {
                StatusColors(background = Color(0xFFAEB7AF), text = Color.Black)
            }

            Status.DISCONTINUED -> if (isDark) {
                StatusColors(background = Color(0xFF7A001D), text = Color(0xFFFFD9E2))
            } else {
                StatusColors(background = Color(0xFFEF2D56), text = Color.White)
            }
        }
    }
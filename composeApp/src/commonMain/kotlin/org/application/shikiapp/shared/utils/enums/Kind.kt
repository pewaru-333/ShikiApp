package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.kind_anime_cm
import shikiapp.composeapp.generated.resources.kind_anime_movie
import shikiapp.composeapp.generated.resources.kind_anime_music
import shikiapp.composeapp.generated.resources.kind_anime_ona
import shikiapp.composeapp.generated.resources.kind_anime_ova
import shikiapp.composeapp.generated.resources.kind_anime_pv
import shikiapp.composeapp.generated.resources.kind_anime_special
import shikiapp.composeapp.generated.resources.kind_anime_tv
import shikiapp.composeapp.generated.resources.kind_anime_tv_13
import shikiapp.composeapp.generated.resources.kind_anime_tv_24
import shikiapp.composeapp.generated.resources.kind_anime_tv_48
import shikiapp.composeapp.generated.resources.kind_anime_tv_special
import shikiapp.composeapp.generated.resources.kind_manga_doujin
import shikiapp.composeapp.generated.resources.kind_manga_manga
import shikiapp.composeapp.generated.resources.kind_manga_manhua
import shikiapp.composeapp.generated.resources.kind_manga_manhwa
import shikiapp.composeapp.generated.resources.kind_manga_one_shot
import shikiapp.composeapp.generated.resources.kind_ranobe_light_novel
import shikiapp.composeapp.generated.resources.kind_ranobe_novel

enum class Kind(val linkedType: LinkedType, val title: StringResource) {
    TV(LinkedType.ANIME, Res.string.kind_anime_tv),
    TV_13(LinkedType.ANIME, Res.string.kind_anime_tv_13),
    TV_24(LinkedType.ANIME, Res.string.kind_anime_tv_24),
    TV_48(LinkedType.ANIME, Res.string.kind_anime_tv_48),
    MOVIE(LinkedType.ANIME, Res.string.kind_anime_movie),
    OVA(LinkedType.ANIME, Res.string.kind_anime_ova),
    ONA(LinkedType.ANIME, Res.string.kind_anime_ona),
    SPECIAL(LinkedType.ANIME, Res.string.kind_anime_special),
    TV_SPECIAL(LinkedType.ANIME, Res.string.kind_anime_tv_special),
    MUSIC(LinkedType.ANIME, Res.string.kind_anime_music),
    PV(LinkedType.ANIME, Res.string.kind_anime_pv),
    CM(LinkedType.ANIME, Res.string.kind_anime_cm),

    MANGA(LinkedType.MANGA, Res.string.kind_manga_manga),
    MANHWA(LinkedType.MANGA, Res.string.kind_manga_manhwa),
    MANHUA(LinkedType.MANGA, Res.string.kind_manga_manhua),
    ONE_SHOT(LinkedType.MANGA, Res.string.kind_manga_one_shot),
    DOUJIN(LinkedType.MANGA, Res.string.kind_manga_doujin),

    LIGHT_NOVEL(LinkedType.RANOBE, Res.string.kind_ranobe_light_novel),
    NOVEL(LinkedType.RANOBE, Res.string.kind_ranobe_novel);
}
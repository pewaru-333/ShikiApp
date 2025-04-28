package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Kind(val linkedType: LinkedType, @StringRes val title: Int) {
    TV(LinkedType.ANIME, R.string.kind_anime_tv),
    TV_13(LinkedType.ANIME, R.string.kind_anime_tv_13),
    TV_24(LinkedType.ANIME, R.string.kind_anime_tv_24),
    TV_48(LinkedType.ANIME, R.string.kind_anime_tv_48),
    MOVIE(LinkedType.ANIME, R.string.kind_anime_movie),
    OVA(LinkedType.ANIME, R.string.kind_anime_ova),
    ONA(LinkedType.ANIME, R.string.kind_anime_ona),
    SPECIAL(LinkedType.ANIME, R.string.kind_anime_special),
    TV_SPECIAL(LinkedType.ANIME, R.string.kind_anime_tv_special),
    MUSIC(LinkedType.ANIME, R.string.kind_anime_music),
    PV(LinkedType.ANIME, R.string.kind_anime_pv),
    CM(LinkedType.ANIME, R.string.kind_anime_cm),

    MANGA(LinkedType.MANGA, R.string.kind_manga_manga),
    MANHWA(LinkedType.MANGA, R.string.kind_manga_manhwa),
    MANHUA(LinkedType.MANGA, R.string.kind_manga_manhua),
    ONE_SHOT(LinkedType.MANGA, R.string.kind_manga_one_shot),
    DOUJIN(LinkedType.MANGA, R.string.kind_manga_doujin),

    LIGHT_NOVEL(LinkedType.RANOBE, R.string.kind_ranobe_light_novel),
    NOVEL(LinkedType.RANOBE, R.string.kind_ranobe_novel);
}
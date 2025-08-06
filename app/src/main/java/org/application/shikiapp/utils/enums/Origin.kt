package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Origin(@StringRes val title: Int) {
    ORIGINAL(R.string.origin_original),
    MANGA(R.string.origin_manga),
    WEB_MANGA(R.string.origin_web_manga),
    FOUR_KOMA_MANGA(R.string.origin_four_koma_manga),
    NOVEL(R.string.origin_novel),
    WEB_NOVEL(R.string.origin_web_novel),
    VISUAL_NOVEL(R.string.origin_visual_novel),
    LIGHT_NOVEL(R.string.origin_light_novel),
    GAME(R.string.origin_game),
    CARD_GAME(R.string.origin_card_game),
    MUSIC(R.string.origin_music),
    RADIO(R.string.origin_radio),
    BOOK(R.string.origin_book),
    PICTURE_BOOK(R.string.origin_picture_book),
    MIXED_MEDIA(R.string.origin_mixed_media),
    OTHER(R.string.origin_other),
    UNKNOWN(R.string.origin_unknown)
}
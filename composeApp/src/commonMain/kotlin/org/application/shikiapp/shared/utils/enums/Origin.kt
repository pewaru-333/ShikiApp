package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class Origin(val title: StringResource) {
    ORIGINAL(Res.string.origin_original),
    MANGA(Res.string.origin_manga),
    WEB_MANGA(Res.string.origin_web_manga),
    FOUR_KOMA_MANGA(Res.string.origin_four_koma_manga),
    NOVEL(Res.string.origin_novel),
    WEB_NOVEL(Res.string.origin_web_novel),
    VISUAL_NOVEL(Res.string.origin_visual_novel),
    LIGHT_NOVEL(Res.string.origin_light_novel),
    GAME(Res.string.origin_game),
    CARD_GAME(Res.string.origin_card_game),
    MUSIC(Res.string.origin_music),
    RADIO(Res.string.origin_radio),
    BOOK(Res.string.origin_book),
    PICTURE_BOOK(Res.string.origin_picture_book),
    MIXED_MEDIA(Res.string.origin_mixed_media),
    OTHER(Res.string.origin_other),
    UNKNOWN(Res.string.origin_unknown)
}
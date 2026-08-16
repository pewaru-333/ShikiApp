package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class Rating(val title: StringResource) {
    NONE(Res.string.rating_none),
    G(Res.string.rating_g),
    PG(Res.string.rating_pg),
    PG_13(Res.string.rating_pg_13),
    R(Res.string.rating_r),
    R_PLUS(Res.string.rating_r_plus),
    RX(Res.string.rating_rx)
}
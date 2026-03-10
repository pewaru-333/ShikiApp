package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.rating_g
import shikiapp.composeapp.generated.resources.rating_none
import shikiapp.composeapp.generated.resources.rating_pg
import shikiapp.composeapp.generated.resources.rating_pg_13
import shikiapp.composeapp.generated.resources.rating_r
import shikiapp.composeapp.generated.resources.rating_r_plus
import shikiapp.composeapp.generated.resources.rating_rx

enum class Rating(val title: StringResource) {
    NONE(Res.string.rating_none),
    G(Res.string.rating_g),
    PG(Res.string.rating_pg),
    PG_13(Res.string.rating_pg_13),
    R(Res.string.rating_r),
    R_PLUS(Res.string.rating_r_plus),
    RX(Res.string.rating_rx)
}
package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes

enum class Rating(@StringRes val title: Int) {
    NONE(org.application.shikiapp.R.string.rating_none),
    G(org.application.shikiapp.R.string.rating_g),
    PG(org.application.shikiapp.R.string.rating_pg),
    PG_13(org.application.shikiapp.R.string.rating_pg_13),
    R(org.application.shikiapp.R.string.rating_r),
    R_PLUS(org.application.shikiapp.R.string.rating_r_plus),
    RX(org.application.shikiapp.R.string.rating_rx)
}
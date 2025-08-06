package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class OrderRates(@StringRes val title: Int, @StringRes val titleManga: Int? = null) {
    TITLE(R.string.order_name),
    SCORE(R.string.order_ranked),
    EPISODES(R.string.order_episodes, R.string.order_chapters),
    KIND(R.string.order_kind),
    CREATED_AT(R.string.order_created_at),
    UPDATE_AT(R.string.order_updated_at)
}
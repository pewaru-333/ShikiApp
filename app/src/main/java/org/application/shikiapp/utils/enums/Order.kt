package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class Order(@StringRes val title: Int) {
    ID(R.string.order_id),
    ID_DESC(R.string.order_id_desc),
    RANKED(R.string.order_ranked),
    KIND(R.string.order_kind),
    POPULARITY(R.string.order_popularity),
    NAME(R.string.order_name),
    AIRED_ON(R.string.order_aired_on),
    EPISODES(R.string.order_episodes),
    STATUS(R.string.order_status),
    RANDOM(R.string.order_random),
    CREATED_AT(R.string.order_created_at),
    CREATED_AT_DESC(R.string.order_created_at_desc)
}
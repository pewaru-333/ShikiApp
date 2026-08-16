package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class Order(val title: StringResource) {
    ID(Res.string.order_id),
    ID_DESC(Res.string.order_id_desc),
    RANKED(Res.string.order_ranked),
    KIND(Res.string.order_kind),
    POPULARITY(Res.string.order_popularity),
    NAME(Res.string.order_name),
    AIRED_ON(Res.string.order_aired_on),
    EPISODES(Res.string.order_episodes),
    STATUS(Res.string.order_status),
    RANDOM(Res.string.order_random),
    CREATED_AT(Res.string.order_created_at_asc),
    CREATED_AT_DESC(Res.string.order_created_at_desc),
    UPDATED_AT(Res.string.order_updated_at_asc),
    UPDATED_AT_DESC(Res.string.order_updated_at_desc)
}
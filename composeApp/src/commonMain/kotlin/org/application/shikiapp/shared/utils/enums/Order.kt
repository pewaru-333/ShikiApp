package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.order_aired_on
import shikiapp.composeapp.generated.resources.order_created_at_asc
import shikiapp.composeapp.generated.resources.order_created_at_desc
import shikiapp.composeapp.generated.resources.order_episodes
import shikiapp.composeapp.generated.resources.order_id
import shikiapp.composeapp.generated.resources.order_id_desc
import shikiapp.composeapp.generated.resources.order_kind
import shikiapp.composeapp.generated.resources.order_name
import shikiapp.composeapp.generated.resources.order_popularity
import shikiapp.composeapp.generated.resources.order_random
import shikiapp.composeapp.generated.resources.order_ranked
import shikiapp.composeapp.generated.resources.order_status
import shikiapp.composeapp.generated.resources.order_updated_at_asc
import shikiapp.composeapp.generated.resources.order_updated_at_desc

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
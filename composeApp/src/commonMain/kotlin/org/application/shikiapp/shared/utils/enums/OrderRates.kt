package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class OrderRates(val title: StringResource, val titleManga: StringResource? = null) {
    TITLE(Res.string.order_name),
    SCORE(Res.string.order_ranked),
    EPISODES(Res.string.order_episodes, Res.string.order_chapters),
    KIND(Res.string.order_kind),
    CREATED_AT(Res.string.order_created_at),
    UPDATED_AT(Res.string.order_updated_at)
}
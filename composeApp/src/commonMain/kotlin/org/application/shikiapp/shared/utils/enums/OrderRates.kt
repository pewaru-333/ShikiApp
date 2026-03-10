package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.order_chapters
import shikiapp.composeapp.generated.resources.order_created_at
import shikiapp.composeapp.generated.resources.order_episodes
import shikiapp.composeapp.generated.resources.order_kind
import shikiapp.composeapp.generated.resources.order_name
import shikiapp.composeapp.generated.resources.order_ranked
import shikiapp.composeapp.generated.resources.order_updated_at

enum class OrderRates(val title: StringResource, val titleManga: StringResource? = null) {
    TITLE(Res.string.order_name),
    SCORE(Res.string.order_ranked),
    EPISODES(Res.string.order_episodes, Res.string.order_chapters),
    KIND(Res.string.order_kind),
    CREATED_AT(Res.string.order_created_at),
    UPDATED_AT(Res.string.order_updated_at)
}
package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.DrawableResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_arrow_down
import shikiapp.composeapp.generated.resources.vector_arrow_up

enum class OrderDirection(val icon: DrawableResource) {
    ASCENDING(Res.drawable.vector_arrow_up),
    DESCENDING(Res.drawable.vector_arrow_down)
}
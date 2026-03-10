package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.preference_list_view_column
import shikiapp.composeapp.generated.resources.preference_list_view_grid

enum class ListView(val title: StringResource) {
    COLUMN(Res.string.preference_list_view_column),
    GRID(Res.string.preference_list_view_grid)
}
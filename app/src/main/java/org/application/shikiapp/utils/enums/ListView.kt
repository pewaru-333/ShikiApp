package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class ListView(@StringRes val title: Int) {
    COLUMN(R.string.preference_list_view_column),
    GRID(R.string.preference_list_view_grid)
}
package org.application.shikiapp.models.ui.list

import androidx.annotation.StringRes
import org.application.shikiapp.utils.ResourceText

data class Content(
    val id: String,
    val title: String,
    @StringRes val kind: Int,
    val season: ResourceText,
    val poster: String
)

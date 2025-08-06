package org.application.shikiapp.models.ui.list

import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.Status

open class Content(
    id: String,
    title: String,
    poster: String,
    val kind: Kind,
    val season: ResourceText,
    val score: String?,
    val status: Status
) : BasicContent(id, title, poster)

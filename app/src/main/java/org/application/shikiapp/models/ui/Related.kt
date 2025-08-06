package org.application.shikiapp.models.ui

import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Status

class Related(
    id: String,
    title: String,
    poster: String,
    kind: Kind,
    season: ResourceText,
    score: String?,
    status: Status,
    val relationText: String,
    val linkedType: LinkedType
) : Content(id, title, poster, kind, season, score, status)

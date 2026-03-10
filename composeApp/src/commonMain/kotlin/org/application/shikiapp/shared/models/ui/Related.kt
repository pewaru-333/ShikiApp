package org.application.shikiapp.shared.models.ui

import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Status

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

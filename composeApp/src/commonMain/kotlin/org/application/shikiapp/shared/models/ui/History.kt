package org.application.shikiapp.shared.models.ui

import androidx.compose.ui.text.AnnotatedString
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.utils.enums.Kind

class History(
    id: String,
    title: String,
    poster: String,
    val contentId: String?,
    val description: AnnotatedString,
    val date: String,
    val kind: Kind
) : BasicContent(id, title, poster)
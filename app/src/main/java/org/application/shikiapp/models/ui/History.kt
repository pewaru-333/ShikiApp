package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import org.application.shikiapp.utils.enums.Kind

data class History(
    val id: String,
    val contentId: String?,
    val title: String,
    val description: AnnotatedString,
    val date: String,
    val kind: Kind,
    val image: String?
)
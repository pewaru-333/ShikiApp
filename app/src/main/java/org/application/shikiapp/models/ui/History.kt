package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString

data class History(
    val id: String,
    val title: String,
    val description: AnnotatedString,
    val date: String,
    val kind: String?,
    val image: String?
)
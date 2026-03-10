package org.application.shikiapp.shared.models.ui

import io.ktor.http.Url

data class ExternalLink(
    val url: Url,
    val title: String,
    val kind: String
)

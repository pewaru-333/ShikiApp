package org.application.shikiapp.models.ui

import android.net.Uri

data class ExternalLink(
    val url: Uri,
    val title: String,
    val kind: String
)

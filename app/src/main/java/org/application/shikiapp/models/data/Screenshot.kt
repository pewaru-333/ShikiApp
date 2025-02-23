package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable

@Serializable
data class Screenshot(
    val original: String,
    val preview: String
)
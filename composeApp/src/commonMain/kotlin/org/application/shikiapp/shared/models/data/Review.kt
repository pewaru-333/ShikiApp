package org.application.shikiapp.shared.models.data

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val content: String,
    val postloader: String? = null
)

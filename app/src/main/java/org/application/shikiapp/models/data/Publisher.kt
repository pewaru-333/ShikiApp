package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable

@Serializable
data class Publisher(
    val id: Long,
    val name: String
)

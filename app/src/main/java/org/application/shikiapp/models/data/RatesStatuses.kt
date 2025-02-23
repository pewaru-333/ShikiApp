package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable

@Serializable
data class RatesStatuses(
    val name: String,
    val value: Int
)
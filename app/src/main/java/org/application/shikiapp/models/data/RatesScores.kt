package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable

@Serializable
data class RatesScores(
    val name: String,
    val value: Int
)
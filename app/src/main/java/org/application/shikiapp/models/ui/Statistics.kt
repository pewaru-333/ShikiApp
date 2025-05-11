package org.application.shikiapp.models.ui

import org.application.shikiapp.utils.ResourceText

typealias Score = String
typealias Label = ResourceText

data class Statistics(
    val sum: Int,
    val scores: Map<Label, Score>
)
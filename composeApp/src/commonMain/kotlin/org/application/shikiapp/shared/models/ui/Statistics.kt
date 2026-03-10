package org.application.shikiapp.shared.models.ui

import org.application.shikiapp.shared.utils.ResourceText

typealias Score = String
typealias Label = ResourceText

data class Statistics(
    val sum: Int,
    val scores: Map<Label, Score>
)
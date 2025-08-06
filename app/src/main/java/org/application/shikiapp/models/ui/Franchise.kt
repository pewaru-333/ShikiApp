package org.application.shikiapp.models.ui

import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.RelationKind

data class Franchise(
    val id: String,
    val title: String,
    val poster: String,
    val year: ResourceText,
    val kind: Kind,
    val linkedType: LinkedType,
    val relationType: RelationKind
)

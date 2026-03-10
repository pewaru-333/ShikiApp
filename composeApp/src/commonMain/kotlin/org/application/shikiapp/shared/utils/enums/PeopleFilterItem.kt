package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_mangaka
import shikiapp.composeapp.generated.resources.text_producer
import shikiapp.composeapp.generated.resources.text_seyu

enum class PeopleFilterItem(val title: StringResource) {
    SEYU(Res.string.text_seyu),
    PRODUCER(Res.string.text_producer),
    MANGAKA(Res.string.text_mangaka)
}
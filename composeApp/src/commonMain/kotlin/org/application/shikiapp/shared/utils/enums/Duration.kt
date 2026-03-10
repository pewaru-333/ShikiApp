package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.duration_D
import shikiapp.composeapp.generated.resources.duration_F
import shikiapp.composeapp.generated.resources.duration_S

enum class Duration(val title: StringResource) {
    S(Res.string.duration_S),
    D(Res.string.duration_D),
    F(Res.string.duration_F)
}
package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class VideoSource(val title: StringResource) {
    KODIK(Res.string.video_source_kodik),
    COLLAPS(Res.string.video_source_collapse),
    CVH(Res.string.video_source_videohub),
    ANIMELIB(Res.string.video_source_animelib)
}
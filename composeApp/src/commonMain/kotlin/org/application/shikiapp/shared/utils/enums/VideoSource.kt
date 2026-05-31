package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.video_source_animelib
import shikiapp.composeapp.generated.resources.video_source_collapse
import shikiapp.composeapp.generated.resources.video_source_kodik
import shikiapp.composeapp.generated.resources.video_source_videohub

enum class VideoSource(val title: StringResource) {
    KODIK(Res.string.video_source_kodik),
    COLLAPS(Res.string.video_source_collapse),
    CVH(Res.string.video_source_videohub),
    ANIMELIB(Res.string.video_source_animelib)
}
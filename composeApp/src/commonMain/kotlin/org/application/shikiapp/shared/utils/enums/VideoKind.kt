package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_video
import shikiapp.composeapp.generated.resources.text_video_character_trailers
import shikiapp.composeapp.generated.resources.text_video_episodes_preview

enum class VideoKind(val title: StringResource, val kinds: List<String>) {
    VIDEO(Res.string.text_video, listOf("pv", "ed", "op", "op_ed_clip", "other")),
    CHARACTER(Res.string.text_video_character_trailers, listOf("character_trailer")),
    EPISODE(Res.string.text_video_episodes_preview, listOf("episode_preview"))
}
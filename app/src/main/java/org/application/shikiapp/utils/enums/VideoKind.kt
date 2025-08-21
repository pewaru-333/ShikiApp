package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class VideoKind(@StringRes val title: Int, val kinds: List<String>) {
    VIDEO(R.string.text_video, listOf("pv", "ed", "op", "op_ed_clip", "other")),
    CHARACTER(R.string.text_video_character_trailers, listOf("character_trailer")),
    EPISODE(R.string.text_video_episodes_preview, listOf("episode_preview"))
}
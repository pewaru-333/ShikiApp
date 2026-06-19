package org.application.shikiapp.shared.utils.enums

import org.application.shikiapp.shared.models.ui.Video
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_video
import shikiapp.composeapp.generated.resources.text_video_character_trailers
import shikiapp.composeapp.generated.resources.text_video_episodes_preview

enum class VideoKind(val title: StringResource, val kinds: Set<String>) {
    VIDEO(Res.string.text_video, setOf("pv", "ed", "op", "op_ed_clip", "other")),
    CHARACTER(Res.string.text_video_character_trailers, setOf("character_trailer")),
    EPISODE(Res.string.text_video_episodes_preview, setOf("episode_preview"));

    companion object {
        private val kindMap = entries.let { enumEntries ->
            buildMap {
                for (entry in enumEntries) {
                    for (kind in entry.kinds) {
                        put(kind, entry)
                    }
                }
            }
        }

        fun group(videos: Iterable<Video>): Map<VideoKind, List<Video>> {
            val destination = LinkedHashMap<VideoKind, MutableList<Video>>()
            for (video in videos) {
                val key = kindMap[video.kind]
                if (key != null) {
                    destination.getOrPut(key) { ArrayList() }.add(video)
                }
            }

            return destination
        }
    }
}
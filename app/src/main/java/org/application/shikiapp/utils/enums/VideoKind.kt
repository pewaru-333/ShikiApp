package org.application.shikiapp.utils.enums

enum class VideoKind(val title: String, val kinds: List<String>) {
    VIDEO("Видео", listOf("pv", "ed", "op", "op_ed_clip", "other")),
    CHARACTER("Трейлеры персонажей", listOf("character_trailer")),
    EPISODE("Превью эпизодов", listOf("episode_preview"))
}
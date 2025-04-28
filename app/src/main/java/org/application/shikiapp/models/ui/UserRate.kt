package org.application.shikiapp.models.ui

import androidx.annotation.StringRes

data class UserRate(
    val id: Long,
    val contentId: String,
    val title: String,
    val poster: String,
    @StringRes val kind: Int,
    val score: Int,
    val scoreString: String,
    val status: String,
    val text: String?,
    val episodes: Int,
    val fullEpisodes: String,
    val chapters: Int,
    val fullChapters: String,
    val volumes: Int,
    val rewatches: Int
)

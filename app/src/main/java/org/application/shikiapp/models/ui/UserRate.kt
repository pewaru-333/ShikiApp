package org.application.shikiapp.models.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import java.time.OffsetDateTime

@Immutable
data class UserRate(
    val chapters: Int,
    val contentId: String,
    val createdAt: OffsetDateTime,
    val episodes: Int,
    val episodesSorting: Int,
    val fullChapters: String,
    val fullEpisodes: String,
    val id: Long,
    @StringRes val kind: Int,
    val poster: String,
    val rewatches: Int,
    val score: Int,
    val scoreString: String,
    val status: String,
    val text: String?,
    val title: String,
    val updatedAt: OffsetDateTime,
    val volumes: Int
)

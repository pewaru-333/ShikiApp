package org.application.shikiapp.shared.models.ui

import androidx.compose.runtime.Immutable
import org.application.shikiapp.shared.utils.enums.Kind
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Instant

@Immutable
data class UserRate(
    val chapters: Int,
    val contentId: String,
    val createdAt: Instant,
    val episodes: Int,
    val episodesSorting: Int,
    val fullChapters: String,
    val fullEpisodes: String,
    val id: Long,
    val kindEnum: Kind,
    val kindString: StringResource,
    val poster: String,
    val rewatches: Int,
    val rewatchExists: Boolean,
    val score: Int,
    val scoreString: String,
    val status: String,
    val text: String?,
    val title: String,
    val updatedAt: Instant,
    val volumes: Int
)

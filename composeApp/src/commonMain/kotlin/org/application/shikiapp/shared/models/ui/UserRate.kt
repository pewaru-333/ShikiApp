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
    val episodesAired: Int? = null,
    val episodesSorting: Int,
    val fullChapters: String,
    val fullEpisodes: String,
    val id: Long,
    val kindEnum: Kind,
    val poster: String,
    val rewatches: Int,
    val score: Int,
    val status: String,
    val text: String?,
    val title: String,
    val updatedAt: Instant,
    val volumes: Int
) {
    val kindString: StringResource
        get() = kindEnum.title

    val rewatchExists: Boolean
        get() = rewatches > 0

    val scoreString: String
        get() = if (score == 0) "-" else score.toString()
}
package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.BaseRate
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.safeEquals
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter
import kotlin.time.Instant


fun BaseRate.mapper(): UserRate {
    val content = anime ?: manga
    val contentId = content?.id ?: 0L

    return UserRate(
        chapters = chapters ?: 0,
        contentId = contentId.toString(),
        createdAt = Instant.parse(createdAt),
        episodes = episodes ?: 0,
        episodesAired = if (Status.ONGOING.safeEquals(anime?.status)) anime?.episodesAired?.takeIf { it >= 0 } else null,
        episodesSorting = anime?.episodes ?: manga?.chapters ?: 0,
        fullChapters = if (manga == null) BLANK else Formatter.getFullEpisodes(manga.chapters, manga.status),
        fullEpisodes = if (anime == null) BLANK else Formatter.getFullEpisodes(anime.episodes, anime.status),
        id = id,
        kindEnum = Enum.safeValueOf<Kind>(content?.kind),
        poster = if (anime == null) manga?.image?.original.orEmpty()
        else Formatter.replaceMissingAnimePoster(anime.image.original, anime.id),
        rewatches = rewatches,
        score = score,
        status = status,
        text = text,
        title = content?.russian?.takeIf(String::isNotBlank) ?: content?.name.orEmpty(),
        updatedAt = Instant.parse(updatedAt),
        volumes = volumes ?: 0
    )
}
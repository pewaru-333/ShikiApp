package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.BaseRate
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter
import java.time.OffsetDateTime


fun BaseRate.mapper(): UserRate {
    val contentId = anime?.id ?: manga?.id ?: 0
    val title = anime?.name ?: manga?.name
    val ruTitle = anime?.russian ?: manga?.russian
    val kindEnum = Enum.safeValueOf<Kind>(anime?.kind ?: manga?.kind)

    return UserRate(
        chapters = chapters ?: 0,
        contentId = contentId.toString(),
        createdAt = OffsetDateTime.parse(createdAt),
        episodes = episodes ?: 0,
        episodesSorting = anime?.episodes ?: manga?.chapters ?: 0,
        fullChapters = Formatter.getFullEpisodes(manga?.chapters, manga?.status),
        fullEpisodes = Formatter.getFullEpisodes(anime?.episodes, anime?.status),
        id = id,
        kindEnum = kindEnum,
        kindString = kindEnum.title,
        poster = if (manga?.image?.original != null) manga.image.original
        else Formatter.replaceMissingAnimePoster(anime?.image?.original, contentId),
        rewatches = rewatches,
        score = score,
        scoreString = score.let { if (it != 0) it else '-' }.toString(),
        status = status,
        text = text,
        title = if (ruTitle.isNullOrEmpty()) title.orEmpty() else ruTitle,
        updatedAt = OffsetDateTime.parse(updatedAt),
        volumes = volumes ?: 0
    )
}
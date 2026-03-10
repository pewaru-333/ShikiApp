package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.BaseRate
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter
import java.time.OffsetDateTime


fun BaseRate.mapper() = UserRate(
    chapters = chapters ?: 0,
    contentId = (anime?.id ?: manga?.id ?: 0).toString(),
    createdAt = OffsetDateTime.parse(createdAt),
    episodes = episodes ?: 0,
    episodesSorting = anime?.episodes ?: manga?.chapters ?: 0,
    fullChapters = Formatter.getFullEpisodes(manga?.chapters, manga?.status),
    fullEpisodes = Formatter.getFullEpisodes(anime?.episodes, anime?.status),
    id = id,
    kind = Enum.safeValueOf<Kind>(anime?.kind ?: manga?.kind).title,
    poster = anime?.image?.original ?: manga?.image?.original.orEmpty(),
    rewatches = rewatches,
    score = score,
    scoreString = score.let { if (it != 0) it else '-' }.toString(),
    status = status,
    text = text,
    title = when {
        anime != null -> anime.russian.orEmpty().ifEmpty(anime::name)
        manga != null -> manga.russian.orEmpty().ifEmpty(manga::name)
        else -> BLANK
    },
    updatedAt = OffsetDateTime.parse(updatedAt),
    volumes = volumes ?: 0
)
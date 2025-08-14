package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.BaseRate
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getFull
import java.time.OffsetDateTime


fun BaseRate.mapper() = UserRate(
    id = id,
    contentId = (anime?.id ?: manga?.id ?: 0).toString(),
    title = anime?.russian ?: manga?.russian ?: anime?.name ?: manga?.name.orEmpty(),
    poster = anime?.image?.original ?: manga?.image?.original.orEmpty(),
    kind = Enum.safeValueOf<Kind>(anime?.kind ?: manga?.kind).title,
    score = score,
    scoreString = score.let { if (it != 0) it else '-' }.toString(),
    status = status,
    text = text,
    episodes = episodes ?: 0,
    episodesSorting = anime?.episodes ?: manga?.chapters ?: 0,
    fullEpisodes = getFull(anime?.episodes, anime?.status),
    volumes = volumes ?: 0,
    chapters = chapters ?: 0,
    rewatches = rewatches,
    fullChapters = getFull(manga?.chapters, manga?.status),
    createdAt = OffsetDateTime.parse(createdAt),
    updatedAt = OffsetDateTime.parse(updatedAt)
)
package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.BaseRate
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getFull

fun BaseRate.mapper() = org.application.shikiapp.models.ui.UserRate(
    id = id,
    contentId = (anime?.id ?: manga?.id!!).toString(),
    title = anime?.russian ?: anime?.name ?: manga?.russian ?: manga?.name!!,
    poster = anime?.image?.original ?: manga?.image?.original!!,
    kind = Enum.safeValueOf<Kind>(anime?.kind ?: manga?.kind).title,
    score = score,
    scoreString = score.let { if (it != 0) it else '-' }.toString(),
    status = status,
    text = text,
    episodes = episodes ?: 0,
    fullEpisodes = getFull(anime?.episodes, anime?.status),
    volumes = volumes ?: 0,
    chapters = chapters ?: 0,
    rewatches = rewatches,
    fullChapters = getFull(manga?.chapters, manga?.status),
)
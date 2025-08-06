package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.generated.UserRatesQuery
import org.application.shikiapp.generated.type.UserRateTargetTypeEnum
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getFull
import java.time.OffsetDateTime

fun UserRatesQuery.Data.UserRate.mapper(type: UserRateTargetTypeEnum) = UserRate(
    id = id.toLong(),
    contentId = anime?.id ?: manga?.id!!,
    title = type.let {
        if (it == UserRateTargetTypeEnum.Anime) anime?.russian ?: anime?.name!!
        else manga?.russian ?: manga?.name!!
    },
    poster = type.let {
        if (it == UserRateTargetTypeEnum.Anime) anime?.poster?.originalUrl!!
        else manga?.poster?.originalUrl!!
    },
    kind = Enum.safeValueOf<Kind>(
        type.let {
            if (it == UserRateTargetTypeEnum.Anime) anime?.kind?.rawValue
            else manga?.kind?.rawValue
        }
    ).title,
    score = score,
    scoreString = score.let { if (it != 0) it else '-' }.toString(),
    status = status.rawValue,
    text = text,
    episodes = episodes,
    fullEpisodes = getFull(anime?.episodes, anime?.status?.rawValue),
    volumes = volumes,
    chapters = chapters,
    rewatches = rewatches,
    fullChapters = getFull(manga?.chapters, manga?.status?.rawValue),
    createdAt = OffsetDateTime.parse(createdAt.toString()),
    updatedAt = OffsetDateTime.parse(updatedAt.toString())
)
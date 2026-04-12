package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.History
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter

fun History.mapper(): org.application.shikiapp.shared.models.ui.History {
    val targetId = target?.id
    val poster = target?.image?.original
    val kind = Enum.safeValueOf<Kind>(target?.kind)

    return org.application.shikiapp.shared.models.ui.History(
        id = id.toString(),
        contentId = targetId?.toString(),
        title = target?.russian?.takeIf(String::isNotEmpty) ?: target?.name.orEmpty(),
        description = fromHtml(description),
        date = Formatter.convertDate(createdAt),
        kind = kind,
        poster = if (kind.linkedType != LinkedType.ANIME) poster.orEmpty()
        else Formatter.replaceMissingAnimePoster(poster, targetId)
    )
}
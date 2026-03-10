package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.data.History
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter

fun History.mapper() = org.application.shikiapp.shared.models.ui.History(
    id = id.toString(),
    contentId = target?.id?.toString(),
    title = target?.let { it.russian.orEmpty().ifEmpty(it::name) }.orEmpty(),
    description = fromHtml(description),
    date = Formatter.convertDate(createdAt),
    kind = Enum.safeValueOf<Kind>(target?.kind),
    image = target?.image?.original
)
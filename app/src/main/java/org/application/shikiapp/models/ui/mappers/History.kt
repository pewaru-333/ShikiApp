package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.History
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.fromHtml

fun History.mapper() = org.application.shikiapp.models.ui.History(
    id = id.toString(),
    contentId = target?.id?.toString(),
    title = target?.let { it.russian.orEmpty().ifEmpty(it::name) }.orEmpty(),
    description = fromHtml(description),
    date = convertDate(createdAt),
    kind = Enum.safeValueOf<Kind>(target?.kind),
    image = target?.image?.original
)
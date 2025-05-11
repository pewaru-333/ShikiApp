package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.History
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.convertDate

fun History.mapper() = org.application.shikiapp.models.ui.History(
    id = id.toString(),
    contentId = target?.id.toString(),
    title = target?.let { it.russian.orEmpty().ifEmpty(it::name) } ?: BLANK,
    description = fromHtml(description),
    date = convertDate(createdAt),
    kind = target?.kind,
    image = target?.image?.original
)
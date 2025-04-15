package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.History
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getImage

fun History.mapper() = org.application.shikiapp.models.ui.History(
    id = id.toString(),
    title = target?.let { it.russian.orEmpty().ifEmpty(it::name) } ?: BLANK,
    description = fromHtml(description),
    date = convertDate(createdAt),
    kind = target?.kind,
    image = getImage(target?.image?.original)
)
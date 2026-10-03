package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.generated.common.MangaListQuery
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter

fun MangaListQuery.Data.Manga.mapper() = Content(
    id = id,
    kind = Enum.safeValueOf<Kind>(kind?.rawValue),
    poster = poster?.mainUrl.orEmpty(),
    score = score?.let(Formatter::convertScore),
    season = Formatter.getSeason(airedOn?.date, kind?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue),
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name
)
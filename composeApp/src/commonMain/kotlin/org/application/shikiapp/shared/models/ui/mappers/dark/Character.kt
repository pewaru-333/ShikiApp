package org.application.shikiapp.shared.models.ui.mappers.dark

import org.application.shikiapp.generated.darkshiki.fragment.CharacterRole
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter

fun org.application.shikiapp.shared.models.data.BasicContent.toContent() = Content(
    id = id.toString(),
    kind = Enum.safeValueOf<Kind>(kind),
    poster = image.original,
    score = score?.let(Formatter::convertScore),
    season = Formatter.getSeason(airedOn, kind),
    status = Enum.safeValueOf<Status>(status),
    title = russian.orEmpty().ifEmpty(::name)
)

fun CharacterRole.toBasicContent() = BasicContent(
    id = character.id,
    title = character.russian.orEmpty().ifEmpty(character::name),
    poster = character.poster?.originalUrl.orEmpty()
)
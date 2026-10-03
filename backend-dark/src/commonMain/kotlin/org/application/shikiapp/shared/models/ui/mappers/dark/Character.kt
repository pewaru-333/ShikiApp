package org.application.shikiapp.shared.models.ui.mappers.dark

import org.application.shikiapp.generated.darkshiki.fragment.CharacterRole
import org.application.shikiapp.shared.models.ui.list.BasicContent

fun CharacterRole.toBasicContent() = BasicContent(
    id = character.id,
    title = character.russian.takeUnless(String?::isNullOrEmpty) ?: character.name,
    poster = character.poster?.originalUrl.orEmpty()
)
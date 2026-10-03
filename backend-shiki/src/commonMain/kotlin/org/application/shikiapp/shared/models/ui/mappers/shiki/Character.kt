package org.application.shikiapp.shared.models.ui.mappers.shiki

import org.application.shikiapp.generated.shikiapp.fragment.CharacterRole
import org.application.shikiapp.shared.models.ui.list.BasicContent

fun CharacterRole.toBasicContent() = BasicContent(
    id = character.id,
    title = character.russian.takeUnless(String?::isNullOrEmpty) ?: character.name,
    poster = character.poster?.originalUrl.orEmpty()
)


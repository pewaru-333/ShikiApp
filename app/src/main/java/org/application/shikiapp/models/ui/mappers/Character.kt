package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.CharacterListQuery
import org.application.CharacterQuery
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getSeason

fun Character.mapper(image: CharacterQuery.Data.Character, comments: Flow<PagingData<Comment>>) =
    org.application.shikiapp.models.ui.Character(
        id = id.toString(),
        russian = russian,
        japanese = japanese,
        altName = altName,
        description = fromHtml(descriptionHTML),
        poster = image.poster?.originalUrl ?: BLANK,
        favoured = favoured,
        anime = animes.map {
            Content(
                id = it.id.toString(),
                title = it.russian.orEmpty().ifEmpty(it::name),
                kind = Enum.safeValueOf<Kind>(it.kind).title,
                season = getSeason(it.releasedOn, it.kind),
                poster = getImage(it.image.original)
            )
        },
        manga = mangas.map {
            Content(
                id = it.id.toString(),
                title = it.russian.orEmpty().ifEmpty(it::name),
                kind = Enum.safeValueOf<Kind>(it.kind).title,
                season = getSeason(it.releasedOn, it.kind),
                poster = getImage(it.image.original)
            )
        },
        seyu = seyu.map {
            Content(
                id = it.id.toString(),
                title = it.russian.orEmpty().ifEmpty(it::name),
                kind = R.string.blank,
                season = ResourceText.StringResource(R.string.blank),
                poster = getImage(it.image.original)
            )
        },
        comments = comments
    )

fun CharacterListQuery.Data.Character.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = R.string.blank,
    season = ResourceText.StringResource(R.string.blank),
    poster = poster?.mainUrl ?: BLANK
)
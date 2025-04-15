package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.CharacterListQuery
import org.application.CharacterQuery
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK

fun Character.mapper(image: CharacterQuery.Data.Character, comments: Flow<PagingData<Comment>>) =
    org.application.shikiapp.models.ui.Character(
        id = id.toString(),
        russian = russian,
        japanese = japanese,
        altName = altName,
        description = fromHtml(descriptionHTML).toString(),
        poster = image.poster?.originalUrl,
        favoured = favoured,
        anime = animes,
        manga = mangas,
        seyu = seyu,
        comments = comments
    )

fun CharacterListQuery.Data.Character.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = BLANK,
    season = BLANK,
    poster = poster?.mainUrl
)
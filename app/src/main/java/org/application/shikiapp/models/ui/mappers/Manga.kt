package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.MangaListQuery
import org.application.MangaQuery
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_ranobe
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.ui.PersonMain
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Similar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.STATUSES_M
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getPublisher
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getStatusM
import org.application.type.MangaKindEnum.light_novel
import org.application.type.MangaKindEnum.novel

fun MangaQuery.Data.Manga.mapper(
    similar: List<MangaBasic>,
    links: List<ExternalLink>,
    comments: Flow<PagingData<Comment>>,
    favoured: Boolean,
) = Manga(
    id = id,
    title = russian?.let { "$it / $name" } ?: name,
    poster = poster?.originalUrl,
    kindEnum = kind,
    kindString = getKind(kind?.rawValue),
    kindTitle = if (kind in listOf(light_novel, novel)) text_ranobe else text_manga,
    volumes = volumes.toString(),
    chapters = chapters.toString(),
    showChapters = status?.rawValue != STATUSES_M.keys.elementAt(1),
    status = getStatusM(status?.rawValue),
    publisher = getPublisher(publishers),
    score = score.toString(),
    description = fromHtml(descriptionHtml).toString(),
    favoured = favoured,
    genres = genres,
    similar = similar.map {
        Similar(
            id = it.id.toString(),
            title = it.russian ?: it.name,
            poster = getImage(it.image.original)
        )
    },
    related = related?.map {
        Related(
            animeId = it.anime?.id,
            mangaId = it.manga?.id,
            title = it.anime?.russian ?: it.anime?.name ?: it.manga?.russian ?: it.manga?.name
            ?: BLANK,
            poster = it.anime?.poster?.originalUrl ?: it.manga?.poster?.originalUrl,
            relationText = it.relationText
        )
    } ?: emptyList(),
    links = links,
    charactersAll = characterRoles?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl
        )
    } ?: emptyList(),
    characterMain = characterRoles?.filter { it.rolesRu.contains("Main") }?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl
        )
    } ?: emptyList(),
    personAll = personRoles?.map {
        PersonMain(
            id = it.person.id.toLong(),
            name = it.person.russian ?: it.person.name,
            poster = it.person.poster?.originalUrl
        )
    } ?: emptyList(),
    personMain = personRoles?.filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }?.map {
        PersonMain(
            id = it.person.id.toLong(),
            name = it.person.russian ?: it.person.name,
            poster = it.person.poster?.originalUrl
        )
    } ?: emptyList(),
    scoresStats = scoresStats,
    statusesStats = statusesStats,
    comments = comments,
    userRate = userRate
)

fun MangaListQuery.Data.Manga.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = getKind(kind?.rawValue),
    season = getSeason(airedOn, kind?.rawValue),
    poster = poster?.mainUrl
)
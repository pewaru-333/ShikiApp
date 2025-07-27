package org.application.shikiapp.models.ui.mappers

import androidx.core.net.toUri
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_ranobe
import org.application.shikiapp.generated.MangaListQuery
import org.application.shikiapp.generated.MangaQuery
import org.application.shikiapp.generated.type.MangaKindEnum.light_novel
import org.application.shikiapp.generated.type.MangaKindEnum.novel
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.ui.PersonMain
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Similar
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.ResourceText.StringResource
import org.application.shikiapp.utils.convertScore
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getWatchStatus

fun MangaQuery.Data.Manga.mapper(
    similar: List<MangaBasic>,
    links: List<ExternalLink>,
    comments: Flow<PagingData<Comment>>,
    favoured: Boolean,
) = Manga(
    id = id,
    title = russian?.let { "$it / $name" } ?: name,
    poster = poster?.originalUrl ?: BLANK,
    kindEnum = kind,
    kindString = Enum.safeValueOf<Kind>(kind?.rawValue).title,
    kindTitle = if (kind in listOf(light_novel, novel)) text_ranobe else text_manga,
    volumes = volumes.toString(),
    chapters = chapters.toString(),
    showChapters = !Status.ONGOING.safeEquals(status?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue).mangaTitle,
    publisher = publishers.map(MangaQuery.Data.Manga.Publisher::name).firstOrNull() ?: "Неизвестно",
    score = score.let(::convertScore),
    description = fromHtml(descriptionHtml),
    favoured = favoured,
    genres = genres,
    similar = similar.map {
        Similar(
            id = it.id.toString(),
            title = it.russian ?: it.name,
            poster = it.image.original
        )
    },
    related = related?.map {
        Related(
            animeId = it.anime?.id,
            mangaId = it.manga?.id,
            title = it.anime?.russian ?: it.anime?.name ?: it.manga?.russian ?: it.manga?.name
            ?: BLANK,
            poster = it.anime?.poster?.originalUrl ?: it.manga?.poster?.originalUrl ?: BLANK,
            relationText = it.relationText
        )
    } ?: emptyList(),
    links = links.filter { it.kind in EXTERNAL_LINK_KINDS.keys }.map {
        org.application.shikiapp.models.ui.ExternalLink(
            url = it.url.toUri(),
            title = EXTERNAL_LINK_KINDS[it.kind].orEmpty(),
            kind = it.kind
        )
    },
    charactersAll = characterRoles?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl ?: BLANK
        )
    } ?: emptyList(),
    characterMain = characterRoles?.filter { it.rolesRu.contains("Main") }?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl ?: BLANK
        )
    } ?: emptyList(),
    personAll = personRoles?.map {
        PersonMain(
            id = it.person.id.toLong(),
            name = it.person.russian ?: it.person.name,
            poster = it.person.poster?.originalUrl ?: BLANK
        )
    } ?: emptyList(),
    personMain = personRoles?.filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }?.map {
        PersonMain(
            id = it.person.id.toLong(),
            name = it.person.russian ?: it.person.name,
            poster = it.person.poster?.originalUrl ?: BLANK
        )
    } ?: emptyList(),
    comments = comments,
    stats = Pair(
        first = scoresStats?.let { scores ->
            Statistics(
                sum = scores.sumOf(MangaQuery.Data.Manga.ScoresStat::count),
                scores = scores.associate {
                    ResourceText.StaticString(it.score.toString()) to it.count.toString()
                }
            )
        },
        second = statusesStats?.let { statuses ->
            Statistics(
                sum = statuses.sumOf(MangaQuery.Data.Manga.StatusesStat::count),
                scores = statuses.associate {
                    StringResource(getWatchStatus(it.status.rawValue, LinkedType.MANGA)) to it.count.toString()
                }
            )
        }
    ),
    userRate = userRate
)

fun MangaListQuery.Data.Manga.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = Enum.safeValueOf<Kind>(kind?.rawValue).title,
    season = getSeason(airedOn, kind?.rawValue),
    poster = poster?.mainUrl ?: BLANK,
    score = score?.let(::convertScore)
)

fun PagingData<MangaBasic>.toContent() = map {
    Content(
        id = it.id.toString(),
        title = it.russian.orEmpty().ifEmpty(it::name),
        kind = Enum.safeValueOf<Kind>(it.kind).title,
        season = ResourceText.StaticString(BLANK),
        poster = it.image.original,
        score = it.score?.let(::convertScore)
    )
}
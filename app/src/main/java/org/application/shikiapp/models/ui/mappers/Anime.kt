package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.AnimeAiringQuery
import org.application.AnimeListQuery
import org.application.AnimeQuery
import org.application.AnimeStatsQuery
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Topic
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.PersonMain
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Similar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.ui.list.ShortContent
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.STATUSES_A
import org.application.shikiapp.utils.getFull
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getRating
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getStatusA
import org.application.shikiapp.utils.getStudio

fun AnimeQuery.Data.Anime.mapper(
    similar: List<AnimeBasic>,
    links: List<ExternalLink>,
    stats: AnimeStatsQuery.Data.Anime,
    comments: Flow<PagingData<Comment>>,
    favoured: Boolean,
) = Anime(
    id = id,
    title = russian?.let { "$it / $name" } ?: name,
    poster = poster?.originalUrl,
    description = fromHtml(descriptionHtml).toString(),
    status = getStatusA(status?.rawValue),
    kind = getKind(kind?.rawValue),
    episodes = when (status?.rawValue) {
        STATUSES_A.keys.elementAt(1) -> "$episodesAired / ${getFull(episodes)}"
        STATUSES_A.keys.elementAt(2) -> "$episodes / $episodes"
        else -> "$episodesAired / $episodes"
    },
    studio = getStudio(studios),
    score = score.toString(),
    rating = getRating(rating?.rawValue),
    genres = genres,
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
    charactersMain = characterRoles?.filter { it.rolesRu.contains("Main") }?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl
        )
    } ?: emptyList(),
    charactersAll = characterRoles?.map {
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
    favoured = favoured,
    similar = similar.map {
        Similar(
            id = it.id.toString(),
            title = it.russian ?: it.name,
            poster = getImage(it.image.original)
        )
    },
    links = links,
    comments = comments,
    screenshots = screenshots.map(AnimeQuery.Data.Anime.Screenshot::originalUrl),
    videos = videos,
    stats = stats,
    userRate = userRate
)

fun AnimeListQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = getKind(kind?.rawValue),
    season = getSeason(season, kind?.rawValue),
    poster = poster?.mainUrl
)

fun AnimeAiringQuery.Data.Anime.mapper() = ShortContent(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.originalUrl
)

fun PagingData<Topic>.toAnimeContent() = map {
    Content(
        id = it.linked.id.toString(),
        title = it.linked.russian.orEmpty().ifEmpty(it.linked::name),
        kind = getKind(it.linked.kind),
        season = getSeason(it.linked.releasedOn, it.linked.kind),
        poster = getImage(it.linked.image.original)
    )
}
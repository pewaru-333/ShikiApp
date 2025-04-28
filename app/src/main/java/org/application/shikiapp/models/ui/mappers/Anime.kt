package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.AnimeAiringQuery
import org.application.AnimeListQuery
import org.application.AnimeQuery
import org.application.AnimeStatsQuery
import org.application.shikiapp.R
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
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.Rating
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.getFull
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getSeason
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
    poster = poster?.originalUrl ?: BLANK,
    description = fromHtml(descriptionHtml),
    status = Enum.safeValueOf<Status>(status?.rawValue).animeTitle ?: R.string.text_unknown,
    kind = Enum.safeValueOf<Kind>(kind?.rawValue).title,
    episodes = when (status?.rawValue?.uppercase()) {
        Status.ONGOING.name -> "$episodesAired / ${getFull(episodes)}"
        Status.RELEASED.name -> "$episodes / $episodes"
        else -> "$episodesAired / $episodes"
    },
    studio = getStudio(studios),
    score = score.toString(),
    rating = Enum.safeValueOf<Rating>(rating?.rawValue).title,
    genres = genres,
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
    charactersMain = characterRoles?.filter { it.rolesRu.contains("Main") }?.map {
        CharacterMain(
            id = it.character.id,
            name = it.character.russian ?: it.character.name,
            poster = it.character.poster?.originalUrl ?: BLANK
        )
    } ?: emptyList(),
    charactersAll = characterRoles?.map {
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
    kind = Enum.safeValueOf<Kind>(kind?.rawValue).title,
    season = getSeason(season, kind?.rawValue),
    poster = poster?.mainUrl ?: BLANK
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
        kind = Enum.safeValueOf<Kind>(it.linked.kind).title,
        season = getSeason(it.linked.releasedOn, it.linked.kind),
        poster = getImage(it.linked.image.original)
    )
}
package org.application.shikiapp.models.ui.mappers

import androidx.core.net.toUri
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R
import org.application.shikiapp.generated.AnimeAiringQuery
import org.application.shikiapp.generated.AnimeExtraQuery
import org.application.shikiapp.generated.AnimeListQuery
import org.application.shikiapp.generated.AnimeMainQuery
import org.application.shikiapp.generated.AnimeRandomQuery
import org.application.shikiapp.generated.fragment.Link
import org.application.shikiapp.generated.fragment.PersonRole
import org.application.shikiapp.generated.fragment.RelatedFragment
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Franchise
import org.application.shikiapp.models.data.Topic
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.Studio
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.ui.Video
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.ResourceText.StringResource
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.convertScore
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Origin
import org.application.shikiapp.utils.enums.Rating
import org.application.shikiapp.utils.enums.RelationKind
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getFull
import org.application.shikiapp.utils.getNextEpisode
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getWatchStatus
import java.time.OffsetDateTime

object AnimeMapper {
    fun create(
        main: AnimeMainQuery.Data.Anime,
        extra: AnimeExtraQuery.Data.Anime,
        franchise: Franchise,
        similar: List<AnimeBasic>,
        comments: Flow<PagingData<org.application.shikiapp.models.ui.Comment>>,
        favoured: Boolean,
    ) = Anime(
        airedOn = convertDate(main.airedOn?.date, false),
        charactersAll = extra.characterRoles.orEmpty()
            .map(AnimeExtraQuery.Data.Anime.CharacterRole::toBasicContent),
        charactersMain = extra.characterRoles.orEmpty()
            .filter { it.rolesRu.contains("Main") }
            .map(AnimeExtraQuery.Data.Anime.CharacterRole::toBasicContent),
        chronology = extra.chronology.orEmpty().map {
            Content(
                id = it.id,
                title = it.russian.orEmpty().ifEmpty(it::name),
                poster = it.poster?.mainUrl ?: BLANK,
                kind = Enum.safeValueOf<Kind>(it.kind?.rawValue),
                status = Enum.safeValueOf<Status>(it.status?.rawValue),
                season = getSeason(it.airedOn?.date, it.kind?.rawValue),
                score = it.score?.let(::convertScore)
            )
        },
        comments = comments,
        description = fromHtml(main.descriptionHtml),
        duration = main.duration?.toString()?.plus(" мин.") ?: BLANK,
        episodes = when (main.status?.rawValue?.uppercase()) {
            Status.ONGOING.name -> "${main.episodesAired} / ${getFull(main.episodes)}"
            Status.RELEASED.name -> "${main.episodes} / ${main.episodes}"
            else -> "${main.episodesAired} / ${main.episodes}"
        },
        fandubbers = main.fandubbers.sorted(),
        fansubbers = main.fansubbers.sorted(),
        favoured = AsyncData.Success(favoured),
        franchise = main.franchise ?: BLANK,
        franchiseList = franchise.let {
            it.links.filter { it.sourceId == franchise.currentId }.map { link ->
                it.nodes.associateBy { it.id }[link.targetId].let { node ->
                    org.application.shikiapp.models.ui.Franchise(
                        id = node?.id.toString(),
                        title = node?.name.orEmpty(),
                        poster = node?.imageUrl.orEmpty(),
                        year = getSeason(node?.year, node?.kind),
                        kind = Enum.safeValueOf<Kind>(node?.kind),
                        relationType = Enum.safeValueOf<RelationKind>(link.relation),
                        linkedType = if (node?.url?.contains("/animes") == true) LinkedType.ANIME
                        else LinkedType.MANGA
                    )
                }
            }
        }.groupBy { it.relationType }.apply { entries.sortedBy { it.key.order } },
        genres = main.genres?.map(AnimeMainQuery.Data.Anime.Genre::russian),
        id = main.id,
        kind = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
        licenseName = main.licenseNameRu ?: BLANK,
        licensors = main.licensors.orEmpty(),
        links = main.externalLinks.orEmpty()
            .filter { it.kind.rawValue in EXTERNAL_LINK_KINDS.keys }
            .map(AnimeMainQuery.Data.Anime.ExternalLink::mapper),
        nextEpisodeAt = getNextEpisode(main.nextEpisodeAt),
        origin = Enum.safeValueOf<Origin>(main.origin?.rawValue).title,
        personAll = extra.personRoles.orEmpty()
            .map(AnimeExtraQuery.Data.Anime.PersonRole::toBasicContent),
        personMain = extra.personRoles.orEmpty()
            .filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }
            .map(AnimeExtraQuery.Data.Anime.PersonRole::toBasicContent),
        poster = main.poster?.originalUrl ?: BLANK,
        rating = Enum.safeValueOf<Rating>(main.rating?.rawValue).title,
        related = extra.related.orEmpty().map(AnimeExtraQuery.Data.Anime.Related::mapper),
        releasedOn = convertDate(main.releasedOn?.date, false),
        score = main.score.let(::convertScore),
        screenshots = main.screenshots.map(AnimeMainQuery.Data.Anime.Screenshot::originalUrl),
        similar = similar.map(AnimeBasic::toContent),
        stats = Pair(
            first = extra.scoresStats?.let { scores ->
                Statistics(
                    sum = scores.sumOf(AnimeExtraQuery.Data.Anime.ScoresStat::count),
                    scores = scores.associate {
                        ResourceText.StaticString(it.score.toString()) to it.count.toString()
                    }
                )
            },
            second = extra.statusesStats?.let { statuses ->
                Statistics(
                    sum = statuses.sumOf(AnimeExtraQuery.Data.Anime.StatusesStat::count),
                    scores = statuses.associate {
                        StringResource(
                            getWatchStatus(
                                it.status.rawValue,
                                LinkedType.ANIME
                            )
                        ) to it.count.toString()
                    }
                )
            }
        ),
        status = Enum.safeValueOf<Status>(main.status?.rawValue).animeTitle
            ?: R.string.text_unknown,
        studio = main.studios.firstOrNull()?.let {
            Studio(
                id = it.id,
                title = it.name,
                poster = it.imageUrl.orEmpty()
            )
        },
        title = main.russian?.let { "$it / ${main.name}" } ?: main.name,
        userRate = AsyncData.Success(
            main.userRate?.let {
                UserRate(
                    id = it.id.toLong(),
                    contentId = main.id,
                    title = main.russian ?: main.name,
                    poster = main.poster?.originalUrl ?: BLANK,
                    kind = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
                    score = it.score,
                    scoreString = it.score.let { if (it != 0) it else '-' }.toString(),
                    status = it.status.rawValue,
                    text = it.text,
                    episodesSorting = 0,
                    episodes = it.episodes,
                    fullEpisodes = getFull(main.episodes, main.status?.rawValue),
                    volumes = it.volumes,
                    chapters = it.chapters,
                    rewatches = it.rewatches,
                    fullChapters = BLANK,
                    createdAt = OffsetDateTime.now(),
                    updatedAt = OffsetDateTime.now()
                )
            }
        ),
        url = main.url,
        videos = main.videos.map {
            Video(
                url = it.url,
                imageUrl = "https:${it.imageUrl}",
                kind = it.kind.rawValue,
                name = it.name
            )
        }
    )
}

fun BasicInfo.toBasicContent() = BasicContent(
    id = id.toString(),
    title = russian.orEmpty().ifEmpty(::name),
    poster = image.original
)

fun Link.mapper() = org.application.shikiapp.models.ui.ExternalLink(
    url = url.toUri(),
    title = EXTERNAL_LINK_KINDS[kind.rawValue].orEmpty(),
    kind = kind.rawValue
)

fun PersonRole.toBasicContent() = BasicContent(
    id = person.id,
    title = person.russian.orEmpty().ifEmpty(person::name),
    poster = person.poster?.originalUrl.orEmpty()
)

fun RelatedFragment.mapper() = Related(
    id = anime?.id ?: manga?.id.orEmpty(),
    title = anime?.russian ?: anime?.name ?: manga?.russian ?: manga?.name.orEmpty(),
    poster = anime?.poster?.originalUrl ?: manga?.poster?.originalUrl.orEmpty(),
    kind = Enum.safeValueOf<Kind>(anime?.kind?.rawValue ?: manga?.kind?.rawValue),
    status = Enum.safeValueOf<Status>(anime?.status?.rawValue ?: manga?.status?.rawValue),
    season = getSeason(anime?.airedOn?.date ?: manga?.airedOn?.date, anime?.kind?.rawValue ?: manga?.kind?.rawValue),
    score = convertScore(anime?.score ?: manga?.score),
    relationText = relationText,
    linkedType = if (anime != null) LinkedType.ANIME else LinkedType.MANGA
)

fun AnimeListQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = Enum.safeValueOf<Kind>(kind?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue),
    season = getSeason(season ?: airedOn?.date, kind?.rawValue),
    poster = poster?.mainUrl.orEmpty(),
    score = score?.let(::convertScore)
)

fun AnimeAiringQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.originalUrl.orEmpty(),
    kind = Kind.TV,
    season = ResourceText.StaticString(BLANK),
    score = score?.let(::convertScore),
    status = Status.ONGOING
)

fun AnimeRandomQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.originalUrl.orEmpty(),
    kind = Kind.TV,
    season = ResourceText.StaticString(BLANK),
    score = score?.let(::convertScore),
    status = Status.RELEASED
)

fun PagingData<Topic>.toAnimeContent() = map {
    Content(
        id = it.linked.id.toString(),
        title = it.linked.russian.orEmpty().ifEmpty(it.linked::name),
        kind = Enum.safeValueOf<Kind>(it.linked.kind),
        status = Enum.safeValueOf<Status>(it.linked.status),
        season = getSeason(it.linked.airedOn, it.linked.kind),
        poster = it.linked.image.original,
        score = null
    )
}

fun PagingData<AnimeBasic>.toContent(): PagingData<BasicContent> = map(AnimeBasic::toContent)
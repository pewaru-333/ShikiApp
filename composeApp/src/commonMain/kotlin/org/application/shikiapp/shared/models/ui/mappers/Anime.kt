package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.shikiapp.AnimeAiringQuery
import org.application.shikiapp.generated.shikiapp.AnimeExtraQuery
import org.application.shikiapp.generated.shikiapp.AnimeListQuery
import org.application.shikiapp.generated.shikiapp.AnimeMainQuery
import org.application.shikiapp.generated.shikiapp.AnimeRandomQuery
import org.application.shikiapp.generated.shikiapp.fragment.Link
import org.application.shikiapp.generated.shikiapp.fragment.PersonRole
import org.application.shikiapp.generated.shikiapp.fragment.RelatedFragment
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.BasicInfo
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.Topic
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.ExternalLink
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.Statistics
import org.application.shikiapp.shared.models.ui.Studio
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.models.ui.Video
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.shared.utils.ROLES_RUSSIAN
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Origin
import org.application.shikiapp.shared.utils.enums.Rating
import org.application.shikiapp.shared.utils.enums.RelationKind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.enums.VideoKind
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_minutes_short
import shikiapp.composeapp.generated.resources.text_unknown
import java.time.OffsetDateTime

object AnimeMapper {
    fun create(
        main: AnimeMainQuery.Data.Anime,
        extra: AnimeExtraQuery.Data.Anime,
        franchise: Franchise,
        similar: List<AnimeBasic>,
        comments: Flow<PagingData<Comment>>,
        favoured: Boolean,
    ): Anime {
        val video = main.videos
            .distinctBy { it.url }
            .sortedBy { it.id.toLongOrNull() }
            .map {
                Video(
                    url = it.url,
                    imageUrl = "https:${it.imageUrl}",
                    kind = it.kind.rawValue,
                    name = it.name
                )
            }

        val status = Enum.safeValueOf<Status>(main.status?.rawValue)
        val characterRoles = extra.characterRoles.orEmpty()

        return Anime(
            airedOn = Formatter.convertDate(main.airedOn?.date, false),
            charactersAll = characterRoles.map(AnimeExtraQuery.Data.Anime.CharacterRole::toBasicContent),
            charactersMain = characterRoles
                .filter { it.rolesRu.contains("Main") }
                .map(AnimeExtraQuery.Data.Anime.CharacterRole::toBasicContent),
            chronology = extra.chronology.orEmpty().map {
                Content(
                    id = it.id,
                    title = it.russian.orEmpty().ifEmpty(it::name),
                    poster = it.poster?.mainUrl.orEmpty(),
                    kind = Enum.safeValueOf<Kind>(it.kind?.rawValue),
                    status = Enum.safeValueOf<Status>(it.status?.rawValue),
                    season = Formatter.getSeason(it.airedOn?.date, it.kind?.rawValue),
                    score = it.score?.let(Formatter::convertScore)
                )
            },
            comments = comments,
            description = fromHtml(main.descriptionHtml),
            duration = if (main.duration == null || main.duration == 0) null
            else ResourceText.MultiString(
                value = listOf(
                    ResourceText.StaticString("${main.duration} "),
                    ResourceText.StringResource(Res.string.text_minutes_short)
                )
            ),
            episodes = when (status) {
                Status.ONGOING -> "${main.episodesAired} / ${Formatter.getFullEpisodes(main.episodes)}"
                Status.RELEASED -> "${main.episodes} / ${main.episodes}"
                else -> "${main.episodesAired} / ${main.episodes}"
            },
            fandubbers = main.fandubbers.sorted(),
            fansubbers = main.fansubbers.sorted(),
            favoured = AsyncData.Success(favoured),
            franchise = main.franchise.orEmpty(),
            franchiseList = franchise.toMappedList(),
            genres = main.genres?.map(AnimeMainQuery.Data.Anime.Genre::russian),
            id = main.id,
            kind = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
            licenseName = main.licenseNameRu.orEmpty(),
            licensors = main.licensors.orEmpty(),
            links = main.externalLinks.orEmpty()
                .filter { it.kind.rawValue in EXTERNAL_LINK_KINDS.keys }
                .map(AnimeMainQuery.Data.Anime.ExternalLink::mapper),
            nextEpisodeAt = Formatter.getNextEpisode(main.nextEpisodeAt),
            origin = Enum.safeValueOf<Origin>(main.origin?.rawValue).title,
            personAll = extra.personRoles.orEmpty()
                .map(AnimeExtraQuery.Data.Anime.PersonRole::toContent),
            personMain = extra.personRoles.orEmpty()
                .filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }
                .map(AnimeExtraQuery.Data.Anime.PersonRole::toContent),
            poster = Formatter.replaceMissingAnimePoster(main.poster?.originalUrl, main.id),
            rating = Enum.safeValueOf<Rating>(main.rating?.rawValue).title,
            related = extra.related.orEmpty().map(AnimeExtraQuery.Data.Anime.Related::mapper).distinctBy(Related::id),
            releasedOn = Formatter.convertDate(main.releasedOn?.date, false),
            score = main.score.let(Formatter::convertScore),
            screenshots = main.screenshots.map(AnimeMainQuery.Data.Anime.Screenshot::originalUrl),
            similar = similar.map(AnimeBasic::toContent),
            stats = Pair(
                first = extra.scoresStats?.let { scores ->
                    Statistics(
                        sum = scores.sumOf(AnimeExtraQuery.Data.Anime.ScoresStat::count),
                        scores = scores.filter { it.count > 0 }.associate {
                            ResourceText.StaticString(it.score.toString()) to it.count.toString()
                        }
                    )
                },
                second = extra.statusesStats?.let { statuses ->
                    Statistics(
                        sum = statuses.sumOf(AnimeExtraQuery.Data.Anime.StatusesStat::count),
                        scores = statuses.filter { it.count > 0 }.associate {
                            ResourceText.StringResource(Formatter.getWatchStatus(it.status.rawValue, LinkedType.ANIME)) to it.count.toString()
                        }
                    )
                }
            ),
            status = status.animeTitle ?: Res.string.text_unknown,
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
                        poster = main.poster?.originalUrl.orEmpty(),
                        kindEnum = Enum.safeValueOf<Kind>(main.kind?.rawValue),
                        kindString = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
                        score = it.score,
                        scoreString = it.score.let { if (it != 0) it else '-' }.toString(),
                        status = it.status.rawValue,
                        text = it.text,
                        episodesSorting = 0,
                        episodes = it.episodes,
                        fullEpisodes = Formatter.getFullEpisodes(main.episodes, main.status?.rawValue),
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
            video = video.take(3),
            videoGrouped = video.run {
                VideoKind.entries.associateWith { entry ->
                    filter { it.kind in entry.kinds }
                }
            }.filterValues { it.isNotEmpty() }
        )
    }
}

fun BasicInfo.toBasicContent() = BasicContent(
    id = id.toString(),
    title = russian.orEmpty().ifEmpty(::name),
    poster = image.original
)

fun Link.mapper() = ExternalLink(
    url = Url(url),
    title = EXTERNAL_LINK_KINDS[kind.rawValue].orEmpty(),
    kind = kind.rawValue
)

fun PersonRole.toContent() = Content(
    id = person.id,
    title = person.russian.orEmpty().ifEmpty(person::name),
    poster = person.poster?.originalUrl.orEmpty(),
    kind = Kind.SPECIAL,
    season = ResourceText.StaticString(rolesRu.joinToString()),
    score = null,
    status = Status.RELEASED
)

fun RelatedFragment.mapper() = Related(
    id = anime?.id ?: manga?.id.orEmpty(),
    title = anime?.russian ?: anime?.name ?: manga?.russian ?: manga?.name.orEmpty(),
    poster = anime?.poster?.originalUrl ?: manga?.poster?.originalUrl.orEmpty(),
    kind = Enum.safeValueOf<Kind>(anime?.kind?.rawValue ?: manga?.kind?.rawValue),
    status = Enum.safeValueOf<Status>(anime?.status?.rawValue ?: manga?.status?.rawValue),
    season = Formatter.getSeason(anime?.airedOn?.date ?: manga?.airedOn?.date, anime?.kind?.rawValue ?: manga?.kind?.rawValue),
    score = Formatter.convertScore(anime?.score ?: manga?.score),
    relationText = relationText,
    linkedType = if (anime != null) LinkedType.ANIME else LinkedType.MANGA
)

fun AnimeListQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = Enum.safeValueOf<Kind>(kind?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue),
    season = Formatter.getSeason(season ?: airedOn?.date, kind?.rawValue),
    poster = poster?.mainUrl.orEmpty(),
    score = score?.let(Formatter::convertScore)
)

fun AnimeAiringQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.originalUrl.orEmpty(),
    kind = Kind.TV,
    season = ResourceText.StaticString(BLANK),
    score = score?.let(Formatter::convertScore),
    status = Status.ONGOING
)

fun AnimeRandomQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.originalUrl.orEmpty(),
    kind = Kind.TV,
    season = ResourceText.StaticString(BLANK),
    score = score?.let(Formatter::convertScore),
    status = Status.RELEASED
)

fun Franchise.toMappedList(): List<Pair<RelationKind, List<org.application.shikiapp.shared.models.ui.Franchise>>> {
    val linksGrouped = links.groupBy { it.sourceId }
    val relativeRelations = mutableMapOf<Long, RelationKind>()

    val queue = ArrayDeque<Long>()
    queue.add(currentId)

    val visited = mutableSetOf<Long>()
    visited.add(currentId)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentRelation = relativeRelations[current]

        linksGrouped[current]?.forEach { link ->
            if (visited.add(link.targetId)) {
                val linkRelation = Enum.safeValueOf<RelationKind>(link.relation)
                val inheritedRelation = when {
                    currentRelation == null -> linkRelation
                    linkRelation == RelationKind.PREQUEL || linkRelation == RelationKind.SEQUEL -> currentRelation
                    else -> linkRelation
                }

                relativeRelations[link.targetId] = inheritedRelation
                queue.add(link.targetId)
            }
        }
    }

    return nodes
        .asSequence()
        .filter { it.id != currentId }
        .map { node ->
            val finalRelation = relativeRelations[node.id] ?: RelationKind.OTHER
            val linkedType = if ("/animes" in node.url) LinkedType.ANIME else LinkedType.MANGA

            org.application.shikiapp.shared.models.ui.Franchise(
                id = node.id.toString(),
                title = node.name,
                poster = if (linkedType != LinkedType.ANIME) node.imageUrl
                else Formatter.replaceMissingAnimePoster(node.imageUrl, node.id),
                year = Formatter.getSeason(node.year, node.kind),
                kind = Enum.safeValueOf<Kind>(node.kind),
                relationType = finalRelation,
                linkedType = linkedType
            )
        }
        .groupBy { it.relationType }
        .toList()
        .sortedBy { (relation, _) -> relation.order }
        .toList()
}

fun PagingData<Topic>.toAnimeContent() = map { topic ->
    with(topic.linked) {
        Content(
            id = id.toString(),
            title = russian.takeUnless { it.isNullOrBlank() } ?: name,
            kind = Enum.safeValueOf<Kind>(kind),
            status = Enum.safeValueOf<Status>(status),
            season = Formatter.getSeason(airedOn, kind),
            poster = Formatter.replaceMissingAnimePoster(image.original, id),
            score = null
        )
    }
}

fun PagingData<AnimeBasic>.toContent(): PagingData<BasicContent> = map(AnimeBasic::toContent)
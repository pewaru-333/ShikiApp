package org.application.shikiapp.shared.models.ui.mappers.dark

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.darkshiki.AnimeExtraQuery
import org.application.shikiapp.generated.darkshiki.fragment.PersonRole
import org.application.shikiapp.generated.darkshiki.fragment.RelatedFragment
import org.application.shikiapp.generated.shikiapp.AnimeMainQuery
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.Statistics
import org.application.shikiapp.shared.models.ui.Studio
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.models.ui.Video
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.models.ui.mappers.toContent
import org.application.shikiapp.shared.models.ui.mappers.toMappedList
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.shared.utils.ROLES_RUSSIAN
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Origin
import org.application.shikiapp.shared.utils.enums.Rating
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

        return Anime(
            airedOn = Formatter.convertDate(main.airedOn?.date, false),
            charactersAll = extra.characterRoles.orEmpty()
                .map(AnimeExtraQuery.Data.Anime.CharacterRole::toBasicContent),
            charactersMain = extra.characterRoles.orEmpty()
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
            episodes = when (Enum.safeValueOf<Status>(main.status?.rawValue)) {
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
            poster = main.poster?.originalUrl.orEmpty(),
            rating = Enum.safeValueOf<Rating>(main.rating?.rawValue).title,
            related = extra.related.orEmpty().map(AnimeExtraQuery.Data.Anime.Related::mapper),
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
            status = Enum.safeValueOf<Status>(main.status?.rawValue).animeTitle ?: Res.string.text_unknown,
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
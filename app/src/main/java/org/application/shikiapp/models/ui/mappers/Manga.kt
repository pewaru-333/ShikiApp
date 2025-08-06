package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_ranobe
import org.application.shikiapp.generated.MangaExtraQuery
import org.application.shikiapp.generated.MangaListQuery
import org.application.shikiapp.generated.MangaMainQuery
import org.application.shikiapp.generated.type.MangaKindEnum.light_novel
import org.application.shikiapp.generated.type.MangaKindEnum.novel
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Franchise
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.ui.Publisher
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.ResourceText.StringResource
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.convertScore
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.RelationKind
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getWatchStatus
import java.time.OffsetDateTime

object MangaMapper {
    fun create(
        main: MangaMainQuery.Data.Manga,
        extra: MangaExtraQuery.Data.Manga,
        franchise: Franchise,
        similar: List<MangaBasic>,
        comments: Flow<PagingData<Comment>>,
        favoured: Boolean,
    ) = Manga(
        airedOn = convertDate(main.airedOn?.date, false),
        chapters = main.chapters.toString(),
        charactersAll = extra.characterRoles.orEmpty()
            .map(MangaExtraQuery.Data.Manga.CharacterRole::toBasicContent),
        charactersMain = extra.characterRoles.orEmpty()
            .filter { it.rolesRu.contains("Main") }
            .map(MangaExtraQuery.Data.Manga.CharacterRole::toBasicContent),
        chronology = extra.chronology.orEmpty().map {
            Content(
                id = it.id,
                title = it.russian.orEmpty().ifEmpty(it::name),
                poster = it.poster?.mainUrl.orEmpty(),
                kind = Enum.safeValueOf<Kind>(it.kind?.rawValue),
                status = Enum.safeValueOf<Status>(it.status?.rawValue),
                season = getSeason(it.airedOn?.date, it.kind?.rawValue),
                score = it.score?.let(::convertScore)
            )
        },
        comments = comments,
        description = fromHtml(main.descriptionHtml),
        favoured = favoured,
        franchise = main.franchise.orEmpty(),
        franchiseList = franchise.let {
            it.links.filter { it.sourceId == franchise.currentId }.map { link ->
                it.nodes.associateBy { it.id } [link.targetId].let { node ->
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
        genres = main.genres?.map(MangaMainQuery.Data.Manga.Genre::russian),
        id = main.id,
        isOngoing = Status.ONGOING.safeEquals(main.status?.rawValue),
        kindEnum = Enum.safeValueOf<Kind>(main.kind?.rawValue),
        kindString = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
        kindTitle = if (main.kind in listOf(light_novel, novel)) text_ranobe else text_manga,
        licenseName = main.licenseNameRu.orEmpty(),
        licensors = main.licensors.orEmpty(),
        links = main.externalLinks.orEmpty()
            .filter { it.kind.rawValue in EXTERNAL_LINK_KINDS.keys }
            .map(MangaMainQuery.Data.Manga.ExternalLink::mapper),
        personAll = extra.personRoles.orEmpty()
            .map(MangaExtraQuery.Data.Manga.PersonRole::toBasicContent),
        personMain = extra.personRoles.orEmpty()
            .filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }
            .map(MangaExtraQuery.Data.Manga.PersonRole::toBasicContent),
        poster = main.poster?.originalUrl.orEmpty(),
        publisher = main.publishers.firstOrNull()?.let {
            Publisher(
                id = it.id,
                title = it.name
            )
        },
        related = extra.related.orEmpty().map(MangaExtraQuery.Data.Manga.Related::mapper),
        releasedOn = convertDate(main.releasedOn?.date, false),
        score = main.score.let(::convertScore),
        similar = similar.map(MangaBasic::toBasicContent),
        stats = Pair(
            first = extra.scoresStats?.let { scores ->
                Statistics(
                    sum = scores.sumOf { it.count },
                    scores = scores.associate {
                        ResourceText.StaticString(it.score.toString()) to it.count.toString()
                    }
                )
            },
            second = extra.statusesStats?.let { statuses ->
                Statistics(
                    sum = statuses.sumOf { it.count },
                    scores = statuses.associate {
                        StringResource(
                            getWatchStatus(
                                it.status.rawValue,
                                LinkedType.MANGA
                            )
                        ) to it.count.toString()
                    }
                )
            }
        ),
        status = Enum.safeValueOf<Status>(main.status?.rawValue).mangaTitle,
        title = main.russian?.let { "$it / ${main.name}" } ?: main.name,
        userRate = main.userRate?.let {
            UserRate(
                id = it.id.toLong(),
                contentId = main.id,
                title = main.russian.orEmpty().ifEmpty(main::name),
                poster = main.poster?.originalUrl.orEmpty(),
                kind = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
                score = it.score,
                scoreString = it.score.let { if (it != 0) it else '-' }.toString(),
                status = it.status.rawValue,
                text = it.text,
                episodes = it.episodes,
                fullEpisodes = BLANK,
                volumes = it.volumes,
                chapters = it.chapters,
                rewatches = it.rewatches,
                fullChapters = BLANK,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
        },
        volumes = main.volumes.toString()
    )
}

fun MangaListQuery.Data.Manga.mapper() = Content(
    id = id,
    kind = Enum.safeValueOf<Kind>(kind?.rawValue),
    poster = poster?.mainUrl.orEmpty(),
    score = score?.let(::convertScore),
    season = getSeason(airedOn?.date, kind?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue),
    title = russian.orEmpty().ifEmpty(::name),
)

fun PagingData<MangaBasic>.toContent(): PagingData<BasicContent> = map(MangaBasic::toContent)
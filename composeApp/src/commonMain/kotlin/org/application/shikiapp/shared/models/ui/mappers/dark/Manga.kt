package org.application.shikiapp.shared.models.ui.mappers.dark

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.darkshiki.MangaExtraQuery
import org.application.shikiapp.generated.shikiapp.type.MangaKindEnum.light_novel
import org.application.shikiapp.generated.shikiapp.type.MangaKindEnum.novel
import org.application.shikiapp.generated.shikiapp.MangaListQuery
import org.application.shikiapp.generated.shikiapp.MangaMainQuery
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.shared.models.ui.Publisher
import org.application.shikiapp.shared.models.ui.Statistics
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.shared.utils.ROLES_RUSSIAN
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.ResourceText.StringResource
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.safeEquals
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_manga
import shikiapp.composeapp.generated.resources.text_ranobe
import java.time.OffsetDateTime

object MangaMapper {
    fun create(
        main: MangaMainQuery.Data.Manga,
        extra: MangaExtraQuery.Data.Manga,
        franchise: Franchise,
        similar: List<MangaBasic>,
        comments: Flow<PagingData<org.application.shikiapp.shared.models.ui.Comment>>,
        favoured: Boolean,
    ) = Manga(
        airedOn = Formatter.convertDate(main.airedOn?.date, false),
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
                season = Formatter.getSeason(it.airedOn?.date, it.kind?.rawValue),
                score = it.score?.let(Formatter::convertScore)
            )
        },
        comments = comments,
        description = fromHtml(main.descriptionHtml),
        favoured = AsyncData.Success(favoured),
        franchise = main.franchise.orEmpty(),
        franchiseList = franchise.toMappedList(),
        genres = main.genres?.map(MangaMainQuery.Data.Manga.Genre::russian),
        id = main.id,
        isOngoing = Status.ONGOING.safeEquals(main.status?.rawValue),
        kindEnum = Enum.safeValueOf<Kind>(main.kind?.rawValue),
        kindString = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
        kindTitle = if (main.kind in listOf(light_novel, novel)
        ) Res.string.text_ranobe else Res.string.text_manga,
        licenseName = main.licenseNameRu.orEmpty(),
        licensors = main.licensors.orEmpty(),
        links = main.externalLinks.orEmpty()
            .filter { it.kind.rawValue in EXTERNAL_LINK_KINDS.keys }
            .map(MangaMainQuery.Data.Manga.ExternalLink::mapper),
        personAll = extra.personRoles.orEmpty()
            .map(MangaExtraQuery.Data.Manga.PersonRole::toContent),
        personMain = extra.personRoles.orEmpty()
            .filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }
            .map(MangaExtraQuery.Data.Manga.PersonRole::toContent),
        poster = main.poster?.originalUrl.orEmpty(),
        publisher = main.publishers.firstOrNull()?.let {
            Publisher(
                id = it.id,
                title = it.name
            )
        },
        related = extra.related.orEmpty().map(MangaExtraQuery.Data.Manga.Related::mapper),
        releasedOn = Formatter.convertDate(main.releasedOn?.date, false),
        score = main.score.let(Formatter::convertScore),
        similar = similar.map(MangaBasic::toContent),
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
                            Formatter.getWatchStatus(
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
        userRate = AsyncData.Success(
            main.userRate?.let {
                UserRate(
                    id = it.id.toLong(),
                    contentId = main.id,
                    title = main.russian ?: main.name,
                    poster = main.poster?.originalUrl.orEmpty(),
                    kind = Enum.safeValueOf<Kind>(main.kind?.rawValue).title,
                    score = it.score,
                    scoreString = it.score.let { if (it != 0) it else '-' }.toString(),
                    status = it.status.rawValue,
                    text = it.text,
                    episodes = it.episodes,
                    episodesSorting = 0,
                    fullEpisodes = BLANK,
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
        volumes = main.volumes.toString()
    )
}
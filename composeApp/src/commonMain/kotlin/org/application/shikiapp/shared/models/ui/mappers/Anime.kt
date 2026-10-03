package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.generated.common.*
import org.application.shikiapp.generated.common.fragment.Link
import org.application.shikiapp.shared.models.data.BasicInfo
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.Topic
import org.application.shikiapp.shared.models.ui.*
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.utils.*
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.ui.Formatter
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_unknown

fun BasicInfo.toBasicContent() = BasicContent(
    id = id.toString(),
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    poster = Formatter.replaceMissingAnimePoster(image.original, id)
)

fun Link.mapper() = ExternalLink(
    url = url,
    title = EXTERNAL_LINK_KINDS[kind.rawValue] ?: Res.string.text_unknown
)

fun AnimeListQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    kind = Enum.safeValueOf<Kind>(kind?.rawValue),
    status = Enum.safeValueOf<Status>(status?.rawValue),
    season = Formatter.getSeason(airedOn?.date ?: season, kind?.rawValue),
    poster = Formatter.replaceMissingAnimePoster(poster?.mainUrl, id),
    score = score?.let(Formatter::convertScore)
)

fun AnimeAiringQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    poster = Formatter.replaceMissingAnimePoster(poster?.mainUrl, id),
    kind = Kind.TV,
    season = ResourceText.StaticString(BLANK),
    score = score?.let(Formatter::convertScore),
    status = Status.ONGOING
)

fun AnimeRandomQuery.Data.Anime.mapper() = Content(
    id = id,
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    poster = Formatter.replaceMissingAnimePoster(poster?.mainUrl, id),
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

fun Topic.toAnimeContent() = with(linked) {
    Content(
        id = id.toString(),
        title = russian.takeUnless(String?::isNullOrBlank) ?: name,
        kind = Enum.safeValueOf<Kind>(kind),
        status = Enum.safeValueOf<Status>(status),
        season = Formatter.getSeason(airedOn, kind),
        poster = Formatter.replaceMissingAnimePoster(image.original, id),
        score = null
    )
}

fun <T> List<T>.toStatistics(countSelector: (T) -> Int, keySelector: (T) -> ResourceText): Pair<Int, Map<ResourceText, String>> {
    var sum = 0
    val map = buildMap {
        this@toStatistics.forEach { item ->
            val count = countSelector(item)
            if (count > 0) {
                sum += count
                put(keySelector(item), count.toString())
            }
        }
    }

    return sum to map
}
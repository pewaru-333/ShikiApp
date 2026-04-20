package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.shikiapp.CharacterListQuery
import org.application.shikiapp.generated.shikiapp.fragment.CharacterRole
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.BasicInfo
import org.application.shikiapp.shared.models.data.Character
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter

object CharacterMapper {
    suspend fun create(
        character: Character,
        image: String?,
        comments: Flow<PagingData<Comment>>
    ) = withContext(Dispatchers.Default) {
        val relatedList = character.animes.map(AnimeBasic::toRelated) +
                character.mangas.map(MangaBasic::toRelated)

        org.application.shikiapp.shared.models.ui.Character(
            altName = character.altName,
            anime = character.animes.map(AnimeBasic::toContent),
            comments = comments,
            description = fromHtml(character.descriptionHTML),
            favoured = AsyncData.Success(character.favoured),
            id = character.id.toString(),
            japanese = character.japanese,
            manga = character.mangas.map(MangaBasic::toContent),
            poster = image.orEmpty(),
            relatedList = relatedList,
            relatedMap = relatedList.groupBy(Related::linkedType).toSortedMap(),
            russian = character.russian,
            seyu = character.seyu.map(BasicInfo::toBasicContent),
            url = "${ApiRoutes.workingBaseUrl}${character.url}"
        )
    }
}

fun org.application.shikiapp.shared.models.data.BasicContent.toRelated(relationText: String = BLANK): Related {
    val kindEnum = Enum.safeValueOf<Kind>(kind)

    return Related(
        id = id.toString(),
        title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
        poster = Formatter.replaceMissingAnimePoster(image.original, id),
        kind = kindEnum,
        status = Enum.safeValueOf<Status>(status),
        season = Formatter.getSeason(airedOn, kind),
        score = Formatter.convertScore(score),
        relationText = relationText,
        linkedType = kindEnum.linkedType
    )
}

fun org.application.shikiapp.shared.models.data.BasicContent.toContent() = Content(
    id = id.toString(),
    kind = Enum.safeValueOf<Kind>(kind),
    poster = image.original,
    score = score?.let(Formatter::convertScore),
    season = Formatter.getSeason(airedOn, kind),
    status = Enum.safeValueOf<Status>(status),
    title = russian.orEmpty().ifEmpty(::name)
)

fun CharacterRole.toBasicContent() = BasicContent(
    id = character.id,
    title = character.russian.orEmpty().ifEmpty(character::name),
    poster = character.poster?.originalUrl.orEmpty()
)

fun CharacterListQuery.Data.Character.mapper() = BasicContent(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.mainUrl.orEmpty(),
)

fun PagingData<BasicInfo>.toContent() = map {
    BasicContent(
        id = it.id.toString(),
        title = it.russian.orEmpty().ifEmpty(it::name),
        poster = it.image.original
    )
}
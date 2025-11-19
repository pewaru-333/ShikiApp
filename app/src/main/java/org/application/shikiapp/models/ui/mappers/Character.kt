package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.CharacterListQuery
import org.application.shikiapp.generated.CharacterQuery
import org.application.shikiapp.generated.fragment.CharacterRole
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Character
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.models.ui.Comment
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.convertScore
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getSeason

object CharacterMapper {
    suspend fun create(
        character: Character,
        image: CharacterQuery.Data.Character,
        comments: Flow<PagingData<Comment>>
    ) = withContext(Dispatchers.Default) {
        val relatedList = character.animes.map(AnimeBasic::toRelated) +
                character.mangas.map(MangaBasic::toRelated)

        org.application.shikiapp.models.ui.Character(
            altName = character.altName,
            anime = character.animes.map(AnimeBasic::toContent),
            comments = comments,
            description = fromHtml(character.descriptionHTML),
            favoured = AsyncData.Success(character.favoured),
            id = character.id.toString(),
            japanese = character.japanese,
            manga = character.mangas.map(MangaBasic::toContent),
            poster = image.poster?.originalUrl.orEmpty(),
            relatedList = relatedList,
            relatedMap = relatedList.groupBy(Related::linkedType).toSortedMap(),
            russian = character.russian,
            seyu = character.seyu.map(BasicInfo::toBasicContent),
            url = character.url
        )
    }
}

fun org.application.shikiapp.models.data.BasicContent.toRelated(relationText: String = BLANK) =
    Related(
        id = id.toString(),
        title = russian.orEmpty().ifEmpty(::name),
        poster = image.original,
        kind = Enum.safeValueOf<Kind>(kind),
        status = Enum.safeValueOf<Status>(status),
        season = getSeason(airedOn, kind),
        score = convertScore(score),
        relationText = relationText,
        linkedType = Enum.safeValueOf<Kind>(kind).linkedType
    )

fun org.application.shikiapp.models.data.BasicContent.toContent() = Content(
    id = id.toString(),
    kind = Enum.safeValueOf<Kind>(kind),
    poster = image.original,
    score = score?.let(::convertScore),
    season = getSeason(airedOn, kind),
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
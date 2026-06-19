package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
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
    fun create(character: Character, image: String?, comments: Flow<PagingData<Comment>>): org.application.shikiapp.shared.models.ui.Character {
        val relatedList = ArrayList<Related>(character.animes.size + character.mangas.size).apply {
            character.animes.mapTo(this, AnimeBasic::toRelated)
            character.mangas.mapTo(this, MangaBasic::toRelated)
        }

        return org.application.shikiapp.shared.models.ui.Character(
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
            relatedMap = relatedList.groupBy(Related::linkedType),
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
    poster = Formatter.replaceMissingAnimePoster(image.original, id),
    score = score?.let(Formatter::convertScore),
    season = Formatter.getSeason(airedOn, kind),
    status = Enum.safeValueOf<Status>(status),
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name
)

fun CharacterRole.toBasicContent() = BasicContent(
    id = character.id,
    title = character.russian.takeUnless(String?::isNullOrEmpty) ?: character.name,
    poster = character.poster?.originalUrl.orEmpty()
)

fun CharacterListQuery.Data.Character.mapper() = BasicContent(
    id = id,
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    poster = poster?.mainUrl.orEmpty(),
)
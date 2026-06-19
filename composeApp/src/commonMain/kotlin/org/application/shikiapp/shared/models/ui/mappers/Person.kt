package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.shikiapp.PeopleQuery
import org.application.shikiapp.shared.models.data.Person
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.enums.LinkedKind
import org.application.shikiapp.shared.utils.ui.Formatter

fun Person.mapper(comments: Flow<PagingData<Comment>>): org.application.shikiapp.shared.models.ui.Person {
    val works = works.orEmpty().mapNotNull {
        val relationText = it.role.orEmpty()
        it.anime?.toRelated(relationText) ?: it.manga?.toRelated(relationText)
    }

    val (birthday, deathday) = Formatter.getPersonDates(birthday, deceasedOn)

    return org.application.shikiapp.shared.models.ui.Person(
        birthday = birthday,
        characters = buildList {
            roles?.forEach { role ->
                role.characters.forEach { character ->
                    add(character.toBasicContent())
                }
            }
        },
        comments = comments,
        deathday = deathday,
        english = name,
        grouppedRoles = grouppedRoles,
        id = id,
        image = image.original,
        favoured = AsyncData.Success(personFavoured || producerFavoured || mangakaFavoured || seyuFavoured),
        japanese = japanese,
        jobTitle = jobTitle,
        personKind = when {
            seyu -> LinkedKind.SEYU.name.lowercase()
            mangaka -> LinkedKind.MANGAKA.name.lowercase()
            producer -> LinkedKind.PRODUCER.name.lowercase()
            else -> LinkedKind.PERSON.name.lowercase()
        },
        relatedList = works,
        relatedMap = works.groupBy(Related::linkedType),
        russian = russian,
        url = "${ApiRoutes.workingBaseUrl}$url",
        website = website
    )
}

fun PeopleQuery.Data.Person.mapper() = BasicContent(
    id = id,
    title = russian.takeUnless(String?::isNullOrEmpty) ?: name,
    poster = poster?.mainUrl.orEmpty()
)
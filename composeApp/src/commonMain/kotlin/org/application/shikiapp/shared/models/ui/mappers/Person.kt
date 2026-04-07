package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.shikiapp.PeopleQuery
import org.application.shikiapp.shared.models.data.BasicInfo
import org.application.shikiapp.shared.models.data.Person
import org.application.shikiapp.shared.models.data.Roles
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.enums.LinkedKind
import org.application.shikiapp.shared.utils.ui.Formatter

suspend fun Person.mapper(comments: Flow<PagingData<Comment>>) = withContext(Dispatchers.Default) {
    val works = works.orEmpty().mapNotNull {
        it.anime?.toRelated(it.role.orEmpty()) ?: it.manga?.toRelated(it.role.orEmpty())
    }

    val (birthday, deathday) = Formatter.getPersonDates(birthday, deceasedOn)

    org.application.shikiapp.shared.models.ui.Person(
        birthday = birthday,
        characters = roles.orEmpty().flatMap(Roles::characters).map(BasicInfo::toBasicContent),
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
        relatedMap = works.groupBy(Related::linkedType).toSortedMap(),
        russian = russian,
        url = "${ApiRoutes.workingBaseUrl}$url",
        website = website
    )
}


fun PeopleQuery.Data.Person.mapper() = BasicContent(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.mainUrl.orEmpty()
)
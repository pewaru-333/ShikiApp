package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.ui.Comment
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.BASE_URL
import org.application.shikiapp.utils.enums.LinkedKind
import org.application.shikiapp.utils.getPersonDates

suspend fun Person.mapper(comments: Flow<PagingData<Comment>>) = withContext(Dispatchers.Default) {
    val works = works.orEmpty().mapNotNull {
        it.anime?.toRelated(it.role.orEmpty()) ?: it.manga?.toRelated(it.role.orEmpty())
    }

    val (birthday, deathday) = getPersonDates(birthday, deceasedOn)

    org.application.shikiapp.models.ui.Person(
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
        url = "$BASE_URL$url",
        website = website
    )
}


fun PeopleQuery.Data.Person.mapper() = BasicContent(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.mainUrl.orEmpty()
)
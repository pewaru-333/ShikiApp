package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.enums.LinkedKind
import org.application.shikiapp.utils.getBirthday
import org.application.shikiapp.utils.getDeathday

fun Person.mapper(comments: Flow<PagingData<Comment>>): org.application.shikiapp.models.ui.Person {
    val works = works.orEmpty().map {
        it.anime?.toRelated(it.role.orEmpty()) ?: it.manga!!.toRelated(it.role.orEmpty())
    }

    return org.application.shikiapp.models.ui.Person(
        birthday = getBirthday(birthday),
        characters = roles.orEmpty().flatMap(Roles::characters).map(BasicInfo::toBasicContent),
        comments = comments,
        deathday = getDeathday(deceasedOn),
        english = name,
        grouppedRoles = grouppedRoles,
        id = id,
        image = image.original,
        isPersonFavoured = personFavoured || producerFavoured || mangakaFavoured || seyuFavoured,
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
        url = url,
        website = website,
    )
}


fun PeopleQuery.Data.Person.mapper() = BasicContent(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    poster = poster?.mainUrl.orEmpty()
)
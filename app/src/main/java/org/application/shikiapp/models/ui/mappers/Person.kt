package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.PeopleQuery
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.LINKED_KIND
import org.application.shikiapp.utils.getBirthday
import org.application.shikiapp.utils.getDeathday

fun Person.mapper(comments: Flow<PagingData<Comment>>) = org.application.shikiapp.models.ui.Person(
    id = id,
    russian = russian,
    english = name,
    japanese = japanese,
    birthday = getBirthday(birthday),
    deathday = getDeathday(deceasedOn),
    jobTitle = jobTitle,
    image = image.original,
    website = website,
    grouppedRoles = grouppedRoles,
    characters = roles?.flatMap(Roles::characters)?.map {
        CharacterMain(
            id = it.id.toString(),
            name = it.russian.orEmpty().ifEmpty(it::name),
            poster = it.image.original
        )
    } ?: emptyList(),
    comments = comments,
    isPersonFavoured = personFavoured || producerFavoured || mangakaFavoured || seyuFavoured,
    personKind = when {
        seyu -> LINKED_KIND[1]
        mangaka -> LINKED_KIND[2]
        producer -> LINKED_KIND[3]
        else -> LINKED_KIND[4]
    }
)

fun PeopleQuery.Data.Person.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = BLANK,
    season = BLANK,
    poster = poster?.mainUrl
)
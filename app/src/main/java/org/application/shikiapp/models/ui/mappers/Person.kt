package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.LinkedKind
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
        seyu -> LinkedKind.SEYU.name.lowercase()
        mangaka -> LinkedKind.MANGAKA.name.lowercase()
        producer -> LinkedKind.PRODUCER.name.lowercase()
        else -> LinkedKind.PERSON.name.lowercase()
    }
)

fun PeopleQuery.Data.Person.mapper() = Content(
    id = id,
    title = russian.orEmpty().ifEmpty(::name),
    kind = R.string.blank,
    season = ResourceText.StringResource(R.string.blank),
    poster = poster?.mainUrl ?: BLANK,
    score = null
)
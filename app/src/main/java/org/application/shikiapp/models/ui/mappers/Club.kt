package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ResourceText

fun Club.mapper(
    images: Flow<PagingData<ClubImages>>,
    members: Flow<PagingData<UserBasic>>,
    animes: Flow<PagingData<Content>>,
    mangas: Flow<PagingData<Content>>,
    ranobe: Flow<PagingData<Content>>,
    characters: Flow<PagingData<Content>>,
    clubs: Flow<PagingData<Content>>,
    comments: Flow<PagingData<Comment>>
) = org.application.shikiapp.models.ui.Club(
    id = id,
    topicId = topicId,
    name = name,
    image = logo.original,
    description = fromHtml(descriptionHtml),
    images = images,
    members = members,
    animes = animes,
    mangas = mangas,
    ranobe = ranobe,
    characters = characters,
    clubs = clubs,
    comments = comments,
    isCensored = isCensored,
    joinPolicy = joinPolicy,
    commentPolicy = commentPolicy
)

fun PagingData<ClubBasic>.toContent() = map {
    Content(
        id = it.id.toString(),
        title = it.name,
        kind = R.string.blank,
        season = ResourceText.StaticString(BLANK),
        poster = it.logo.main.orEmpty(),
        score = null
    )
}

fun Club.toContent() = Content(
    id = id.toString(),
    title = name,
    kind = R.string.blank,
    season = ResourceText.StaticString(BLANK),
    poster = logo.main.orEmpty(),
    score = null
)
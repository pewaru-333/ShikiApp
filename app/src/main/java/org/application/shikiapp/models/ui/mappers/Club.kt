package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.fromHtml

fun Club.mapper(
    images: Flow<PagingData<ClubImages>>,
    members: Flow<PagingData<UserBasic>>,
    animes: Flow<PagingData<BasicContent>>,
    mangas: Flow<PagingData<BasicContent>>,
    ranobe: Flow<PagingData<BasicContent>>,
    characters: Flow<PagingData<BasicContent>>,
    clubs: Flow<PagingData<BasicContent>>,
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
    BasicContent(
        id = it.id.toString(),
        title = it.name,
        poster = it.logo.main.orEmpty()
    )
}

fun Club.toContent() = BasicContent(
    id = id.toString(),
    title = name,
    poster = logo.main.orEmpty()
)
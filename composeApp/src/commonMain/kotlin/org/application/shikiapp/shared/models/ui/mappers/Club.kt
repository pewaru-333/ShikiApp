package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.data.Club
import org.application.shikiapp.shared.models.data.ClubImages
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.utils.ui.HtmlParser

fun Club.mapper(comments: Flow<PagingData<Comment>>) = org.application.shikiapp.shared.models.ui.Club(
    name = name,
    image = logo.original,
    description = HtmlParser.parseComment(descriptionHtml.orEmpty()),
    comments = comments,
    isCensored = isCensored,
    joinPolicy = joinPolicy,
    commentPolicy = commentPolicy
)

fun Club.toContent() = BasicContent(
    id = id.toString(),
    title = name,
    poster = logo.main.orEmpty()
)

fun ClubImages.toContent() = BasicContent(
    id = id.toString(),
    title = mainUrl.orEmpty(),
    poster = originalUrl.orEmpty()
)
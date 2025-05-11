package org.application.shikiapp.models.ui.mappers

import org.application.shikiapp.models.data.News
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getNewsPoster

fun News.mapper() = org.application.shikiapp.models.ui.list.News(
    id = id,
    title = topicTitle,
    poster = getNewsPoster(htmlFooter),
    date = convertDate(createdAt),
    author = user.nickname
)
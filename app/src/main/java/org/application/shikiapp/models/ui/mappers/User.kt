package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.UsersQuery
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.fromHtml
import org.application.shikiapp.utils.getWatchStatus

fun User.mapper(
    clubs: List<ClubBasic>,
    comments: Flow<PagingData<Comment>>,
    friends: Flow<PagingData<UserBasic>>,
    history: Flow<PagingData<History>>,
    favourites: Favourites
) = org.application.shikiapp.models.ui.User(
    about = fromHtml(aboutHtml),
    avatar = image.x160,
    clubs = clubs,
    comments = comments,
    commonInfo = fromHtml(commonInfo.joinToString(" / ")),
    favourites = favourites,
    friends = friends,
    history = history,
    id = id,
    inFriends = inFriends == true,
    lastOnline = lastOnline.orEmpty(),
    nickname = nickname,
    stats = Pair(
        first = Statistics(
            sum = stats.statuses.anime.sumOf { it.size.toInt() },
            scores = stats.statuses.anime.associate {
                ResourceText.StringResource(getWatchStatus(it.name, LinkedType.ANIME)) to it.size.toString()
            }
        ),
        second = Statistics(
            sum = stats.statuses.manga.sumOf { it.size.toInt() },
            scores = stats.statuses.manga.associate {
                ResourceText.StringResource(getWatchStatus(it.name, LinkedType.MANGA)) to it.size.toString()
            }
        )
    )
)

fun UsersQuery.Data.User.toContent() = BasicContent(
    id = id,
    title = nickname,
    poster = avatarUrl
)
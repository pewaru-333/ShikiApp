package org.application.shikiapp.shared.models.ui.mappers

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.shikiapp.UsersQuery
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.data.ClubBasic
import org.application.shikiapp.shared.models.data.User
import org.application.shikiapp.shared.models.data.UserBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.History
import org.application.shikiapp.shared.models.ui.Statistics
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.FavouriteItem
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.fromHtml
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser

suspend fun User.mapper(
    clubs: List<ClubBasic>,
    comments: Flow<PagingData<Comment>>,
    friends: Flow<PagingData<UserBasic>>,
    history: Flow<PagingData<History>>,
    favourites: Map<FavouriteItem, List<BasicContent>>
) = withContext(Dispatchers.Default) {
    val animeStatsSum = stats.statuses.anime.sumOf { it.size.toInt() }
    val mangaStatsSum = stats.statuses.manga.sumOf { it.size.toInt() }

    org.application.shikiapp.shared.models.ui.User(
        about = HtmlParser.parseComment(aboutHtml),
        avatar = image.x160,
        banned = banned,
        clubs = clubs.map {
            BasicContent(
                id = it.id.toString(),
                title = it.name,
                poster = it.logo.original.orEmpty()
            )
        },
        comments = comments,
        commonInfo = fromHtml(commonInfo.joinToString(" / ")),
        favourites = favourites,
        friends = friends.map { list ->
            list.map {
                BasicContent(
                    id = it.id.toString(),
                    title = it.nickname,
                    poster = it.image.x148
                )
            }
        },
        history = history,
        id = id,
        inFriends = inFriends == true,
        lastOnline = lastOnline.orEmpty(),
        nickname = nickname,
        sex = sex,
        showStats = Preferences.userId != id && (animeStatsSum + mangaStatsSum > 0),
        stats = Pair(
            first = Statistics(
                sum = animeStatsSum,
                scores = stats.statuses.anime.associate {
                    ResourceText.StringResource(
                        Formatter.getWatchStatus(it.name, LinkedType.ANIME)
                    ) to it.size.toString()
                }
            ),
            second = Statistics(
                sum = mangaStatsSum,
                scores = stats.statuses.manga.associate {
                    ResourceText.StringResource(
                        Formatter.getWatchStatus(it.name, LinkedType.MANGA)
                    ) to it.size.toString()
                }
            )
        )
    )
}

fun UsersQuery.Data.User.toContent() = BasicContent(
    id = id,
    title = nickname,
    poster = avatarUrl
)
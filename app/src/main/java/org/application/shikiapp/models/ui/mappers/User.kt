package org.application.shikiapp.models.ui.mappers

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.R
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.screens.fromHtml
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ResourceText

fun User.mapper(
    clubs: List<ClubBasic>,
    comments: Flow<PagingData<Comment>>,
    friends: Flow<PagingData<UserBasic>>,
    history: Flow<PagingData<History>>,
    favourites: Favourites
) = org.application.shikiapp.models.ui.User(
    id = id,
    nickname = nickname,
    avatar = image.x160,
    lastOnline = lastOnline.orEmpty(),
    about = fromHtml(aboutHtml),
    commonInfo = fromHtml(commonInfo.joinToString(" / ")),
    inFriends = inFriends == true,
    clubs = clubs,
    comments = comments,
    friends = friends,
    history = history,
    stats = stats,
    favourites = favourites
)

fun UserBasic.toContent() = Content(
    id = id.toString(),
    title = nickname,
    kind = R.string.blank,
    season = ResourceText.StaticString(BLANK),
    poster = image.x80
)
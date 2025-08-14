package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.utils.enums.FavouriteItem

data class User(
    val about: AnnotatedString,
    val avatar: String,
    val clubs: List<BasicContent>,
    val comments: Flow<PagingData<Comment>>,
    val commonInfo: AnnotatedString,
    val favourites: Map<FavouriteItem, List<BasicContent>>,
    val friends: Flow<PagingData<BasicContent>>,
    val history: Flow<PagingData<History>>,
    val id: Long,
    val inFriends: Boolean,
    val lastOnline: String,
    val nickname: String,
    val stats: Pair<Statistics?, Statistics?>
)

package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.UserBasic

data class User(
    val about: AnnotatedString,
    val avatar: String,
    val clubs: List<ClubBasic>,
    val comments: Flow<PagingData<Comment>>,
    val commonInfo: AnnotatedString,
    val favourites: Favourites,
    val friends: Flow<PagingData<UserBasic>>,
    val history: Flow<PagingData<History>>,
    val id: Long,
    val inFriends: Boolean,
    val lastOnline: String,
    val nickname: String,
    val stats: Pair<Statistics?, Statistics?>
)

package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.Stats
import org.application.shikiapp.models.data.UserBasic

data class User(
    val id: Long,
    val nickname: String,
    val avatar: String,
    val lastOnline: String,
    val about: AnnotatedString,
    val commonInfo: AnnotatedString,
    val inFriends: Boolean,
    val clubs: List<ClubBasic>,
    val comments: Flow<PagingData<Comment>>,
    val friends: Flow<PagingData<UserBasic>>,
    val history: Flow<PagingData<History>>,
    val stats: Stats,
    val favourites: Favourites
)

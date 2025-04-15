package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic

data class User(
    val user: User,
    val clubs: List<ClubBasic>,
    val comments: Flow<PagingData<Comment>>,
    val friends: Flow<PagingData<UserBasic>>,
    val history: Flow<PagingData<History>>,
    val favourites: Favourites
)

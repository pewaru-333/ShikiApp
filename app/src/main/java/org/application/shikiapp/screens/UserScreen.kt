package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.events.UserDetailEvent
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.viewModels.UserViewModel
import org.application.shikiapp.network.Response
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<UserViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is Response.Error -> ErrorScreen()
        is Response.Loading -> LoadingScreen()
        is Response.Success -> UserView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(
    user: User,
    state: UserState,
    onEvent: (UserDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val friends = user.friends.collectAsLazyPagingItems()
    val history = user.history.collectAsLazyPagingItems()
    val comments = user.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (Preferences.getUserId() != user.user.id) NavigationIcon(back)
                    else IconButton(back) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, null)
                    }
                },
                actions = {
                    if (comments.itemCount > 0)
                        IconButton(
                            onClick = { onEvent(UserDetailEvent.ShowComments) }
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                    IconButton(
                        onClick = { onEvent(UserDetailEvent.ShowSheet) }
                    ) {
                        Icon(Icons.Outlined.MoreVert, null)
                    }
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                UserBriefItem(user.user)
            }
            item {
                BriefInfo(
                    setMenu = { onEvent(UserDetailEvent.PickMenu(it)) }
                )
            }
            item {
                UserStats(
                    id = user.user.id,
                    stats = user.user.stats,
                    onNavigate = onNavigate
                )
            }
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(UserDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogFavourites(
        favourites = user.favourites,
        tab = state.favouriteTab,
        visible = state.showFavourite,
        setTab = { onEvent(UserDetailEvent.PickFavouriteTab(it)) },
        onNavigate = onNavigate,
        hide = { onEvent(UserDetailEvent.ShowFavourite) }
    )

    DialogHistory(
        history = history,
        visible = state.showHistory,
        onNavigate = onNavigate,
        hide = { onEvent(UserDetailEvent.ShowHistory) }
    )

    DialogItem(
        state = state,
        menu = state.menu,
        visible = state.showDialog,
        friends = friends,
        clubs = user.clubs,
        hide = { onEvent(UserDetailEvent.PickMenu()) },
        onNavigate = onNavigate
    )

    if (state.showSheet)
        BottomSheet(
            state = state.sheetState,
            hideSheet = { onEvent(UserDetailEvent.ShowSheet) },
            showFavourite = { onEvent(UserDetailEvent.ShowFavourite) },
            showHistory = { onEvent(UserDetailEvent.ShowHistory) }
        )
}
package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.states.showClubs
import org.application.shikiapp.models.states.showFavourite
import org.application.shikiapp.models.states.showFriends
import org.application.shikiapp.models.states.showHistory
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.viewModels.UserViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
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
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit,
    visibility: NavigationBarVisibility? = null
) {
    val friends = user.friends.collectAsLazyPagingItems()
    val history = user.history.collectAsLazyPagingItems()
    val comments = user.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (Preferences.userId == user.id) {
                        IconButton(back) {
                            Icon(Icons.AutoMirrored.Outlined.ExitToApp, null)
                        }
                    } else {
                        NavigationIcon(back)
                    }
                },
                actions = {
                    if (comments.itemCount > 0) {
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.ShowComments) }
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                    }

                    if (Preferences.token != null && Preferences.userId != user.id) {
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.User.ShowDialogToggleFriend) }
                        ) {
                            Icon(
                                contentDescription = null,
                                painter = painterResource(
                                    if (user.inFriends) R.drawable.vector_remove_friend
                                    else R.drawable.vector_add_friend
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserBriefItem(user)
            }

            item {
                HorizontalDivider()
            }

            item {
                UserMenuItems { onEvent(ContentDetailEvent.User.PickMenu(it)) }
            }

            item {
                HorizontalDivider()
            }

            item {
                UserStats(
                    id = user.id,
                    stats = user.stats,
                    onNavigate = onNavigate
                )
            }

            user.about.let {
                if (it.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                    }

                    item {
                        Column(Modifier.wrapContentHeight(), Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = stringResource(R.string.text_about_me),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(it)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(state.menu) {
        visibility?.let { bottomBar ->
            if (state.menu != null) bottomBar.hide()
            else bottomBar.show()
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogFriends(
        friends = friends,
        visible = state.showFriends,
        onNavigate = onNavigate,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    DialogClubs(
        clubs = user.clubs,
        visible = state.showClubs,
        onNavigate = onNavigate,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    DialogFavourites(
        favourites = user.favourites,
        tab = state.favouriteTab,
        visible = state.showFavourite,
        setTab = { onEvent(ContentDetailEvent.User.PickFavouriteTab(it)) },
        onNavigate = onNavigate,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    DialogHistory(
        history = history,
        visible = state.showHistory,
        onNavigate = onNavigate,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    if (state.showDialogToggleFriend) {
        DialogToggleFriend(user.inFriends, onEvent)
    }
}

@Composable
private fun DialogToggleFriend(inFriends: Boolean, onEvent: (ContentDetailEvent) -> Unit) =
    AlertDialog(
        onDismissRequest = { onEvent(ContentDetailEvent.User.ShowDialogToggleFriend) },
        dismissButton = {
            TextButton(
                onClick = { onEvent(ContentDetailEvent.User.ShowDialogToggleFriend) }
            ) { Text(stringResource(R.string.text_cancel)) }
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleFriend) }
            ) { Text(stringResource(R.string.text_confirm)) }
        },
        title = {
            Text(
                text = stringResource(
                    if (inFriends) R.string.text_remove_friend
                    else R.string.text_add_friend
                )
            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(
                    if (inFriends) R.string.text_confirm_remove_friend
                    else R.string.text_confirm_add_friend
                )
            )
        }
    )
package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.UserMessagesState
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.states.showClubs
import org.application.shikiapp.models.states.showDialogDelete
import org.application.shikiapp.models.states.showFavourite
import org.application.shikiapp.models.states.showFriends
import org.application.shikiapp.models.states.showHistory
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.viewModels.UserMessagesViewModel
import org.application.shikiapp.models.viewModels.UserViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.BLANK
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
                    TopBarActions(user, onEvent, comments.itemCount > 0)
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
                UserMenuItems { onEvent(ContentDetailEvent.User.PickMenu(it)) }
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

    LaunchedEffect(state.menu, state.showDialogs) {
        visibility?.let { bottomBar ->
            if (state.menu != null || state.showDialogs) bottomBar.hide()
            else bottomBar.show()
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogUserDialogs(
        visible = state.showDialogs,
        onNavigate = onNavigate,
        dialogId = if (Preferences.userId == user.id) null else user.nickname,
        userId = if (Preferences.userId == user.id) null else user.id,
        hide = { onEvent(ContentDetailEvent.User.ShowDialogs) }
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
private fun TopBarActions(
    user: User,
    onEvent: (ContentDetailEvent) -> Unit,
    showComments: Boolean
) {
    when {
        Preferences.userId == user.id -> {
            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ShowDialogs) },
                content = { Icon(Icons.Outlined.Email, null) }
            )
        }

        else -> {
            if (showComments) {
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

            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ShowDialogs) },
                content = { Icon(Icons.Outlined.Email, null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogUserDialogs(
    hide: () -> Unit,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    dialogId: String? = null,
    userId: Long? = null
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    val model = viewModel<UserMessagesViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val response by model.response.collectAsStateWithLifecycle()
    val dialogMessages by model.messages.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (dialogId != null && userId != null) {
            model.getDialog(userId, dialogId)
        }
    }

    BackHandler {
        if (state.dialogId == null || userId != null) hide()
        else model.showDialogs()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        text = state.dialogId ?: stringResource(R.string.text_dialogs),
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    NavigationIcon {
                        if (state.dialogId == null || userId != null) hide()
                        else model.showDialogs()
                    }
                }
            )
        }
    ) { values ->
        Crossfade(state.dialogId) {
            when (it) {
                null -> AllUserDialogs(
                    values = values,
                    response = response,
                    reload = model::loadData,
                    getDialog = model::getDialog,
                    showDelete = model::showDialogDelete
                )

                else -> UserDialog(
                    values = values,
                    state = state,
                    dialogMessages = dialogMessages,
                    onNavigate = onNavigate,
                    reload = model::getDialog,
                    setText = model::setText,
                    sendMessage = model::sendMessage
                )
            }
        }
    }

    if (state.showDialogDelete) {
        DialogRemoveUserDialog(
            nickname = state.toDeleteId ?: BLANK,
            hide = model::showDialogDelete,
            remove = model::removeDialog
        )
    }
}

@Composable
private fun UserDialog(
    dialogMessages: Response<List<Dialog>, Throwable>,
    state: UserMessagesState,
    values: PaddingValues,
    reload: () -> Unit,
    setText: (String) -> Unit,
    sendMessage: () -> Unit,
    onNavigate: (Screen) -> Unit
) = when (val messages = dialogMessages) {
    Response.Loading -> LoadingScreen()
    is Response.Error -> ErrorScreen(reload)
    is Response.Success -> Column(Modifier.imePadding()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            reverseLayout = true,
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding())
        ) {
            items(messages.data) { message ->
                Column {
                    ListItem(
                        modifier = Modifier.offset(x = (-8).dp),
                        headlineContent = { Text(message.userNickname) },
                        supportingContent = { Text(message.lastDate) },
                        leadingContent = {
                            AsyncImage(
                                model = message.userAvatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigate(Screen.User(message.userId)) }
                            )
                        }
                    )

                    HtmlCommentBody(message.lastMessage)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Spacer(Modifier.width(8.dp))

            OutlinedTextField(
                maxLines = 1,
                value = state.text,
                onValueChange = setText,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.text_enter_message)) },
            )

            IconButton(
                enabled = state.text.isNotEmpty(),
                content = { Icon(Icons.AutoMirrored.Outlined.Send, null) },
                onClick = sendMessage
            )
        }
    }

    else -> Unit
}

@Composable
private fun AllUserDialogs(
    response: Response<List<Dialog>, Throwable>,
    reload: () -> Unit,
    getDialog: (Long, String) -> Unit,
    showDelete: (String) -> Unit,
    values: PaddingValues
) = when (val dialogs = response) {
    Response.Loading -> LoadingScreen()
    is Response.Error -> ErrorScreen(reload)
    is Response.Success -> LazyColumn(contentPadding = values) {
        items(dialogs.data) { dialog ->
            ListItem(
                headlineContent = { Text(dialog.userNickname) },
                trailingContent = { Text(dialog.lastDate) },
                modifier = Modifier
                    .combinedClickable(
                        onClick = { getDialog(dialog.userId, dialog.userNickname) },
                        onLongClick = { showDelete(dialog.id) }
                    ),
                leadingContent = {
                    AsyncImage(
                        model = dialog.userAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                },
                supportingContent = {
                    Text(
                        minLines = 2,
                        maxLines = 2,
                        text = dialog.lastMessage,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }

    else -> Unit
}

@Composable
private fun DialogRemoveUserDialog(nickname: String, hide: () -> Unit, remove: () -> Unit) =
    AlertDialog(
        onDismissRequest = hide,
        dismissButton = {
            TextButton(hide) {
                Text(stringResource(R.string.text_cancel))
            }
        },
        confirmButton = {
            TextButton(remove) {
                Text(stringResource(R.string.text_confirm))
            }
        },
        text = {
            Text(
                text = stringResource(R.string.text_sure_to_delete_dialog, nickname),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )

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
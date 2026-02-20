@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.UserDialogState
import org.application.shikiapp.models.states.UserMessagesState
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.states.showClubs
import org.application.shikiapp.models.states.showDialogUser
import org.application.shikiapp.models.states.showDialogs
import org.application.shikiapp.models.states.showFavourite
import org.application.shikiapp.models.states.showFriends
import org.application.shikiapp.models.states.showHistory
import org.application.shikiapp.models.ui.Comment
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.ui.list.Message
import org.application.shikiapp.models.viewModels.UserViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.DialogClubs
import org.application.shikiapp.ui.templates.DialogFavourites
import org.application.shikiapp.ui.templates.DialogFriends
import org.application.shikiapp.ui.templates.DialogHistory
import org.application.shikiapp.ui.templates.DialogList
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.Messages
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.Notifications
import org.application.shikiapp.ui.templates.Statistics
import org.application.shikiapp.ui.templates.UserBriefItem
import org.application.shikiapp.ui.templates.UserMenuItems
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.MessageType
import org.application.shikiapp.utils.extensions.showToast
import org.application.shikiapp.utils.navigation.NavigationBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun UserScreen(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val model = viewModel<UserViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    AnimatedScreen(response, model::loadData) { user ->
        UserView(user, state, model.mailManager, model::onEvent, onNavigate, onBack)
    }
}

@Composable
fun UserView(
    user: User,
    state: UserState,
    mailManager: UserViewModel.MailManager,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    visibility: NavigationBarVisibility? = null
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val friends = user.friends.collectAsLazyPagingItems()
    val history = user.history.collectAsLazyPagingItems()
    val comments = user.comments.collectAsLazyPagingItems()

    val mailState by mailManager.state.collectAsStateWithLifecycle()
    val dialogs by mailManager.dialogs.collectAsStateWithLifecycle()
    val news = mailManager.news.collectAsLazyPagingItems()
    val notifications = mailManager.notifications.collectAsLazyPagingItems()
    val newMessages by mailManager.newMessages.collectAsStateWithLifecycle()
    val oldMessages = mailManager.oldMessages.collectAsLazyPagingItems()

    val listStates = FavouriteItem.entries.map { rememberLazyListState() }

    LaunchedEffect(mailManager.dialogDeleteError) {
        mailManager.dialogDeleteError.collectLatest(context::showToast)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (Preferences.userId == user.id) {
                        IconButton(onBack) { VectorIcon(R.drawable.vector_exit_app) }
                    } else {
                        NavigationIcon(onBack)
                    }
                },
                actions = {
                    TopBarActions(user, state, comments, onEvent)
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

            if (Preferences.userId != user.id) {
                item {
                    Statistics(
                        id = user.id,
                        statistics = user.stats,
                        onNavigate = onNavigate
                    )
                }
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

    LaunchedEffect(state.dialogState, state.menu) {
        if (state.dialogState != null || state.menu != null) {
            visibility?.hide()
        } else {
            visibility?.show()
        }
    }

    LaunchedEffect(state.showDialogs) {
        if (!state.showDialogs) {
            mailManager.getUnreadMessages()
        }
    }

    Comments(
        list = comments,
        listState = listState,
        visible = state.dialogState is UserDialogState.Comments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogMail(
        mailManager = mailManager,
        dialogs = dialogs,
        news = news,
        notifications = notifications,
        visible = state.showDialogs,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.User.ToggleDialog(null)) }
    )

    UserDialog(
        dialogMessages = oldMessages,
        newMessages = newMessages,
        state = mailState,
        visible = state.showDialogUser,
        onNavigate = onNavigate,
        sendMessage = mailManager::sendMessage,
        onShowDelete = mailManager::showDialogDelete,
        onBack = {
            onEvent(
                ContentDetailEvent.User.ToggleDialog(
                    dialog = if (mailState.isFromList) UserDialogState.DialogAll else null
                )
            )
        }
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
        visible = state.showFavourite,
        listStates = listStates,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
        onNavigate = onNavigate,
    )

    DialogHistory(
        history = history,
        visible = state.showHistory,
        hide = { onEvent(ContentDetailEvent.User.PickMenu()) },
        onNavigate = onNavigate,
    )

    when {
        state.showDeleteUserDialog -> DialogRemoveUserDialog(
            nickname = mailState.userNickname.getValue().orEmpty(),
            hide = mailManager::showDialogDelete,
            remove = mailManager::removeDialog
        )

        mailState.showDeleteAll -> DialogRemoveAllNews(
            onCancel = mailManager::showDialogDeleteAll,
            onConfirm = mailManager::deleteAllMessages
        )
    }

    if (state.dialogState is UserDialogState.ToggleFriend) {
        DialogToggleFriend(user.inFriends, onEvent)
    }
}

@Composable
private fun TopBarActions(
    user: User,
    state: UserState,
    comments: LazyPagingItems<Comment>,
    onEvent: (ContentDetailEvent) -> Unit
) {
    when {
        Preferences.userId == user.id -> {
            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.DialogAll)) },
                content = {
                    BadgedBox(
                        content = { VectorIcon(R.drawable.vector_mail) },
                        badge = {
                            state.unreadMessages.total.let {
                                if (it > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        content = {
                                            Text(
                                                text = if (it >= 10) "!" else "$it",
                                                autoSize = TextAutoSize.StepBased(
                                                    minFontSize = 10.sp,
                                                    maxFontSize = 11.sp,
                                                    stepSize = (0.1).sp
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            )
            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.Settings)) },
                content = { VectorIcon(R.drawable.vector_settings) }
            )
        }

        else -> {
            IconComment(
                onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount },
                onEvent = { onEvent(ContentDetailEvent.ShowComments) }
            )

            if (Preferences.token != null && Preferences.userId != user.id) {
                IconButton(
                    onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.ToggleFriend)) },
                    content = {
                        VectorIcon(
                            resId = if (user.inFriends) R.drawable.vector_remove_friend
                            else R.drawable.vector_add_friend
                        )
                    }
                )
            }

            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.DialogUser(user.id))) },
                content = { VectorIcon(R.drawable.vector_mail) }
            )
        }
    }
}

@Composable
private fun DialogMail(
    mailManager: UserViewModel.MailManager,
    dialogs: Response<List<Dialog>, Exception>,
    news: LazyPagingItems<Message>,
    notifications: LazyPagingItems<Message>,
    visible: Boolean,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    LaunchedEffect(visible) {
        if (visible) {
           mailManager.loadData()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
    ) {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = MessageType.tabs::size)

        fun onScroll(page: Int) {
            scope.launch {
                pagerState.animateScrollToPage(page)
                mailManager.pickTab(MessageType.tabs[page])
            }
        }

        BackHandler(visible, onHide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.text_mail)) },
                    navigationIcon = { NavigationIcon(onHide) },
                    actions = {
                        if (pagerState.currentPage > 0) {
                            IconButton(
                                onClick = { mailManager.showDialogDeleteAll() },
                                content = { VectorIcon(R.drawable.vector_trash) })
                            IconButton(
                                content = { VectorIcon(R.drawable.vector_check) },
                                onClick = {
                                    mailManager.markAllRead(
                                        if (pagerState.currentPage == 1) news.itemSnapshotList.items
                                        else notifications.itemSnapshotList.items
                                    )
                                }
                            )
                        }
                    }
                )
            }
        ) { values ->
            Column(Modifier.padding(values)) {
                PrimaryTabRow(pagerState.currentPage) {
                    MessageType.tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == pagerState.targetPage,
                            onClick = { onScroll(index) },
                            text = {
                                Text(
                                    maxLines = 1,
                                    text = stringResource(tab.title),
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    when (page) {
                        0 -> DialogList(
                            dialogs = dialogs,
                            getDialog = mailManager::getDialog,
                            loadData = mailManager::loadData
                        )

                        1 -> Messages(
                            list = news,
                            onNavigate = onNavigate,
                            onMarkRead = mailManager::markRead,
                            onDelete = mailManager::deleteMessage
                        )

                        2 -> Notifications(
                            list = notifications,
                            onNavigate = onNavigate,
                            onMarkRead = mailManager::markRead,
                            onDelete = mailManager::deleteMessage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDialog(
    dialogMessages: LazyPagingItems<Dialog>,
    newMessages: List<Dialog>,
    state: UserMessagesState,
    visible: Boolean,
    onBack: () -> Unit,
    onShowDelete: () -> Unit,
    sendMessage: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember(::FocusRequester)
    val listState = rememberLazyListState()
    val textFieldState = rememberTextFieldState()

    BackHandler(visible, onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(IntrinsicSize.Min),
                navigationIcon = { NavigationIcon(onBack) },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                actions = {
                    AnimatedVisibility(dialogMessages.itemSnapshotList.isNotEmpty()) {
                        IconButton(onShowDelete) { VectorIcon(R.drawable.vector_trash) }
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onNavigate(Screen.User(state.userId)) }
                            .padding(6.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = state.userAvatar.getValue(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border((0.5).dp, MaterialTheme.colorScheme.onTertiaryContainer, CircleShape)
                        ) {
                            val state by painter.state.collectAsStateWithLifecycle()
                            if (state is AsyncImagePainter.State.Success) {
                                SubcomposeAsyncImageContent()
                            } else {
                                CircularProgressIndicator()
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            text = state.userNickname.getValue().orEmpty().ifEmpty {
                                stringResource(R.string.text_loading)
                            }
                        )
                    }
                }
            )
        }
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .imePadding()
        ) {
            when (dialogMessages.loadState.refresh) {
                LoadState.Loading -> LoadingScreen(Modifier.weight(1f))
                is LoadState.Error -> ErrorScreen(dialogMessages::retry)
                is LoadState.NotLoading ->
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                        reverseLayout = true,
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(newMessages, Dialog::id) { message ->
                            MessageBubble(message, Modifier.animateItem())
                        }
                        items(dialogMessages.itemCount, dialogMessages.itemKey(Dialog::id)) { index ->
                            dialogMessages[index]?.let { MessageBubble(it) }
                        }
                        if (dialogMessages.loadState.append == LoadState.Loading) {
                            item {
                                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                                    CircularProgressIndicator(Modifier.padding(16.dp))
                                }
                            }
                        }
                        if (dialogMessages.loadState.hasError) item { ErrorScreen(dialogMessages::retry) }
                    }
            }

            if (dialogMessages.loadState.refresh !is LoadState.Error) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        state = textFieldState,
                        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                        placeholder = { Text(stringResource(R.string.text_enter_message)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Default
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        content = { VectorIcon(R.drawable.vector_send) },
                        enabled = textFieldState.text.isNotBlank(),
                        onClick = {
                            sendMessage(textFieldState.text.toString())
                            textFieldState.clearText()
                            focusRequester.requestFocus()
                            scope.launch { listState.animateScrollToItem(0) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Dialog, modifier: Modifier = Modifier) {
    val alignment = if (message.accountUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.accountUser) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.accountUser) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (message.accountUser) 48.dp else 0.dp,
                end = if (message.accountUser) 0.dp else 48.dp
            )
    ) {
        Column(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.accountUser) 16.dp else 0.dp,
                        bottomEnd = if (message.accountUser) 0.dp else 16.dp
                    )
                )
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            HtmlComment(message.lastMessage)

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = message.lastDate,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textColor.copy(alpha = 0.7f)
                    )
                )

                if (message.accountUser) {
                    when {
                        message.isSending -> {
                            VectorIcon(
                                resId = R.drawable.vector_mail,
                                modifier = Modifier.size(14.dp),
                                tint = textColor.copy(alpha = 0.7f)
                            )
                        }

                        message.isError -> {
                            VectorIcon(
                                resId = R.drawable.vector_bad,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogRemoveUserDialog(nickname: String, hide: () -> Unit, remove: () -> Unit) =
    AlertDialog(
        onDismissRequest = hide,
        dismissButton = { TextButton(hide) { Text(stringResource(R.string.text_dismiss)) } },
        confirmButton = { TextButton(remove) { Text(stringResource(R.string.text_confirm)) } },
        text = {
            Text(
                text = stringResource(R.string.text_sure_to_delete_dialog, nickname),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )

@Composable
private fun DialogRemoveAllNews(onConfirm: () -> Unit, onCancel: () -> Unit) =
    AlertDialog(
        onDismissRequest = onCancel,
        dismissButton = { TextButton(onCancel) { Text(stringResource(R.string.text_dismiss)) } },
        confirmButton = { TextButton(onConfirm) { Text(stringResource(R.string.text_confirm)) } },
        title = { Text(stringResource(R.string.text_pay_attention)) },
        text = {
            Text(
                text = stringResource(R.string.text_sure_to_delete_all_notifications),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )

@Composable
private fun DialogToggleFriend(inFriends: Boolean, onEvent: (ContentDetailEvent) -> Unit) =
    AlertDialog(
        onDismissRequest = { onEvent(ContentDetailEvent.User.ToggleDialog(null)) },
        dismissButton = {
            TextButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(null)) },
                content = { Text(stringResource(R.string.text_dismiss)) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleFriend) },
                content = { Text(stringResource(R.string.text_confirm)) }
            )
        },
        title = {
            Text(
                text = stringResource(
                    id = if (inFriends) R.string.text_remove_friend
                    else R.string.text_add_friend
                )
            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(
                    id = if (inFriends) R.string.text_confirm_remove_friend
                    else R.string.text_confirm_add_friend
                )
            )
        }
    )
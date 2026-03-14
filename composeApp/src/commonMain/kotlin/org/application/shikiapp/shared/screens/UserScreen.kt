@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.UserDialogState
import org.application.shikiapp.shared.models.states.UserMessagesState
import org.application.shikiapp.shared.models.states.UserState
import org.application.shikiapp.shared.models.states.showClubs
import org.application.shikiapp.shared.models.states.showDialogUser
import org.application.shikiapp.shared.models.states.showDialogs
import org.application.shikiapp.shared.models.states.showFavourite
import org.application.shikiapp.shared.models.states.showFriends
import org.application.shikiapp.shared.models.states.showHistory
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.User
import org.application.shikiapp.shared.models.ui.list.Dialog
import org.application.shikiapp.shared.models.ui.list.Message
import org.application.shikiapp.shared.models.viewModels.UserViewModel
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogClubs
import org.application.shikiapp.shared.ui.templates.DialogFavourites
import org.application.shikiapp.shared.ui.templates.DialogFriends
import org.application.shikiapp.shared.ui.templates.DialogHistory
import org.application.shikiapp.shared.ui.templates.DialogImages
import org.application.shikiapp.shared.ui.templates.DialogList
import org.application.shikiapp.shared.ui.templates.ErrorScreen
import org.application.shikiapp.shared.ui.templates.HtmlContent
import org.application.shikiapp.shared.ui.templates.IconComment
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.Messages
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.Notifications
import org.application.shikiapp.shared.ui.templates.Statistics
import org.application.shikiapp.shared.ui.templates.UserMenuItems
import org.application.shikiapp.shared.ui.templates.UserProfileHeader
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.templates.htmlContent
import org.application.shikiapp.shared.utils.enums.FavouriteItem
import org.application.shikiapp.shared.utils.enums.MessageType
import org.application.shikiapp.shared.utils.navigation.NavigationBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberToastState
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_about_me
import shikiapp.composeapp.generated.resources.text_add_friend
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_confirm_add_friend
import shikiapp.composeapp.generated.resources.text_confirm_remove_friend
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_enter_message
import shikiapp.composeapp.generated.resources.text_loading
import shikiapp.composeapp.generated.resources.text_mail
import shikiapp.composeapp.generated.resources.text_pay_attention
import shikiapp.composeapp.generated.resources.text_remove_friend
import shikiapp.composeapp.generated.resources.text_sure_to_delete_all_notifications
import shikiapp.composeapp.generated.resources.text_sure_to_delete_dialog
import shikiapp.composeapp.generated.resources.vector_add_friend
import shikiapp.composeapp.generated.resources.vector_bad
import shikiapp.composeapp.generated.resources.vector_check
import shikiapp.composeapp.generated.resources.vector_exit_app
import shikiapp.composeapp.generated.resources.vector_mail
import shikiapp.composeapp.generated.resources.vector_remove_friend
import shikiapp.composeapp.generated.resources.vector_send
import shikiapp.composeapp.generated.resources.vector_settings
import shikiapp.composeapp.generated.resources.vector_trash

@Composable
fun UserScreen(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val model = viewModel(::UserViewModel)
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
    val toast = rememberToastState()

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

    val commentsState = rememberLazyListState()
    val listState = rememberLazyListState()
    val isHeaderScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    var galleryInfo by remember { mutableStateOf<Pair<List<CommentContent.ImageContent>, Int>?>(null) }

    LaunchedEffect(mailManager.dialogDeleteError) {
        mailManager.dialogDeleteError.collectLatest(toast::onShow)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(isHeaderScrolled, Modifier, fadeIn(), fadeOut()) {
                        Text(
                            text = user.nickname,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    if (Preferences.userId == user.id) {
                        IconButton(onBack) { VectorIcon(Res.drawable.vector_exit_app) }
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
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = values.calculateTopPadding() + 8.dp,
                end = 16.dp,
                bottom = 32.dp
            )
        ) {
            item {
                UserProfileHeader(user)
            }

            item {
                UserMenuItems { onEvent(ContentDetailEvent.User.PickMenu(it)) }
            }

            if (user.showStats) {
                item {
                    Statistics(
                        id = user.id,
                        statistics = user.stats,
                        onNavigate = onNavigate
                    )
                }
            }

            if (user.about.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))

                        Text(
                            text = stringResource(Res.string.text_about_me),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                htmlContent(user.about) { images, index -> galleryInfo = images to index }
            }
        }
    }

    galleryInfo?.let { (imageContents, initialIndex) ->
        val imageUrls = imageContents.map { it.fullUrl ?: it.previewUrl }

        DialogImages(
            images = imageUrls,
            initialIndex = initialIndex,
            isVisible = true,
            onClose = { galleryInfo = null }
        )
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
        listState = commentsState,
        isVisible = state.dialogState is UserDialogState.Comments,
        onHide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogMail(
        mailManager = mailManager,
        dialogs = dialogs,
        news = news,
        notifications = notifications,
        isVisible = state.showDialogs,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.User.ToggleDialog(null)) }
    )

    UserDialog(
        dialogMessages = oldMessages,
        newMessages = newMessages,
        state = mailState,
        isVisible = state.showDialogUser,
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
        isVisible = state.showFriends,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    DialogClubs(
        clubs = user.clubs,
        isVisible = state.showClubs,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.User.PickMenu()) },
    )

    DialogFavourites(
        favourites = user.favourites,
        isVisible = state.showFavourite,
        listStates = listStates,
        onHide = { onEvent(ContentDetailEvent.User.PickMenu()) },
        onNavigate = onNavigate,
    )

    DialogHistory(
        history = history,
        isVisible = state.showHistory,
        onHide = { onEvent(ContentDetailEvent.User.PickMenu()) },
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
                        content = { VectorIcon(Res.drawable.vector_mail) },
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
                content = { VectorIcon(Res.drawable.vector_settings) }
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
                            resId = if (user.inFriends) Res.drawable.vector_remove_friend
                            else Res.drawable.vector_add_friend
                        )
                    }
                )
            }

            IconButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.DialogUser(user.id))) },
                content = { VectorIcon(Res.drawable.vector_mail) }
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
    isVisible: Boolean,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
           mailManager.loadData()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
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

        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = isVisible,
            onBackCompleted = onHide
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.text_mail)) },
                    navigationIcon = { NavigationIcon(onHide) },
                    actions = {
                        if (pagerState.currentPage > 0) {
                            IconButton(
                                onClick = { mailManager.showDialogDeleteAll() },
                                content = { VectorIcon(Res.drawable.vector_trash) }
                            )
                            IconButton(
                                content = { VectorIcon(Res.drawable.vector_check) },
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
    isVisible: Boolean,
    onBack: () -> Unit,
    onShowDelete: () -> Unit,
    sendMessage: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember(::FocusRequester)
    val listState = rememberLazyListState()
    val textFieldState = rememberTextFieldState()

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onBack
    )
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
                        IconButton(onShowDelete) { VectorIcon(Res.drawable.vector_trash) }
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
                                stringResource(Res.string.text_loading)
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
                        placeholder = { Text(stringResource(Res.string.text_enter_message)) },
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
                        content = { VectorIcon(Res.drawable.vector_send) },
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
    val backgroundColor = if (message.accountUser) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.surfaceContainerLow

    fun bubbleShape(isSelf: Boolean) = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isSelf) 16.dp else 4.dp,
        bottomEnd = if (isSelf) 4.dp else 16.dp
    )

    Box(
        contentAlignment = if (message.accountUser) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 2.dp)
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .shadow(0.5.dp, bubbleShape(message.accountUser))
                .background(backgroundColor, bubbleShape(message.accountUser))
                .padding(12.dp, 8.dp)
        ) {
            HtmlContent(message.lastMessage)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = message.lastDate,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )

                if (message.accountUser) {
                    val (icon, color) = when {
                        message.isError -> Res.drawable.vector_bad to MaterialTheme.colorScheme.error
                        message.isSending -> Res.drawable.vector_mail to MaterialTheme.colorScheme.primary
                        else -> Res.drawable.vector_check to MaterialTheme.colorScheme.primary
                    }

                    VectorIcon(
                        resId = icon,
                        modifier = Modifier.size(12.dp),
                        tint = color.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogRemoveUserDialog(nickname: String, hide: () -> Unit, remove: () -> Unit) =
    AlertDialog(
        onDismissRequest = hide,
        dismissButton = { TextButton(hide) { Text(stringResource(Res.string.text_dismiss)) } },
        confirmButton = { TextButton(remove) { Text(stringResource(Res.string.text_confirm)) } },
        text = {
            Text(
                text = stringResource(Res.string.text_sure_to_delete_dialog, nickname),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )

@Composable
private fun DialogRemoveAllNews(onConfirm: () -> Unit, onCancel: () -> Unit) =
    AlertDialog(
        onDismissRequest = onCancel,
        dismissButton = { TextButton(onCancel) { Text(stringResource(Res.string.text_dismiss)) } },
        confirmButton = { TextButton(onConfirm) { Text(stringResource(Res.string.text_confirm)) } },
        title = { Text(stringResource(Res.string.text_pay_attention)) },
        text = {
            Text(
                text = stringResource(Res.string.text_sure_to_delete_all_notifications),
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
                content = { Text(stringResource(Res.string.text_dismiss)) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(ContentDetailEvent.User.ToggleFriend) },
                content = { Text(stringResource(Res.string.text_confirm)) }
            )
        },
        title = {
            Text(
                text = stringResource(
                    resource = if (inFriends) Res.string.text_remove_friend
                    else Res.string.text_add_friend
                )
            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(
                    resource = if (inFriends) Res.string.text_confirm_remove_friend
                    else Res.string.text_confirm_add_friend
                )
            )
        }
    )
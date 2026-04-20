@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.shared.models.ui.list.Dialog
import org.application.shikiapp.shared.models.ui.list.Message
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_broadcast_message_club
import shikiapp.composeapp.generated.resources.text_no_messages
import shikiapp.composeapp.generated.resources.vector_bookmark
import shikiapp.composeapp.generated.resources.vector_check
import shikiapp.composeapp.generated.resources.vector_trash

@Composable
fun DialogList(
    dialogs: Response<List<Dialog>, Exception>,
    getDialog: (Long, Boolean) -> Unit,
    loadData: () -> Unit
) = AnimatedScreen(dialogs, loadData) { dialogList ->
    LazyColumn(Modifier.fillMaxSize()) {
        items(dialogList, key = Dialog::id) { dialog ->
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { getDialog(dialog.userId, true) }
                        .padding(16.dp, 12.dp)
                ) {
                    CircleContentImage(dialog.userAvatar, Modifier.size(56.dp))

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(
                                maxLines = 1,
                                text = dialog.userNickname,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text = dialog.lastDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = dialog.lastMessage.asComposableString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(start = 88.dp),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun MessageCardItem(
    title: String,
    kind: Kind,
    season: ResourceText,
    score: String?,
    status: Status,
    image: String?,
    isRead: AsyncData<Boolean>,
    isDeleting: AsyncData<Boolean>,
    onClick: () -> Unit,
    onMarkRead: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var lastRead by remember { mutableStateOf(isRead.getValue() ?: false) }
    if (isRead is AsyncData.Success) {
        lastRead = isRead.data
    }
    val readState = isRead.getValue() ?: lastRead

    val backgroundColor by animateColorAsState(
        animationSpec = tween(300),
        targetValue = if (readState) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    )

    MediaListItem(
        title = title,
        poster = image,
        score = score,
        season = season.asComposableString(),
        kind = kind,
        status = status,
        onClick = onClick,
        backgroundColor = backgroundColor,
        actions = {
            Row(Modifier.fillMaxWidth(), Arrangement.End, Alignment.CenterVertically) {
                AnimatedContent(isRead) { state ->
                    when (state) {
                        is AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                        is AsyncData.Success -> with(state.getValue()) {
                            FilledTonalIconButton(
                                onClick = { onMarkRead(if (this) 0 else 1) },
                                content = {
                                    VectorIcon(
                                        resId = if (this) Res.drawable.vector_bookmark
                                        else Res.drawable.vector_check
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                AnimatedContent(isDeleting) { state ->
                    when (state) {
                        is AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                        is AsyncData.Success -> {
                            if (!state.getValue()) {
                                FilledTonalIconButton(
                                    content = { VectorIcon(Res.drawable.vector_trash) },
                                    onClick = onDelete,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun Messages(
    list: LazyPagingItems<Message>,
    onNavigate: (Screen) -> Unit,
    onMarkRead: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        when (list.loadState.refresh) {
            is LoadState.Loading if list.itemCount == 0 -> {
                LoadingScreen(Modifier.background(MaterialTheme.colorScheme.surface))
            }

            is LoadState.Error if list.itemCount == 0 -> {
                ErrorScreen(list::retry)
            }

            is LoadState.NotLoading if list.itemCount == 0 && list.loadState.append.endOfPaginationReached -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(Res.string.text_no_messages))
                }
            }

            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(list.itemCount, list.itemKey(Message::id)) { index ->
                        list[index]?.let { news ->
                            if (news.linked != null) {
                                MessageCardItem(
                                    title = news.linked.title,
                                    kind = news.linked.kind,
                                    season = news.linked.season,
                                    score = news.linked.score,
                                    status = news.linked.status,
                                    image = news.linked.poster,
                                    isRead = news.read,
                                    isDeleting = news.isDeleting,
                                    onMarkRead = { onMarkRead(news.id, it) },
                                    onDelete = { onDelete(news.id) },
                                    onClick = { onNavigate(Screen.Anime(news.linked.id)) }
                                )
                            } else {
                                BaseMessageCardItem(
                                    news = news,
                                    onMarkRead = { onMarkRead(news.id, it) },
                                    onDelete = { onDelete(news.id) },
                                    onUserClick = { onNavigate(Screen.User(it)) }
                                )
                            }

                            if (index < list.itemCount - 1) {
                                HorizontalDivider()
                            }
                        }
                    }

                    if (list.loadState.append is LoadState.Loading) {
                        item { LoadingScreen(Modifier.padding(8.dp)) }
                    }

                    if (list.loadState.append is LoadState.Error) {
                        item { ErrorScreen(list::retry) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BaseMessageCardItem(
    news: Message,
    onUserClick: (Long) -> Unit,
    onMarkRead: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var lastRead by remember { mutableStateOf(news.read.getValue() ?: false) }
    if (news.read is AsyncData.Success) {
        lastRead = news.read.data
    }
    val readState = news.read.getValue() ?: lastRead

    val backgroundColor by animateColorAsState(
        animationSpec = tween(300),
        targetValue = if (readState) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserClick(news.from.id) }
                .padding(vertical = 4.dp)
        ) {
            CircleContentImage(news.from.avatar, Modifier.size(40.dp))

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = news.from.nickname,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )

                if (news.isBroadcast) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = stringResource(Res.string.text_broadcast_message_club),
                            modifier = Modifier.padding(6.dp, 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            }

            Row(Modifier.offset(x = 4.dp), Arrangement.End, Alignment.CenterVertically) {
                AnimatedContent(news.read) { state ->
                    when (state) {
                        is AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))
                        is AsyncData.Success -> with(state.getValue()) {
                            FilledTonalIconButton(
                                onClick = { onMarkRead(if (this) 0 else 1) },
                                content = {
                                    VectorIcon(
                                        resId = if (this) Res.drawable.vector_bookmark
                                        else Res.drawable.vector_check
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                AnimatedContent(news.isDeleting) { state ->
                    when (state) {
                        is AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))
                        is AsyncData.Success -> {
                            if (!state.getValue()) {
                                FilledTonalIconButton(
                                    content = { VectorIcon(Res.drawable.vector_trash) },
                                    onClick = onDelete,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        HtmlContent(news.body)
    }
}

@Composable
private fun Notification(
    notification: Message,
    modifier: Modifier = Modifier,
    onMarkRead: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var lastRead by remember { mutableStateOf(notification.read.getValue() ?: false) }
    if (notification.read is AsyncData.Success) {
        lastRead = notification.read.data
    }
    val readState = notification.read.getValue() ?: lastRead

    val backgroundColor by animateColorAsState(
        animationSpec = tween(300),
        targetValue = if (readState) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp, 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(Screen.User(notification.from.id)) }
        ) {
            CircleContentImage(notification.from.avatar, Modifier.size(56.dp))

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = notification.from.nickname,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = notification.createdAt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (val state = notification.read) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                    is AsyncData.Success<*> -> {
                        if (state.getValue() == true) {
                            FilledTonalIconButton(
                                content = { VectorIcon(Res.drawable.vector_bookmark) },
                                onClick = { onMarkRead(notification.id, 0) }
                            )
                        } else {
                            FilledTonalIconButton(
                                content = { VectorIcon(Res.drawable.vector_check) },
                                onClick = { onMarkRead(notification.id, 1) }
                            )
                        }
                    }
                }

                when (val state = notification.isDeleting) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                    is AsyncData.Success<*> -> if (state.getValue() == false) {
                        FilledTonalIconButton(
                            content = { VectorIcon(Res.drawable.vector_trash) },
                            onClick = { onDelete(notification.id) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        HtmlContent(notification.body)
    }
}

@Composable
fun Notifications(
    list: LazyPagingItems<Message>,
    onMarkRead: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        when (list.loadState.refresh) {
            is LoadState.Loading if list.itemCount == 0 -> {
                LoadingScreen(Modifier.background(MaterialTheme.colorScheme.surface))
            }

            is LoadState.Error if list.itemCount == 0 -> {
                ErrorScreen(list::retry)
            }

            is LoadState.NotLoading if list.itemCount == 0 && list.loadState.append.endOfPaginationReached -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(Res.string.text_no_messages))
                }
            }

            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(list.itemCount, list.itemKey(Message::id)) { index ->
                        list[index]?.let { notification ->
                            Notification(
                                notification = notification,
                                modifier = Modifier.animateItem(),
                                onMarkRead = onMarkRead,
                                onDelete = onDelete,
                                onNavigate = onNavigate
                            )

                            if (index < list.itemCount - 1) {
                                HorizontalDivider()
                            }
                        }
                    }


                    if (list.loadState.append is LoadState.Loading) {
                        item { LoadingScreen(Modifier.padding(8.dp)) }
                    }

                    if (list.loadState.append is LoadState.Error) {
                        item { ErrorScreen(list::retry) }
                    }
                }
            }
        }
    }
}
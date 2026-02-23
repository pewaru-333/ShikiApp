package org.application.shikiapp.ui.templates

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.R
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.ui.list.Message
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.enums.backgroundColor
import org.application.shikiapp.utils.enums.textColor
import org.application.shikiapp.utils.extensions.getLastMessage
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun DialogList(
    dialogs: Response<List<Dialog>, Exception>,
    getDialog: (Long, String, String) -> Unit,
    loadData: () -> Unit
) = AnimatedScreen(dialogs, loadData) { dialogs ->
    LazyColumn {
        items(dialogs, Dialog::id) { dialog ->
            Column(
                modifier = Modifier.clickable {
                    getDialog(dialog.userId, dialog.userNickname, dialog.userAvatar)
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 12.dp)
                ) {
                    AnimatedAsyncImage(
                        model = dialog.userAvatar,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border((0.5).dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dialog.userNickname,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = dialog.lastDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dialog.lastMessage.getLastMessage().asString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
fun MessageCardItem(
    title: String,
    kind: Kind,
    season: ResourceText,
    score: String?,
    status: Status,
    image: String?,
    isRead: AsyncData<Boolean>,
    isDeleting: AsyncData<Boolean>,
    modifier: Modifier = Modifier,
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

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AnimatedAsyncImage(
                model = image,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .border((0.5).dp, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.shapes.medium)
            )

            if (score != null) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp, 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VectorIcon(
                            resId = R.drawable.vector_star,
                            tint = Color(0xFFFFC319),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = score,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f), Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = stringResource(kind.title) + season.asString().run {
                    if (isEmpty()) "" else (" · $this")
                }
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = status.backgroundColor
            ) {
                Text(
                    text = stringResource(status.getTitle(kind)),
                    modifier = Modifier.padding(8.dp, 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = status.textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth(), Arrangement.End, Alignment.CenterVertically) {
                AnimatedContent(isRead) { state ->
                    when (state) {
                        is AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                        is AsyncData.Success -> with(state.getValue()) {
                            FilledTonalIconButton(
                                onClick = { onMarkRead(if (this) 0 else 1) },
                                content = {
                                    VectorIcon(
                                        resId = if (this) R.drawable.vector_bookmark
                                        else R.drawable.vector_check
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
                                    content = { VectorIcon(R.drawable.vector_trash) },
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
    }
}

@Composable
fun Messages(
    list: LazyPagingItems<Message>,
    onNavigate: (Screen) -> Unit,
    onMarkRead: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit
) = Box(Modifier.fillMaxSize()) {
    LazyColumn {
        items(list.itemCount, list.itemKey(Message::id)) { index ->
            list[index]?.let { news ->
                news.linked?.let { linked ->
                    MessageCardItem(
                        title = linked.title,
                        kind = linked.kind,
                        season = linked.season,
                        score = linked.score,
                        status = linked.status,
                        image = linked.poster,
                        isRead = news.read,
                        isDeleting = news.isDeleting,
                        modifier = Modifier.animateItem(),
                        onMarkRead = { onMarkRead(news.id, it) },
                        onDelete = { onDelete(news.id) },
                        onClick = { onNavigate(Screen.Anime(linked.id)) })
                }
            }

            if (index < list.itemCount - 1) {
                HorizontalDivider()
            }
        }

        when (list.loadState.append) {
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.Error -> item { ErrorScreen(list::retry) }
            else -> Unit
        }
    }

    if (list.itemCount == 0) {
        when (list.loadState.refresh) {
            is LoadState.Loading -> LoadingScreen(Modifier.background(MaterialTheme.colorScheme.surface))
            is LoadState.Error -> ErrorScreen(list::retry)
            is LoadState.NotLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.text_no_messages))
            }
        }
    }
}


@Composable
fun Notification(
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
            AnimatedAsyncImage(
                model = notification.from.avatar,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )

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
                                content = { VectorIcon(R.drawable.vector_bookmark) },
                                onClick = { onMarkRead(notification.id, 0) }
                            )
                        } else {
                            FilledTonalIconButton(
                                content = { VectorIcon(R.drawable.vector_check) },
                                onClick = { onMarkRead(notification.id, 1) }
                            )
                        }
                    }
                }

                when (val state = notification.isDeleting) {
                    AsyncData.Loading -> CircularProgressIndicator(Modifier.size(40.dp))

                    is AsyncData.Success<*> -> if (state.getValue() == false) {
                        FilledTonalIconButton(
                            content = { VectorIcon(R.drawable.vector_trash) },
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

        HtmlComment(notification.body)
    }
}

@Composable
fun Notifications(
    list: LazyPagingItems<Message>,
    onMarkRead: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = Box(Modifier.fillMaxSize()) {
    LazyColumn {
        items(list.itemCount, list.itemKey(Message::id)) { index ->
            list[index]?.let { notification ->
                Notification(notification, Modifier.animateItem(), onMarkRead, onDelete, onNavigate)

                if (index < list.itemCount - 1) {
                    HorizontalDivider()
                }
            }
        }

        when (list.loadState.append) {
            is LoadState.Loading -> item { LoadingScreen() }
            is LoadState.Error -> item { ErrorScreen(list::retry) }
            else -> Unit
        }
    }

    if (list.itemCount == 0) {
        when (list.loadState.refresh) {
            is LoadState.Loading -> LoadingScreen(Modifier.background(MaterialTheme.colorScheme.surface))
            is LoadState.Error -> ErrorScreen(list::retry)
            is LoadState.NotLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.text_no_messages))
            }
        }
    }
}
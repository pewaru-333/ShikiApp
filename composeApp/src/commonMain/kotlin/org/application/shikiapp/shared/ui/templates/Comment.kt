@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.utils.extensions.flattenText
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.CommentListState
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_change
import shikiapp.composeapp.generated.resources.text_comments
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_delete
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_editing
import shikiapp.composeapp.generated.resources.text_empty
import shikiapp.composeapp.generated.resources.text_enter_message
import shikiapp.composeapp.generated.resources.text_error_comment_create
import shikiapp.composeapp.generated.resources.text_offtopic
import shikiapp.composeapp.generated.resources.text_sure_to_delete_comment
import shikiapp.composeapp.generated.resources.vector_check
import shikiapp.composeapp.generated.resources.vector_close
import shikiapp.composeapp.generated.resources.vector_edit
import shikiapp.composeapp.generated.resources.vector_refresh
import shikiapp.composeapp.generated.resources.vector_send
import shikiapp.composeapp.generated.resources.vector_trash

@Composable
private fun Comment(
    comment: Comment,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val density = LocalDensity.current

    var showMenu by remember { mutableStateOf(false) }
    var touchOffset by remember { mutableStateOf(DpOffset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(false, PointerEventPass.Initial)

                    touchOffset = with(density) {
                        DpOffset(down.position.x.toDp(), down.position.y.toDp())
                    }

                    waitForUpOrCancellation(PointerEventPass.Initial)
                }
            }
            .combinedClickable(
                enabled = comment.canBeEdited,
                onLongClick = { showMenu = true },
                onClick = {}
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .combinedClickable(
                        onClick = { onNavigate(Screen.User(comment.userId)) },
                        onLongClick = {
                            if (comment.canBeEdited) {
                                showMenu = true
                            }
                        }
                    )
                    .padding(vertical = 4.dp)
            ) {
                CircleContentImage(comment.userAvatar, Modifier.size(40.dp))

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = comment.userNickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                    Text(
                        text = comment.createdAt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (comment.isOfftopic) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = stringResource(Res.string.text_offtopic),
                                modifier = Modifier.padding(6.dp, 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }

                    comment.type?.let {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(6.dp, 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            HtmlContent(comment.commentContent)
        }

        Box(Modifier.size(0.dp)) { // без этого иногда меню появляется где угодно
            DropdownMenuPopup(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = touchOffset,
                content = {
                    DropdownMenuGroup(
                        shapes = MenuDefaults.groupShape(0, 1),
                        containerColor = MenuDefaults.groupVibrantContainerColor
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.text_change)) },
                            trailingIcon = { VectorIcon(Res.drawable.vector_edit) },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.text_delete)) },
                            trailingIcon = { VectorIcon(Res.drawable.vector_trash) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun Comments(
    state: CommentListState,
    list: LazyPagingItems<Comment> = state.comments,
    isVisible: Boolean,
    isSending: Boolean,
    canSend: Boolean = true,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onCreateComment: (String, Boolean) -> Unit,
    onUpdateComment: (Long, String, Boolean) -> Unit,
    onDeleteComment: (Long) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it }
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onHide
    )

    var isOfftopic by remember { mutableStateOf(false) }
    var editComment by remember { mutableStateOf<Comment?>(null) }
    var deleteComment by remember { mutableStateOf<Comment?>(null) }

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(enabled = false) {}
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.text_comments)) },
                    navigationIcon = { NavigationIcon(onHide) },
                    actions = { IconButton(state::refresh) { VectorIcon(Res.drawable.vector_refresh) } }
                )
            }
        ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .imePadding()
            ) {
                Box(Modifier.weight(1f)) {
                    LazyColumn(
                        state = state.listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        reverseLayout = true
                    ) {
                        items(list.itemCount, list.itemKey(Comment::id)) { index ->
                            list[index]?.let { comment ->
                                Comment(
                                    comment = comment,
                                    onNavigate = onNavigate,
                                    onEditClick = {
                                        editComment = comment
                                        isOfftopic = comment.isOfftopic

                                        state.textFieldState.edit {
                                            replace(0, length, comment.commentContent?.flattenText().orEmpty())
                                        }
                                        state.focusRequester.requestFocus()
                                    },
                                    onDeleteClick = {
                                        deleteComment = comment
                                    }
                                )

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

                    if (list.loadState.refresh is LoadState.Loading && list.itemCount == 0) {
                        LoadingScreen(Modifier.background(MaterialTheme.colorScheme.surface))
                    } else if (list.loadState.refresh is LoadState.Error && list.itemCount == 0) {
                        ErrorScreen(list::retry)
                    } else if (list.itemCount == 0) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(stringResource(Res.string.text_empty))
                        }
                    }
                }

                if (list.loadState.refresh !is LoadState.Error && Preferences.token != null && canSend) {
                    HorizontalDivider()

                    Column(Modifier.fillMaxWidth()) {
                        AnimatedVisibility(state.errorTrigger > 0L) {
                            var progress by remember { mutableFloatStateOf(1f) }

                            LaunchedEffect(state.errorTrigger) {
                                if (state.errorTrigger > 0L) {
                                    progress = 0f
                                    animate(
                                        initialValue = 0f,
                                        targetValue = 1f,
                                        animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
                                        block = { value, _ -> progress = value }
                                    )
                                    state.hideError()
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 8.dp)
                                ) {
                                    VectorIcon(
                                        resId = Res.drawable.vector_close,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = stringResource(Res.string.text_error_comment_create),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    trackColor = MaterialTheme.colorScheme.onError
                                )
                            }
                        }

                        AnimatedVisibility(editComment != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp, 8.dp)
                            ) {
                                VectorIcon(
                                    resId = Res.drawable.vector_edit,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(Res.string.text_editing),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = editComment?.commentContent?.flattenText().orEmpty(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    content = { VectorIcon(Res.drawable.vector_close) },
                                    onClick = {
                                        editComment = null
                                        isOfftopic = false
                                        state.textFieldState.clearText()
                                    }
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            OutlinedTextField(
                                state = state.textFieldState,
                                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                                placeholder = { Text(stringResource(Res.string.text_enter_message)) },
                                trailingIcon = {
                                    FilterChip(
                                        modifier = Modifier.padding(end = 4.dp),
                                        selected = isOfftopic,
                                        onClick = { isOfftopic = !isOfftopic },
                                        label = { Text(stringResource(Res.string.text_offtopic)) }
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Default
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(state.focusRequester)
                            )

                            Spacer(Modifier.width(8.dp))

                            FilledTonalIconButton(
                                enabled = state.textFieldState.text.isNotBlank() && !isSending,
                                onClick = {
                                    val text = state.textFieldState.text.toString()

                                    editComment?.let {
                                        onUpdateComment(it.id, text, isOfftopic != it.isOfftopic)
                                    } ?: onCreateComment(text, isOfftopic)

                                    state.textFieldState.clearText()
                                    editComment = null
                                    isOfftopic = false
                                },
                                content = {
                                    if (isSending) {
                                        CircularProgressIndicator(Modifier.size(24.dp))
                                    } else {
                                        VectorIcon(
                                            resId = if (editComment != null) Res.drawable.vector_check
                                            else Res.drawable.vector_send
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    deleteComment?.let {
        AlertDialog(
            onDismissRequest = { deleteComment = null },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(Res.string.text_sure_to_delete_comment))
                    Text(
                        maxLines = 5,
                        text = it.commentContent?.flattenText().orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    content = { Text(stringResource(Res.string.text_dismiss)) },
                    onClick = { deleteComment = null }
                )
            },
            confirmButton = {
                TextButton(
                    content = { Text(stringResource(Res.string.text_confirm)) },
                    onClick = {
                        onDeleteComment(it.id)
                        deleteComment = null

                        editComment?.let { oldComment ->
                            if (oldComment.id == it.id) {
                                editComment = null
                                isOfftopic = false
                                state.textFieldState.clearText()
                            }
                        }
                    }
                )
            }
        )
    }
}
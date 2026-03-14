@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.utils.navigation.Screen
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_comments
import shikiapp.composeapp.generated.resources.text_offtopic

@Composable
fun Comment(comment: Comment, onNavigate: (Screen) -> Unit) =
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
                .clickable { onNavigate(Screen.User(comment.userId)) }
                .padding(vertical = 4.dp)
        ) {
            AnimatedAsyncImage(
                model = comment.userAvatar,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )

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
        }

        Spacer(Modifier.height(4.dp))

        HtmlContent(comment.commentContent)
    }

@Composable
fun Comments(
    list: LazyPagingItems<Comment>,
    isVisible: Boolean,
    listState: LazyListState,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onHide
    )
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.text_comments)) },
                    navigationIcon = { NavigationIcon(onHide) }
                )
            }
        ) { values ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    reverseLayout = true
                ) {
                    items(list.itemCount, list.itemKey(Comment::id)) { index ->
                        list[index]?.let {
                            Comment(it, onNavigate)

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
                }
            }
        }
    }
}
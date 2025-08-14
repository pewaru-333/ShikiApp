package org.application.shikiapp.ui.templates

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun Comment(comment: Comment, onNavigate: (Screen) -> Unit) =
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(Screen.User(comment.userId)) }
        ) {
            AsyncImage(
                model = comment.user.image.x160,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = comment.user.nickname,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = convertDate(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        HtmlComment(comment.htmlBody.trimIndent())
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    list: LazyPagingItems<Comment>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_comments)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = values,
            reverseLayout = true
        ) {
            when (list.loadState.refresh) {
                is LoadState.Error -> item { ErrorScreen(list::retry) }
                LoadState.Loading -> item { LoadingScreen() }
                is LoadState.NotLoading -> items(list.itemCount, list.itemKey(Comment::id)) { index ->
                    list[index]?.let {
                        Comment(it, onNavigate)

                        if (index < list.itemCount - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
            if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}
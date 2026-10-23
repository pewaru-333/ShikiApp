package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.shared.models.ui.list.News
import org.application.shikiapp.shared.models.viewModels.NewsViewModel
import org.application.shikiapp.shared.ui.templates.*
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_empty
import shikiapp.composeapp.generated.resources.text_error_loading
import shikiapp.composeapp.generated.resources.text_try_again

@Composable
fun NewsScreen(onNavigate: (Screen) -> Unit) {
    val viewModel = viewModel { NewsViewModel() }
    val list = viewModel.newsList.collectAsLazyPagingItems()

    val isCompact = rememberWindowSize().isCompact
    val snackbarState = remember(::SnackbarHostState)

    val errorMessage = stringResource(Res.string.text_error_loading)
    val retryLabel = stringResource(Res.string.text_try_again)

    var isManual by remember { mutableStateOf(false) }
    val isInitial by remember(list) {
        derivedStateOf {
            list.loadState.refresh is LoadState.Loading && list.itemCount == 0
        }
    }
    val onRefresh = remember(list) {
        {
            isManual = true
            list.refresh()
        }
    }

    LaunchedEffect(list, list.loadState.refresh) {
        val refresh = list.loadState.refresh
        if (refresh !is LoadState.Loading) {
            isManual = false
        }

        if (refresh is LoadState.Error && list.itemCount > 0) {
            val result = snackbarState.showSnackbar(
                message = errorMessage,
                actionLabel = retryLabel,
                withDismissAction = true
            )

            if (result == SnackbarResult.ActionPerformed) {
                list.retry()
            }
        }
    }

    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        PullToRefreshBox(isManual, onRefresh, Modifier.fillMaxSize()) {
            when {
                isInitial -> {
                    if (isCompact) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            userScrollEnabled = false,
                            content = { items(5) { NewsCardPlaceholder() } }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(320.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            userScrollEnabled = false,
                            content = { items(15) { NewsCardPlaceholder() } }
                        )
                    }
                }

                list.loadState.refresh is LoadState.Error && list.itemCount == 0 -> {
                    ErrorScreen(list::retry)
                }

                list.itemCount == 0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = { item { Text(stringResource(Res.string.text_empty)) } }
                    )
                }

                else -> {
                    if (isCompact) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(list.itemCount, list.itemKey(News::id)) { index ->
                                list[index]?.let { NewsCard(it, onNavigate) }
                            }

                            when (list.loadState.append) {
                                is LoadState.Loading -> item { LoadingScreen() }
                                is LoadState.Error -> item { ErrorScreen(list::retry) }
                                else -> Unit
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 320.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            items(list.itemCount, list.itemKey(News::id)) { index ->
                                list[index]?.let { NewsCard(it, onNavigate) }
                            }

                            when (list.loadState.append) {
                                is LoadState.Loading -> item(
                                    span = { GridItemSpan(maxLineSpan) },
                                    content = { LoadingScreen(Modifier.padding(16.dp)) }
                                )

                                is LoadState.Error -> item(
                                    span = { GridItemSpan(maxLineSpan) },
                                    content = { ErrorScreen(list::retry) }
                                )

                                else -> Unit
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun NewsCard(news: News, onNavigate: (Screen) -> Unit) =
    Card(
        onClick = { onNavigate(Screen.NewsDetail(news.id)) },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedAsyncImage(
            model = news.poster,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        )

        Column(Modifier.padding(12.dp, 8.dp)) {
            Text(
                text = news.title,
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                maxLines = 2,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = news.date,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = news.author,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VectorIcon(
                        imageVector = Icons.Comments,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = news.commentsCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }


@Composable
private fun NewsCardPlaceholder() {
    val titleLineHeight = with(LocalDensity.current) { 22.sp.toDp() }
    val expectedHeight = with(LocalDensity.current) {
        MaterialTheme.typography.labelMedium.lineHeight.toDp()
    }.coerceAtLeast(16.dp)

    Card(Modifier.fillMaxWidth(), MaterialTheme.shapes.medium) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .then(loadingEffectModifier())
        )
        Column(Modifier.padding(12.dp, 8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(titleLineHeight)
                    .padding(vertical = 2.dp)
                    .then(loadingEffectModifier())
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(titleLineHeight)
                    .padding(vertical = 2.dp)
                    .then(loadingEffectModifier())
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(expectedHeight)
                    .then(loadingEffectModifier())
            )
        }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.window.core.layout.WindowSizeClass
import org.application.shikiapp.shared.models.ui.list.News
import org.application.shikiapp.shared.models.viewModels.NewsViewModel
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.ErrorScreen
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.rememberLoadingEffect
import org.application.shikiapp.shared.utils.navigation.Screen

@Composable
fun NewsScreen(onNavigate: (Screen) -> Unit) {
    val newsViewModel = viewModel { NewsViewModel() }
    val list = newsViewModel.newsList.collectAsLazyPagingItems()

    val isRefreshing by remember {
        derivedStateOf { list.loadState.refresh is LoadState.Loading }
    }

    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = !windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    PullToRefreshBox(isRefreshing, list::refresh, Modifier.fillMaxSize()) {
        when {
            isRefreshing && list.itemCount == 0 -> {
                if (isCompact) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        userScrollEnabled = false,
                        content = { items(7) { NewsCardPlaceholder() } }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(320.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        userScrollEnabled = false,
                        content = { items(12) { NewsCardPlaceholder() } }
                    )
                }
            }

            list.loadState.refresh is LoadState.Error && list.itemCount == 0 -> {
                ErrorScreen(list::retry)
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
                            is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                                LoadingScreen(Modifier.padding(16.dp))
                            }

                            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                                ErrorScreen(list::retry)
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsCard(news: News, onNavigate: (Screen) -> Unit) =
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onNavigate(Screen.NewsDetail(news.id)) },
    ) {
        Column {
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
                }
            }
        }
    }


@Composable
private fun NewsCardPlaceholder() = Card(Modifier.fillMaxWidth(), MaterialTheme.shapes.medium) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(rememberLoadingEffect())
        )
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(20.dp)
                    .background(rememberLoadingEffect(), MaterialTheme.shapes.small)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .background(rememberLoadingEffect(), MaterialTheme.shapes.small)
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .background(rememberLoadingEffect(), MaterialTheme.shapes.small)
            )
        }
    }
}
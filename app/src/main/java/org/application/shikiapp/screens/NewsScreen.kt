@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.application.shikiapp.models.ui.list.News
import org.application.shikiapp.models.viewModels.NewsViewModel
import org.application.shikiapp.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.rememberLoadingEffect
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(onNavigate: (Screen) -> Unit) {
    val newsViewModel = viewModel<NewsViewModel>()
    val list = newsViewModel.newsList.collectAsLazyPagingItems()

    val isRefreshing by remember {
        derivedStateOf { list.loadState.refresh is LoadState.Loading }
    }

    PullToRefreshBox(isRefreshing, list::refresh, Modifier.fillMaxSize()) {
        when {
            isRefreshing && list.itemCount == 0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false
                ) {
                    items(7) {
                        NewsCardPlaceholder()
                    }
                }
            }

            list.loadState.refresh is LoadState.Error && list.itemCount == 0 -> {
                ErrorScreen(list::retry)
            }

            else -> {
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
            }
        }
    }
}

@Composable
private fun NewsCard(news: News, onNavigate: (Screen) -> Unit) =
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigate(Screen.NewsDetail(news.id)) }
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
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${news.date} · ${news.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }


@Composable
private fun NewsCardPlaceholder() = Card(Modifier.fillMaxWidth()) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .background(rememberLoadingEffect())
        )
        Column(Modifier.padding(12.dp, 8.dp)) {
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

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .background(rememberLoadingEffect(), MaterialTheme.shapes.small)
            )
        }
    }
}
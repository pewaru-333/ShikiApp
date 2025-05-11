@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import org.application.shikiapp.models.ui.list.News
import org.application.shikiapp.models.viewModels.NewsViewModel
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun NewsScreen(onNavigate: (Screen) -> Unit) {
    val scope = rememberCoroutineScope()
    val pullState = rememberPullToRefreshState()

    val news = viewModel<NewsViewModel>()
    val list = news.newsList.collectAsLazyPagingItems()

    var isRefreshing by remember { mutableStateOf(false) }

    val onRefresh = {
        list.refresh().also {
            isRefreshing = false
            scope.launch {
                pullState.animateToHidden()
            }
        }
    }

    PullToRefreshBox(isRefreshing, onRefresh, Modifier, pullState) {
        when (list.loadState.refresh) {
            is LoadState.Error -> ErrorScreen(list::retry)
            is LoadState.Loading -> LoadingScreen()
            is LoadState.NotLoading ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(list.itemCount, list.itemKey(News::id)) {
                        list[it]?.let { NewsCard(it, onNavigate) }
                    }
                    if (list.loadState.append == LoadState.Loading) {
                        item {
                            LoadingScreen()
                        }
                    }
                    if (list.loadState.hasError) {
                        item {
                            ErrorScreen(list::retry)
                        }
                    }
                }
        }
    }
}

@Composable
private fun NewsCard(news: News, onNavigate: (Screen) -> Unit) =
    ElevatedCard(
        onClick = { onNavigate(Screen.NewsDetail(news.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedAsyncImage(
            model = news.poster,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(MaterialTheme.shapes.large)
        )

        Text(
            text = news.title,
            modifier = Modifier.padding(8.dp),
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "${news.date} · ${news.author}",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.W500)
        )
    }
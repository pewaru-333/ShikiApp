package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.views.NewsViewModel
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getPoster

@Composable
fun NewsScreen(toDetail: (Long) -> Unit) {
    val news = viewModel<NewsViewModel>()
    val list = news.newsList.collectAsLazyPagingItems()

    when (list.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(list::retry)
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(list.itemCount) { NewsCard(list[it]!!, toDetail) }
            if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

@Composable
private fun NewsCard(news: News, toDetail: (Long) -> Unit) =
    ElevatedCard(
        onClick = { toDetail(news.id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = getPoster(news.htmlFooter),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(MaterialTheme.shapes.large)
        )
        Text(
            text = news.topicTitle,
            modifier = Modifier.padding(8.dp),
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "${convertDate(news.createdAt)} · ${news.user.nickname}",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
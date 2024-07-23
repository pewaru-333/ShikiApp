package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.NewsDetailDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.data.News
import org.application.shikiapp.models.views.NewsViewModel
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getPoster

@Destination<RootGraph>(start = true)
@Composable
fun NewsScreen(navigator: DestinationsNavigator) {
    val news = viewModel<NewsViewModel>().newsList.collectAsLazyPagingItems()

    when (news.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(news::retry)
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(news.itemCount) { NewsCard(news[it]!!, navigator) }
            if (news.loadState.append == LoadState.Loading) item { LoadingScreen() }
            if (news.loadState.hasError) item { ErrorScreen(news::retry) }
        }
    }
}

@Composable
private fun NewsCard(news: News, navigator: DestinationsNavigator) {
    ElevatedCard(Modifier
        .fillMaxWidth()
        .clickable { navigator.navigate(NewsDetailDestination(news.id)) }
    ) {
        AsyncImage(
            model = getPoster(news.htmlFooter),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(MaterialTheme.shapes.large),
            error = painterResource(R.drawable.vector_home),
            fallback = painterResource(R.drawable.vector_home),
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
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
}
@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.NewsDetailState
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.models.viewModels.NewsDetailViewModel
import org.application.shikiapp.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.DialogScreenshot
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun NewsDetail(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<NewsDetailViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    AnimatedScreen(response, model::loadData) { newsDetail ->
        NewsDetailView(newsDetail, state, model::onEvent, onNavigate, back)
    }
}

@Composable
fun NewsDetailView(
    news: NewsDetail,
    state: NewsDetailState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val handler = LocalUriHandler.current
    val listState = rememberLazyListState()
    val comments = news.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_news_one)) },
                navigationIcon = { NavigationIcon(back) },
                actions = { IconComment(comments) { onEvent(ContentDetailEvent.ShowComments) } }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                AnimatedAsyncImage(
                    model = news.poster,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            item {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(news.userNickname) },
                    modifier = Modifier.offset(x = (-8).dp),
                    supportingContent = { Text(news.date) },
                    leadingContent = {
                        AsyncImage(
                            model = news.userImage,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.High,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable { onNavigate(Screen.User(news.userId)) }
                        )
                    }
                )
            }

            item {
                Text(
                    text = news.newsBody,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                LazyRow(horizontalArrangement = spacedBy(12.dp)) {
                    news.videos.let {
                        if (it.size >= 2) item {
                            AnimatedAsyncImage(
                                model = it[1],
                                modifier = Modifier
                                    .size(172.dp, 130.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { handler.openUri(it[0]) }
                            )
                        }
                    }

                    itemsIndexed(news.images) { index, image ->
                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(172.dp, 130.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onEvent(ContentDetailEvent.Media.ShowImage(index)) }
                        )
                    }
                }
            }
        }
    }

    Comments(
        list = comments,
        listState = listState,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogScreenshot(
        list = news.images,
        screenshot = state.image,
        visible = state.showImage,
        hide = { onEvent(ContentDetailEvent.Media.ShowImage()) }
    )
}
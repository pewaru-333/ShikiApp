@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
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
import org.application.shikiapp.utils.CommentContent
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun NewsDetail(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val model = viewModel<NewsDetailViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    AnimatedScreen(response, model::loadData) { newsDetail ->
        NewsDetailView(newsDetail, state, model::onEvent, onNavigate, onBack)
    }
}

@Composable
fun NewsDetailView(
    news: NewsDetail,
    state: NewsDetailState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val commentsListState = rememberLazyListState()
    val comments = news.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_news_one)) },
                navigationIcon = { NavigationIcon(onBack) },
                actions = {
                    IconComment(
                        onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount },
                        onEvent = { onEvent(ContentDetailEvent.ShowComments) }
                    )
                }
            )
        }
    ) { values ->
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = values) {
            item {
                when (val poster = news.poster) {
                    is CommentContent.VideoContent -> Box(Modifier.padding(8.dp)) {
                        HtmlComment(listOf(poster))
                    }

                    else -> {
                        AnimatedAsyncImage(
                            model = if (poster is CommentContent.ImageContent) poster.fullUrl else poster,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .aspectRatio(16f / 9f)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onEvent(ContentDetailEvent.Media.ShowImage()) }
                        )
                    }
                }
            }

            item {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.clickable { onNavigate(Screen.User(news.userId)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedAsyncImage(
                            model = news.userImage,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(
                                text = news.userNickname,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = news.date,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = news.newsBody,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        )
                    )
                }
            }

            if (news.videos.isNotEmpty() || news.images.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(16.dp, 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(news.videos) {
                            Box(Modifier.size(200.dp, 120.dp)) {
                                HtmlComment(news.videos)
                            }
                        }
                        itemsIndexed(news.images) { index, image ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(200.dp, 120.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { onEvent(ContentDetailEvent.Media.ShowImage(index)) }
                            ) {
                                AnimatedAsyncImage(
                                    model = image,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Comments(
        list = comments,
        listState = commentsListState,
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
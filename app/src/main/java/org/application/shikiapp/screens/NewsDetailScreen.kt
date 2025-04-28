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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.NewsDetailEvent
import org.application.shikiapp.models.states.NewsDetailState
import org.application.shikiapp.models.ui.NewsDetail
import org.application.shikiapp.models.viewModels.NewsDetailViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetail(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<NewsDetailViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is Response.Error -> ErrorScreen(model::loadData)
        is Response.Loading -> LoadingScreen()
        is Response.Success -> NewsDetailView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailView(
    news: NewsDetail,
    state: NewsDetailState,
    onEvent: (NewsDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val handler = LocalUriHandler.current
    val comments = news.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_news_one)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (news.commentsCount > 0)
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.ShowComments) }
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                AsyncImage(
                    model = news.poster,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    filterQuality = FilterQuality.High,
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
                        if (it.isNotEmpty()) item {
                            AsyncImage(
                                model = it[1],
                                contentDescription = null,
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
                                .clickable { onEvent(NewsDetailEvent.ShowImage(index)) }
                        )
                    }
                }
            }
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    DialogScreenshot(
        list = news.images,
        screenshot = state.image,
        visible = state.showImage,
        setScreenshot = { onEvent(NewsDetailEvent.SetImage(it)) },
        hide = { onEvent(NewsDetailEvent.ShowImage()) }
    )
}
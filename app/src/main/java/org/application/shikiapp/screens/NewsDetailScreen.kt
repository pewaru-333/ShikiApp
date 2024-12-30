package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_image_of
import org.application.shikiapp.models.views.NewsDetailState
import org.application.shikiapp.models.views.NewsDetailViewModel
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Error
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Loading
import org.application.shikiapp.models.views.NewsDetailViewModel.Response.Success
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getLinks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetail(toUser: (Long) -> Unit, back: () -> Unit) {
    val model = viewModel<NewsDetailViewModel>()
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        is Error -> ErrorScreen(model::getNews)
        is Loading -> LoadingScreen()
        is Success -> {
            val state by model.state.collectAsStateWithLifecycle()
            val comments = data.comments.collectAsLazyPagingItems()
            val news = data.news
            val links = getLinks(news.htmlFooter)
            val video = links.filter { it.contains("youtu") }
            val images = links.filter { it.contains("original") }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.text_news_one)) },
                        navigationIcon = { NavigationIcon(back) },
                        actions = {
                            if (news.commentsCount > 0) IconButton(model::showComments) {
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
                            model = links.find { it.contains(".jpg") },
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.FillWidth,
                            filterQuality = FilterQuality.High
                        )
                    }

                    item {
                        Text(
                            text = news.topicTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = { Text(news.user.nickname) },
                            modifier = Modifier.offset(x = (-8).dp),
                            supportingContent = { Text(convertDate(news.createdAt)) },
                            leadingContent = {
                                AsyncImage(
                                    model = news.user.image.x160,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .clickable { toUser(news.user.id) },
                                    contentScale = ContentScale.Crop,
                                    filterQuality = FilterQuality.High
                                )
                            }
                        )
                    }

                    item { Text(fromHtml(news.htmlBody), style = MaterialTheme.typography.bodyLarge) }

                    item {
                        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
                            if(video.isNotEmpty()) {
                                val handler = LocalUriHandler.current
                                AsyncImage(
                                    model = video[1],
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(172.dp, 130.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable { handler.openUri(video[0]) }
                                )
                            }

                            images.forEachIndexed { index, image ->
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(172.dp, 130.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable { model.showImage(index) }
                                )
                            }
                        }
                    }
                }
            }

            when {
                state.showComments -> Comments(model::hideComments, comments, toUser)
                state.showImage -> DialogImage(model, state, images)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogImage(model: NewsDetailViewModel, state: NewsDetailState, list: List<String>) {
    val pagerState = rememberPagerState(state.image) { list.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest(model::setImage)
    }

    Dialog(model::hideImage, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_image_of, state.image + 1, list.size)) },
                    navigationIcon = { NavigationIcon(model::hideImage) }
                )
            }
        ) { values ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = values
            ) { Box(Modifier.fillMaxSize(), Alignment.Center) { AsyncImage(list[it], null) } }
        }
    }
}
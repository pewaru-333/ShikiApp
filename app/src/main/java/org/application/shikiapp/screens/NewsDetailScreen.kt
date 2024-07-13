package org.application.shikiapp.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.NewsDetailViewModel
import org.application.shikiapp.models.views.NewsState
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getLinks

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun NewsDetail(newsId: Long, navigator: DestinationsNavigator) {
    val model = viewModel<NewsDetailViewModel>(factory = factory { NewsDetailViewModel(newsId) })
    val state by model.state.collectAsStateWithLifecycle()

    when (val response = state) {
        is NewsState.Error -> ErrorScreen(model.getNews())
        is NewsState.Loading -> LoadingScreen()
        is NewsState.Success -> {
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(newsId)
            }).comments.collectAsLazyPagingItems()
            val news = response.news
            val poster = getLinks(news.htmlFooter).find { it.contains(".jpg") }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.text_news_one)) },
                        navigationIcon = { NavigationIcon(navigator::popBackStack) }
                    )
                }
            ) { values ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = poster,
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
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    filterQuality = FilterQuality.High
                                )
                            }
                        )
                    }

                    item {
                        Text(
                            text = fromHtml(news.htmlBody),
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                    }

                    item {
                        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(16.dp)) {
                            fromHtml(news.htmlFooter).getLinkAnnotations(0, news.htmlFooter.length)
                                .forEach { link ->
                                    val url = link.item.toString().substringAfterLast("=")
                                        .substringBeforeLast(")")
                                    if (!url.contains(".be"))
//                                    AsyncImage(
//                                        model = url,
//                                        contentDescription = null,
//                                        modifier = Modifier.size(160.dp, 75.dp),
//                                        imageLoader = LocalContext.current.imageLoader.newBuilder()
//                                            .components { add(VideoFrameDecoder.Factory()) }.build(),
//                                        contentScale = ContentScale.Inside,
//                                        fallback = painterResource(id = R.drawable.vector_home),
//                                         error = painterResource(id = R.drawable.vector_home)
//                                    )
//                                else
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            modifier = Modifier.size(236.dp, 350.dp),
                                            contentScale = ContentScale.FillBounds
                                        )
                                }
                        }
                    }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }
        }
    }
}
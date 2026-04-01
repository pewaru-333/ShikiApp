@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.NewsDetailState
import org.application.shikiapp.shared.models.ui.NewsDetail
import org.application.shikiapp.shared.models.viewModels.NewsDetailViewModel
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogImages
import org.application.shikiapp.shared.ui.templates.HtmlContent
import org.application.shikiapp.shared.ui.templates.IconComment
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_news_one

@Composable
fun NewsDetail(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val model = viewModel(::NewsDetailViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    AnimatedScreen(response, model::loadData, NewsDetail::comments) { newsDetail, comments ->
        NewsDetailView(newsDetail, state, model::onEvent, onNavigate, onBack)

        val commentListState = rememberCommentListState(
            list = comments,
            onCommentEvent = model.commentEvent
        )
        Comments(
            state = commentListState,
            isVisible = state.dialogState is BaseDialogState.Comments,
            isSending = state.isSendingComment,
            onNavigate = onNavigate,
            onHide = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) },
            onSendComment = { text, isOfftopic ->
                model.onEvent(ContentDetailEvent.SendComment(text, isOfftopic))
            }
        )
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
    val isExpanded = !rememberWindowSize().isCompact

    val isPosterImage = remember(news.poster) { news.poster is CommentContent.ImageContent }

    val titleUser = @Composable {
        Column(Modifier.padding(bottom = 16.dp)) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .padding(vertical = 4.dp)
                    .clickable { onNavigate(Screen.User(news.userId)) }
                    .padding(top = 4.dp, end = 16.dp, bottom = 4.dp)
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
        }
    }

    val body = @Composable {
        SelectionContainer {
            Text(
                text = news.newsBody,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp
                )
            )
        }
    }

    val poster = @Composable {
        when (val poster = news.poster) {
            is CommentContent.VideoContent -> Box(Modifier.padding(bottom = 16.dp)) {
                HtmlContent(listOf(poster))
            }

            else -> {
                AnimatedAsyncImage(
                    model = if (poster is CommentContent.ImageContent) poster.fullUrl else poster,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Image())) }
                )
            }
        }
    }

    val media = @Composable {
        if (news.videos.isNotEmpty() || news.images.isNotEmpty()) {
            if (isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    news.videos.forEach { video ->
                        Box(
                            content = { HtmlContent(listOf(video)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    news.images.forEachIndexed { index, image ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Image(index))) }
                        ) {
                            AnimatedAsyncImage(
                                model = image,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(news.videos) {
                        Box(
                            content = { HtmlContent(listOf(it)) },
                            modifier = Modifier
                                .size(200.dp, 120.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    itemsIndexed(news.images) { index, image ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(200.dp, 120.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Image(if (isPosterImage) index + 1 else index))) }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.text_news_one)) },
                navigationIcon = { NavigationIcon(onBack) },
                actions = { IconComment { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Comments)) } }
            )
        }
    ) { values ->
        if (isExpanded) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .padding(horizontal = 24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(0.6f),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item { titleUser() }
                    item { body() }
                }
                LazyColumn(
                    modifier = Modifier.weight(0.4f),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item { poster() }
                    item { media() }
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 840.dp)
                ) {
                    item { poster() }
                    item { titleUser() }
                    item { body() }
                    item { media() }
                }
            }
        }
    }

    DialogImages(
        images = if (news.poster is CommentContent.ImageContent) listOf(news.poster.fullUrl ?: news.poster.previewUrl) + news.images
        else news.images,
        initialIndex = state.image,
        isVisible = state.dialogState is BaseDialogState.Media.Image,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )
}
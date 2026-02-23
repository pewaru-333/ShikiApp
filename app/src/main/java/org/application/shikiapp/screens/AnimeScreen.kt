@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.states.showSheetContent
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.Video
import org.application.shikiapp.models.viewModels.AnimeViewModel
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.DialogEditRate
import org.application.shikiapp.ui.templates.DialogPoster
import org.application.shikiapp.ui.templates.DialogScreenshot
import org.application.shikiapp.ui.templates.LinksSheet
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScaffoldContent
import org.application.shikiapp.ui.templates.SheetColumn
import org.application.shikiapp.ui.templates.SimilarFull
import org.application.shikiapp.ui.templates.Statistics
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.ui.templates.description
import org.application.shikiapp.ui.templates.genres
import org.application.shikiapp.ui.templates.info
import org.application.shikiapp.ui.templates.profiles
import org.application.shikiapp.ui.templates.related
import org.application.shikiapp.ui.templates.summary
import org.application.shikiapp.ui.templates.title
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.VideoKind
import org.application.shikiapp.utils.extensions.openLinkInBrowser
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun AnimeScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<AnimeViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LaunchedEffect(model.openLink) {
        model.openLink.collectLatest {
            context.openLinkInBrowser((response as Success).data.url)
        }
    }

    AnimatedScreen(response, model::loadData) { anime ->
        AnimeView(anime, state, model::onEvent, onNavigate, back)
    }
}

@Composable
private fun AnimeView(
    anime: Anime,
    state: AnimeState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val rateModel = viewModel<UserRateViewModel>()
    val rate = anime.userRate.getValue()
    val newRate by rateModel.newRate.collectAsStateWithLifecycle()

    val commentsState = rememberLazyListState()
    val authorsState = rememberLazyListState()
    val charactersState = rememberLazyListState()
    val similarState = rememberLazyListState()

    val comments = anime.comments.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        rate?.let { rateModel.getRate(it, LinkedType.ANIME) }
    }

    ScaffoldContent(
        title = { Text(stringResource(R.string.text_anime)) },
        userRate = anime.userRate,
        isFavoured = anime.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Media.Anime.ToggleFavourite) },
        onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount }
    ) {
        title(anime.title)
        info(
            poster = anime.poster,
            kind = anime.kind,
            score = anime.score,
            status = anime.status,
            airedOn = anime.airedOn,
            releasedOn = anime.releasedOn,
            episodes = anime.episodes,
            origin = anime.origin,
            rating = anime.rating,
            onOpenFullscreenPoster = { onEvent(ContentDetailEvent.Media.ShowPoster) }
        )

        genres(anime.genres)
        summary(
            similar = anime.similar,
            studio = anime.studio,
            duration = anime.duration,
            nextEpisodeAt = anime.nextEpisodeAt,
            onEvent = onEvent,
            onNavigate = onNavigate
        )

        description(anime.description)
        related(
            related = anime.related,
            onShow = { onEvent(ContentDetailEvent.Media.ShowRelated) },
            onNavigate = onNavigate
        )
        profiles(
            profiles = anime.charactersMain,
            title = R.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = anime.personMain,
            title = R.string.text_authors,
            onShow = { onEvent(ContentDetailEvent.Media.ShowAuthors) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )

        anime.screenshots.let { list ->
            if (list.isNotEmpty()) {
                item {
                    Screenshots(
                        list = list,
                        onShow = { onEvent(ContentDetailEvent.Media.ShowImage(it)) },
                        onHide = { onEvent(ContentDetailEvent.Media.Anime.ShowScreenshots) },
                    )
                }
            }
        }
        anime.video.let { list ->
            if (list.isNotEmpty()) {
                item {
                    Video(list) { onEvent(ContentDetailEvent.Media.Anime.ShowVideo) }
                }
            }
        }
    }

    Comments(
        list = comments,
        listState = commentsState,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = anime.related,
        chronology = anime.chronology,
        franchise = anime.franchiseList,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    SimilarFull(
        list = anime.similar,
        listState = similarState,
        visible = state.showSimilar,
        onNavigate = { onNavigate(Screen.Anime(it)) },
        hide = { onEvent(ContentDetailEvent.Media.ShowSimilar) }
    )

    Statistics(
        statistics = anime.stats,
        visible = state.showStats,
        hide = { onEvent(ContentDetailEvent.Media.ShowStats) },
    )

    ProfilesFull(
        list = if (state.showCharacters) anime.charactersAll else anime.personAll,
        visible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) R.string.text_characters else R.string.text_authors),
        state = if (state.showCharacters) charactersState else authorsState,
        onHide = {
            onEvent(
                if (state.showCharacters) ContentDetailEvent.Media.ShowCharacters
                else ContentDetailEvent.Media.ShowAuthors
            )
        },
        onNavigate = {
            onNavigate(
                if (state.showCharacters) Screen.Character(it)
                else Screen.Person(it.toLong())
            )
        }
    )

    Screenshots(
        list = anime.screenshots,
        visible = state.showScreenshots,
        onShowScreenshot = { onEvent(ContentDetailEvent.Media.ShowImage(it)) },
        onHide = { onEvent(ContentDetailEvent.Media.Anime.ShowScreenshots) }
    )

    DialogScreenshot(
        list = anime.screenshots,
        screenshot = state.screenshot,
        visible = state.showScreenshot,
        hide = { onEvent(ContentDetailEvent.Media.ShowImage()) }
    )

    DialogPoster(
        link = anime.poster,
        isVisible = state.showPoster,
        onClose = { onEvent(ContentDetailEvent.Media.ShowPoster) }
    )

    Video(
        video = anime.videoGrouped,
        visible = state.showVideo,
        onHide = { onEvent(ContentDetailEvent.Media.Anime.ShowVideo) }
    )

    when {
        state.showRate -> DialogEditRate(
            state = newRate,
            type = LinkedType.ANIME,
            isExists = rate != null,
            onEvent = rateModel::onEvent,
            onDismiss = { onEvent(ContentDetailEvent.Media.ShowRate) },
            onCreate = { type ->
                rateModel.create(
                    id = anime.id,
                    targetType = type,
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onUpdate = {
                rateModel.update(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onDelete = {
                rateModel.delete(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            }
        )

        state.showSheet -> BottomSheet(
            url = anime.url,
            canShowLinks = anime.links.isNotEmpty(),
            onEvent = onEvent
        )

        state.showLinks -> LinksSheet(anime.links) {
            onEvent(ContentDetailEvent.Media.ShowLinks)
        }

        state.showSheetContent -> SheetColumn(
            label = stringResource(if (state.showFansubbers) R.string.text_subtitles else R.string.text_voices),
            list = if (state.showFansubbers) anime.fansubbers else anime.fandubbers,
            onHide = {
                onEvent(
                    if (state.showFansubbers) ContentDetailEvent.Media.ShowFansubbers
                    else ContentDetailEvent.Media.ShowFandubbers
                )
            }
        )
    }
}

@Composable
private fun Screenshots(list: List<String>, onShow: (Int) -> Unit, onHide: () -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(onHide) { VectorIcon(R.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            itemsIndexed(list.take(6)) { index, item ->
                AnimatedAsyncImage(
                    model = item,
                    modifier = Modifier
                        .size(172.dp, 97.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onShow(index) }
                )
            }
        }
    }

@Composable
private fun Video(list: List<Video>, onShow: () -> Unit) {
    val handler = LocalUriHandler.current

    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_video), Modifier.padding(bottom = 4.dp))
            IconButton(onShow) { VectorIcon(R.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list, Video::url) {
                AnimatedAsyncImage(
                    model = it.imageUrl,
                    modifier = Modifier
                        .size(172.dp, 130.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { handler.openUri(it.url) }
                )
            }
        }
    }
}

@Composable
private fun Screenshots(
    list: List<String>,
    visible: Boolean,
    onShowScreenshot: (Int) -> Unit,
    onHide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, onHide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_screenshots)) },
                navigationIcon = { NavigationIcon(onHide) }
            )
        }
    ) { values ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 8.dp),
            verticalArrangement = spacedBy(2.dp),
            horizontalArrangement = spacedBy(2.dp)
        ) {
            itemsIndexed(items = list, key = { _, item -> item }) { index, item ->
                AnimatedAsyncImage(
                    model = item,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clickable { onShowScreenshot(index) }
                )
            }
        }
    }
}

@Composable
private fun Video(video: Map<VideoKind, List<Video>>, visible: Boolean, onHide: () -> Unit) =
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        val handler = LocalUriHandler.current

        BackHandler(visible, onHide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_video)) },
                    navigationIcon = { NavigationIcon(onHide) }
                )
            }
        ) { values ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
                horizontalArrangement = spacedBy(8.dp),
                verticalArrangement = spacedBy(12.dp)
            ) {
                video.filterValues { it.isNotEmpty() }.forEach { (entry, values) ->
                    item(entry.name, { GridItemSpan(maxLineSpan) }) {
                        ParagraphTitle(stringResource(entry.title), Modifier.padding(bottom = 4.dp))
                    }

                    items(values, Video::url) { video ->
                        Column(verticalArrangement = spacedBy(4.dp)) {
                            AnimatedAsyncImage(
                                model = video.imageUrl,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(172f / 130f)
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { handler.openUri(video.url) }
                            )
                            Text(
                                text = video.name ?: stringResource(R.string.text_unknown),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
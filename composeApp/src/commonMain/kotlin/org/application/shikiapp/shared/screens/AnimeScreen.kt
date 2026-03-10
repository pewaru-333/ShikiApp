@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.AnimeState
import org.application.shikiapp.shared.models.states.showSheetContent
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.Video
import org.application.shikiapp.shared.models.viewModels.AnimeViewModel
import org.application.shikiapp.shared.models.viewModels.UserRateViewModel
import org.application.shikiapp.shared.network.response.Response.Success
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.BottomSheet
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogEditRate
import org.application.shikiapp.shared.ui.templates.DialogImages
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.LinkListener
import org.application.shikiapp.shared.ui.templates.LinksSheet
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.ParagraphTitle
import org.application.shikiapp.shared.ui.templates.ProfilesFull
import org.application.shikiapp.shared.ui.templates.RelatedFull
import org.application.shikiapp.shared.ui.templates.ScaffoldContent
import org.application.shikiapp.shared.ui.templates.SheetColumn
import org.application.shikiapp.shared.ui.templates.SimilarFull
import org.application.shikiapp.shared.ui.templates.Statistics
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.templates.description
import org.application.shikiapp.shared.ui.templates.genres
import org.application.shikiapp.shared.ui.templates.info
import org.application.shikiapp.shared.ui.templates.profiles
import org.application.shikiapp.shared.ui.templates.related
import org.application.shikiapp.shared.ui.templates.summary
import org.application.shikiapp.shared.ui.templates.title
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.VideoKind
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime
import shikiapp.composeapp.generated.resources.text_authors
import shikiapp.composeapp.generated.resources.text_characters
import shikiapp.composeapp.generated.resources.text_screenshots
import shikiapp.composeapp.generated.resources.text_subtitles
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.text_video
import shikiapp.composeapp.generated.resources.text_voices
import shikiapp.composeapp.generated.resources.vector_arrow_forward

@Composable
fun AnimeScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::AnimeViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

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
    val rateModel = viewModel(::UserRateViewModel)
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
        title = { Text(stringResource(Res.string.text_anime)) },
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
            title = Res.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = anime.personMain,
            title = Res.string.text_authors,
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
        isVisible = state.showComments,
        onHide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = anime.related,
        chronology = anime.chronology,
        franchise = anime.franchiseList,
        isVisible = state.showRelated,
        onHide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    SimilarFull(
        list = anime.similar,
        listState = similarState,
        isVisible = state.showSimilar,
        onNavigate = { onNavigate(Screen.Anime(it)) },
        onHide = { onEvent(ContentDetailEvent.Media.ShowSimilar) }
    )

    Statistics(
        statistics = anime.stats,
        isVisible = state.showStats,
        onHide = { onEvent(ContentDetailEvent.Media.ShowStats) },
    )

    ProfilesFull(
        list = if (state.showCharacters) anime.charactersAll else anime.personAll,
        isVisible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) Res.string.text_characters else Res.string.text_authors),
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
        isVisible = state.showScreenshots,
        onShowScreenshot = { onEvent(ContentDetailEvent.Media.ShowImage(it)) },
        onHide = { onEvent(ContentDetailEvent.Media.Anime.ShowScreenshots) }
    )

    DialogImages(
        images = anime.screenshots,
        initialIndex = state.screenshot,
        isVisible = state.showScreenshot,
        onClose = { onEvent(ContentDetailEvent.Media.ShowImage()) }
    )

    DialogPoster(
        link = anime.poster,
        isVisible = state.showPoster,
        onClose = { onEvent(ContentDetailEvent.Media.ShowPoster) }
    )

    Video(
        video = anime.videoGrouped,
        isVisible = state.showVideo,
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
            label = stringResource(if (state.showFansubbers) Res.string.text_subtitles else Res.string.text_voices),
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
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(onHide) { VectorIcon(Res.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_video), Modifier.padding(bottom = 4.dp))
            IconButton(onShow) { VectorIcon(Res.drawable.vector_arrow_forward) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
    isVisible: Boolean,
    onShowScreenshot: (Int) -> Unit,
    onHide: () -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onHide
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.text_screenshots)) },
                navigationIcon = { NavigationIcon(onHide) }
            )
        }
    ) { values ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = values.calculateTopPadding() + 8.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
private fun Video(video: Map<VideoKind, List<Video>>, isVisible: Boolean, onHide: () -> Unit) =
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        val handler = LocalUriHandler.current

        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = isVisible,
            onBackCompleted = onHide
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.text_video)) },
                    navigationIcon = { NavigationIcon(onHide) }
                )
            }
        ) { values ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                video.filterValues { it.isNotEmpty() }.forEach { (entry, values) ->
                    item(entry.name, { GridItemSpan(maxLineSpan) }) {
                        ParagraphTitle(stringResource(entry.title), Modifier.padding(bottom = 4.dp))
                    }

                    items(values, Video::url) { video ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                                text = video.name ?: stringResource(Res.string.text_unknown),
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
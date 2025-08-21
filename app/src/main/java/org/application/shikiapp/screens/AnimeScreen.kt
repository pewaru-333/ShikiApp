package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.states.showSheetContent
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.ui.Video
import org.application.shikiapp.models.viewModels.AnimeViewModel
import org.application.shikiapp.network.response.Response.Error
import org.application.shikiapp.network.response.Response.Loading
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.CreateRate
import org.application.shikiapp.ui.templates.Description
import org.application.shikiapp.ui.templates.DialogScreenshot
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.LinksSheet
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.ui.templates.Poster
import org.application.shikiapp.ui.templates.Profiles
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.Related
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScoreInfo
import org.application.shikiapp.ui.templates.SheetColumn
import org.application.shikiapp.ui.templates.SimilarFull
import org.application.shikiapp.ui.templates.Statistics
import org.application.shikiapp.ui.templates.StatusInfo
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

    when (val data = response) {
        is Error -> ErrorScreen(model::loadData)
        is Loading -> LoadingScreen()
        is Success -> AnimeView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeView(
    anime: Anime,
    state: AnimeState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val listState = rememberLazyListState()
    val comments = anime.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_anime)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    IconComment(
                        comments = comments,
                        onEvent = { onEvent(ContentDetailEvent.ShowComments) }
                    )
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowSheet) },
                        content = { Icon(Icons.Outlined.MoreVert, null) }
                    )
                }
            )
        }
    ) { values ->
        LazyColumn(
            verticalArrangement = spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = values.calculateTopPadding(),
                end = 8.dp,
                bottom = 0.dp
            )
        ) {
            item {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(anime.poster)
                    Column(Modifier.height(300.dp), SpaceBetween) {
                        LabelInfoItem(stringResource(text_kind), stringResource(anime.kind))
                        LabelInfoItem(stringResource(text_episodes), anime.episodes)
                        StatusInfo(anime.status, anime.airedOn, anime.releasedOn)
                        LabelInfoItem(stringResource(R.string.text_source), stringResource(anime.origin))
                        ScoreInfo(anime.score)
                        LabelInfoItem(stringResource(R.string.text_rating), stringResource(anime.rating))
                    }
                }
            }

            anime.genres?.let { list ->
                item {
                    LazyRow(horizontalArrangement = spacedBy(4.dp)) {
                        items(list) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(it) }
                            )
                        }
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    anime.studio?.let { studio ->
                        item {
                            DetailBox(
                                icon = R.drawable.vector_anime,
                                label = stringResource(R.string.text_studio),
                                value = studio.title,
                                onClick = { onNavigate(Screen.Catalog(studio = studio.id)) }
                            )
                        }
                    }

                    item {
                        DetailBox(
                            icon = R.drawable.vector_timer,
                            label = stringResource(R.string.text_episode),
                            value = anime.duration
                        )
                    }

                    anime.nextEpisodeAt.let {
                        if (it.isNotEmpty()) {
                            item {
                                DetailBox(
                                    icon = R.drawable.vector_calendar,
                                    label = stringResource(R.string.text_episode_next),
                                    value = it
                                )
                            }
                        }
                    }

                    anime.similar.let {
                        if (it.isNotEmpty()) {
                            item {
                                DetailBox(
                                    icon = R.drawable.vector_similar,
                                    label = stringResource(R.string.text_similar),
                                    onClick = { onEvent(ContentDetailEvent.Media.ShowSimilar) }
                                )
                            }
                        }
                    }

                    item {
                        DetailBox(
                            icon = R.drawable.vector_statistics,
                            label = stringResource(R.string.text_statistics),
                            onClick = { onEvent(ContentDetailEvent.Media.ShowStats) }
                        )
                    }

                    item {
                        DetailBox(
                            icon = R.drawable.vector_subtitles,
                            label = stringResource(R.string.text_subtitles),
                            onClick = { onEvent(ContentDetailEvent.Media.ShowFansubbers) }
                        )
                    }

                    item {
                        DetailBox(
                            icon = R.drawable.vector_voice_actors,
                            label = stringResource(R.string.text_voices),
                            onClick = { onEvent(ContentDetailEvent.Media.ShowFandubbers) }
                        )
                    }
                }
            }

            anime.description.let {
                if (it.isNotEmpty()) {
                    item { Description(it) }
                }
            }
            anime.related.let {
                if (it.isNotEmpty()) {
                    item {
                        Related(
                            list = it,
                            showAllRelated = { onEvent(ContentDetailEvent.Media.ShowRelated) },
                            onNavigate = onNavigate
                        )
                    }
                }
            }

            anime.charactersMain.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it,
                            title = stringResource(R.string.text_characters),
                            onShowFull = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
                            onNavigate = { onNavigate(Screen.Character(it)) }
                        )
                    }
                }
            }
            anime.personMain.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it,
                            title = stringResource(R.string.text_authors),
                            onShowFull = { onEvent(ContentDetailEvent.Media.ShowAuthors) },
                            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
                        )
                    }
                }
            }
            anime.screenshots.let {
                if (it.isNotEmpty()) {
                    item {
                        Screenshots(
                            list = it,
                            show = { onEvent(ContentDetailEvent.Media.ShowImage(it)) },
                            hide = { onEvent(ContentDetailEvent.Media.Anime.ShowScreenshots) },
                        )
                    }
                }
            }
            anime.videos.let {
                if (it.isNotEmpty()) {
                    item {
                        Video(
                            list = it,
                            show = { onEvent(ContentDetailEvent.Media.Anime.ShowVideo) },
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
        listState = state.lazySimilar,
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
        state = if (state.showCharacters) state.lazyCharacters else state.lazyAuthors,
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
        showScreenshot = { onEvent(ContentDetailEvent.Media.ShowImage(it)) },
        hide = { onEvent(ContentDetailEvent.Media.Anime.ShowScreenshots) }
    )

    DialogScreenshot(
        list = anime.screenshots,
        screenshot = state.screenshot,
        visible = state.showScreenshot,
        setScreenshot = { onEvent(ContentDetailEvent.Media.SetImage(it)) },
        hide = { onEvent(ContentDetailEvent.Media.ShowImage()) }
    )

    Video(
        list = anime.videos,
        visible = state.showVideo,
        hide = { onEvent(ContentDetailEvent.Media.Anime.ShowVideo) }
    )

    when {
        state.showSheet -> BottomSheet(
            state = state.sheetBottom,
            rate = anime.userRate,
            favoured = anime.favoured,
            onEvent = onEvent,
            toggleFavourite = { onEvent(ContentDetailEvent.Media.Anime.ToggleFavourite) }
        )

        state.showRate -> CreateRate(
            id = anime.id,
            type = LinkedType.ANIME,
            rateF = anime.userRate.getValue(),
            reload = { onEvent(ContentDetailEvent.Media.ChangeRate) },
            hide = { onEvent(ContentDetailEvent.Media.ShowRate) }
        )

        state.showLinks -> LinksSheet(
            list = anime.links,
            state = state.sheetLinks,
            hide = { onEvent(ContentDetailEvent.Media.ShowLinks) }
        )

        state.showSheetContent -> SheetColumn(
            state = state.sheetColumn,
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
fun DetailBox(icon: Int, label: String, value: String? = null, onClick: (() -> Unit)? = null) =
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
    ) {
        if (value != null) {
            Row(
                horizontalArrangement = spacedBy(8.dp),
                verticalAlignment = CenterVertically,
                modifier = Modifier
                    .height(56.dp)
                    .padding(12.dp, 8.dp),
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                if (onClick != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = spacedBy(2.dp, CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(56.dp)
                    .padding(10.dp, 6.dp),
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

@Composable
private fun Screenshots(list: List<String>, show: (Int) -> Unit, hide: () -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(hide) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            itemsIndexed(list.take(6)) { index, item ->
                AsyncImage(
                    model = item,
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 97.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { show(index) }
                )
            }
        }
    }

@Composable
private fun Video(list: List<Video>, show: () -> Unit, uri: UriHandler = LocalUriHandler.current) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_video), Modifier.padding(bottom = 4.dp))
            IconButton(show) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list.take(3)) {
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 130.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { uri.openUri(it.url) }
                )
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Screenshots(
    list: List<String>,
    visible: Boolean,
    showScreenshot: (Int) -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_screenshots)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalItemSpacing = 2.dp,
            horizontalArrangement = spacedBy(2.dp)
        ) {
            itemsIndexed(list) { index, item ->
                AsyncImage(
                    model = item,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(80.dp)
                        .clickable { showScreenshot(index) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Video(
    list: List<Video>,
    visible: Boolean,
    hide: () -> Unit,
    handler: UriHandler = LocalUriHandler.current,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_video)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
            horizontalArrangement = SpaceBetween,
            verticalItemSpacing = 12.dp
        ) {
            VideoKind.entries.forEach { entry ->
                if (list.any { it.kind in entry.kinds })
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ParagraphTitle(stringResource(entry.title), Modifier.padding(bottom = 4.dp))
                    }

                items(list.filter { it.kind in entry.kinds }.sortedBy(Video::name)) {
                    Column(verticalArrangement = spacedBy(4.dp)) {
                        AsyncImage(
                            model = it.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(172.dp, 130.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { handler.openUri(it.url) }
                        )
                        it.name?.let {
                            Text(
                                text = it,
                                modifier = Modifier.size(172.dp, 40.dp),
                                textAlign = TextAlign.Center,
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
}
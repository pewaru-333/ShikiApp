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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import org.application.AnimeQuery.Data.Anime.Video
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_rating
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_studio
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.events.AnimeDetailEvent
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.ui.Anime
import org.application.shikiapp.models.viewModels.AnimeViewModel
import org.application.shikiapp.network.Response.Error
import org.application.shikiapp.network.Response.Loading
import org.application.shikiapp.network.Response.Success
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.enums.VideoKinds
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun AnimeScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<AnimeViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

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
    onEvent: (AnimeDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val comments = anime.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_anime)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (comments.itemCount > 0)
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.ShowComments) }
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowSheet) }
                    ) {
                        Icon(Icons.Outlined.MoreVert, null)
                    }
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
                    ShortInfo(anime)
                }
            }

            anime.genres?.let {
                item {
                    LazyRow(horizontalArrangement = spacedBy(4.dp)) {
                        items(it) { (russian) ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(russian) }
                            )
                        }
                    }
                }
            }

            anime.description.let {
                if (it.isNotEmpty()) item { Description(it) }
            }
            anime.related.let {
                if (it.isNotEmpty()) item {
                    Related(
                        list = it,
                        hide = { onEvent(ContentDetailEvent.ShowRelated) },
                        onNavigate = onNavigate
                    )
                }
            }
            anime.charactersMain.let {
                if (it.isNotEmpty()) item {
                    Characters(
                        list = it,
                        show = { onEvent(AnimeDetailEvent.ShowCharacters) },
                        onNavigate = onNavigate
                    )
                }
            }
            anime.personMain.let {
                if (it.isNotEmpty()) item {
                    Authors(
                        list = it,
                        show = { onEvent(AnimeDetailEvent.ShowAuthors) },
                        onNavigate = onNavigate
                    )
                }
            }
            anime.screenshots.let {
                if (it.isNotEmpty()) item {
                    Screenshots(
                        list = it,
                        show = { onEvent(AnimeDetailEvent.ShowScreenshot(it)) },
                        hide = { onEvent(AnimeDetailEvent.ShowScreenshots) },
                    )
                }
            }
            anime.videos.let {
                if (it.isNotEmpty()) item {
                    Video(
                        list = it,
                        show = { onEvent(AnimeDetailEvent.ShowVideo) },
                    )
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

    RelatedFull(
        list = anime.related,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.ShowRelated) },
        onNavigate = onNavigate
    )

    SimilarFull(
        list = anime.similar,
        listState = state.lazySimilar,
        visible = state.showSimilar,
        onNavigate = { onNavigate(Screen.Anime(it)) },
        hide = { onEvent(ContentDetailEvent.ShowSimilar) }
    )

    Statistics(
        scores = anime.stats.scoresStats,
        stats = anime.stats.statusesStats,
        type = LINKED_TYPE[0],
        visible = state.showStats,
        hide = { onEvent(ContentDetailEvent.ShowStats) },
    )

    CharactersFull(
        list = anime.charactersAll,
        state = state.lazyCharacters,
        visible = state.showCharacters,
        hide = { onEvent(AnimeDetailEvent.ShowCharacters) },
        onNavigate = onNavigate
    )

    AuthorsFull(
        roles = anime.personAll,
        state = state.lazyAuthors,
        visible = state.showAuthors,
        hide = { onEvent(AnimeDetailEvent.ShowAuthors) },
        onNavigate = onNavigate
    )

    Screenshots(
        list = anime.screenshots,
        visible = state.showScreenshots,
        showScreenshot = { onEvent(AnimeDetailEvent.ShowScreenshot(it)) },
        hide = { onEvent(AnimeDetailEvent.ShowScreenshots) }
    )

    DialogScreenshot(
        list = anime.screenshots,
        screenshot = state.screenshot,
        visible = state.showScreenshot,
        setScreenshot = { onEvent(AnimeDetailEvent.SetScreenshot(it)) },
        hide = { onEvent(AnimeDetailEvent.ShowScreenshot()) }
    )

    Video(
        list = anime.videos,
        visible = state.showVideo,
        hide = { onEvent(AnimeDetailEvent.ShowVideo) }
    )

    when {
        state.showSheet -> BottomSheet(
            state = state.sheetBottom,
            rate = anime.userRate,
            favoured = anime.favoured,
            onEvent = onEvent as (ContentDetailEvent) -> Unit,
            toggleFavourite = { onEvent(AnimeDetailEvent.ToggleFavourite(anime.favoured)) }
        )

        state.showRate -> CreateRate(
            id = anime.id,
            type = LINKED_TYPE[0],
            rateF = anime.userRate,
            reload = { onEvent(AnimeDetailEvent.Reload) },
            hide = { onEvent(AnimeDetailEvent.ShowRate) }
        )

        state.showLinks -> LinksSheet(
            list = anime.links,
            state = state.sheetLinks,
            hide = { onEvent(ContentDetailEvent.ShowLinks) }
        )
    }
}

@Composable
private fun ShortInfo(anime: Anime) {
    val name = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    val info = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    Column(Modifier.height(300.dp), SpaceBetween) {
        Column {
            Text(stringResource(text_kind), style = name)
            Text(anime.kind, style = info)
        }
        Column {
            Text(stringResource(text_episodes), style = name)
            Text(anime.episodes, style = info)
        }
        Column {
            Text(stringResource(text_status), style = name)
            Text(anime.status, style = info)
        }
        Column {
            Text(stringResource(text_studio), style = name)
            Text(anime.studio, style = info)
        }
        Column {
            Text(stringResource(text_score), style = name)
            Row(
                horizontalArrangement = spacedBy(4.dp),
                verticalAlignment = CenterVertically
            ) {
                Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
                Text(anime.score, style = info)
            }
        }
        Column {
            Text(stringResource(text_rating), style = name)
            Text(anime.rating, style = info)
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
                    model = "https:${it.imageUrl}",
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
    BackHandler(onBack = hide)
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
    BackHandler(onBack = hide)
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
            VideoKinds.entries.forEach { entry ->
                if (list.any { it.kind.rawValue in entry.kinds })
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ParagraphTitle(entry.title, Modifier.padding(bottom = 4.dp))
                    }

                items(list.filter { it.kind.rawValue in entry.kinds }.sortedBy(Video::name)) {
                    Column(verticalArrangement = spacedBy(4.dp)) {
                        AsyncImage(
                            model = "https:${it.imageUrl}",
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
package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.application.AnimeQuery.Data.Anime
import org.application.AnimeQuery.Data.Anime.Screenshot
import org.application.AnimeQuery.Data.Anime.Video
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_image_of
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_rating
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_studio
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.states.AnimeState
import org.application.shikiapp.models.views.AnimeViewModel
import org.application.shikiapp.models.views.AnimeViewModel.Response.Error
import org.application.shikiapp.models.views.AnimeViewModel.Response.Loading
import org.application.shikiapp.models.views.AnimeViewModel.Response.Success
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.STATUSES_A
import org.application.shikiapp.utils.VideoKinds
import org.application.shikiapp.utils.getFull
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getRating
import org.application.shikiapp.utils.getStatusA
import org.application.shikiapp.utils.getStudio

@Composable
fun AnimeScreen(
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser: (Long) -> Unit,
    back: () -> Unit
) {
    val model = viewModel<AnimeViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen(model::getAnime)
        Loading -> LoadingScreen()
        is Success -> AnimeView(
            model = model,
            state = state,
            data = data,
            toAnime = toAnime,
            toManga = toManga,
            toCharacter = toCharacter,
            toPerson = toPerson,
            toUser = toUser,
            back = back
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeView(
    model: AnimeViewModel,
    state: AnimeState,
    data: Success,
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser: (Long) -> Unit,
    back: () -> Unit
) {
    val (anime, similar, links, _, stats, favoured) = data
    val comments = data.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_anime)) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (comments.itemCount > 0) IconButton(model::showComments) {
                        Icon(painterResource(vector_comments), null)
                    }
                    IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
                }
            )
        }) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = anime.russian?.let { "$it / ${anime.name}" } ?: anime.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(anime.poster?.originalUrl)
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

            anime.descriptionHtml?.let { if (fromHtml(it).isNotEmpty()) item { Description(it) } }
            anime.related?.let {
                if (it.isNotEmpty()) item { Related(model::showRelated, it, toAnime, toManga) }
            }
            anime.characterRoles?.let {
                if (it.isNotEmpty()) item { Characters(model::showCharacters, it, toCharacter) }
            }
            anime.personRoles?.let {
                if (it.isNotEmpty()) item { Authors(model::showAuthors, it, toPerson) }
            }
            anime.screenshots.let { if (it.isNotEmpty()) item { Screenshots(model, it) } }
            anime.videos.let { if (it.isNotEmpty()) item { Video(model, it) } }
        }
    }

    when {
        state.showSheet -> BottomSheet(
            model::showSheet, model::showRate, model::showSimilar, model::showStats,
            model::showLinks, model::changeFavourite, state.sheetBottom, anime.userRate, favoured
        )
        state.showComments -> Comments(model::showComments, comments, toUser)
        state.showRelated -> DialogRelated(model::showRelated, anime.related!!, toAnime, toManga)
        state.showCharacters -> DialogCharacters(model::showCharacters, state.lazyCharacters, anime.characterRoles!!, toCharacter)
        state.showAuthors -> DialogAuthors(model::showAuthors, state.lazyAuthors, anime.personRoles!!, toPerson)
        state.showScreenshots -> DialogScreenshots(model, state, anime.screenshots)
        state.showScreenshot -> DialogScreenshot(model, state, anime.screenshots)
        state.showVideo -> DialogVideo(model, anime.videos)
        state.showRate -> CreateRate(model::showRate, model::reload, LINKED_TYPE[0], anime.id, anime.userRate)
        state.showSimilar -> DialogSimilar(state.lazySimilar, similar, toAnime, model::showSimilar)
        state.showStats -> Statistics(model::showStats, stats.scoresStats, stats.statusesStats, LINKED_TYPE[0])
        state.showLinks -> LinksSheet(model::showLinks, state.sheetLinks, links)
    }
}

@Composable
private fun ShortInfo(anime: Anime) {
    val name = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    val info = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    fun getEpisodes() = when (anime.status?.rawValue) {
        STATUSES_A.keys.elementAt(1) -> "${anime.episodesAired} / ${getFull(anime.episodes)}"
        STATUSES_A.keys.elementAt(2) -> "${anime.episodes} / ${anime.episodes}"
        else -> "${anime.episodesAired} / ${anime.episodes}"
    }

    Column(Modifier.height(300.dp), SpaceBetween) {
        Column {
            Text(stringResource(text_kind), style = name)
            Text(getKind(anime.kind?.rawValue), style = info)
        }
        Column {
            Text(stringResource(text_episodes), style = name)
            Text(getEpisodes(), style = info)
        }
        Column {
            Text(stringResource(text_status), style = name)
            Text(getStatusA(anime.status?.rawValue), style = info)
        }
        Column {
            Text(stringResource(text_studio), style = name)
            Text(getStudio(anime.studios), style = info)
        }
        Column {
            Text(stringResource(text_score), style = name)
            Row(horizontalArrangement = spacedBy(4.dp), verticalAlignment = CenterVertically) {
                Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
                Text(anime.score.toString(), style = info)
            }
        }
        Column {
            Text(stringResource(text_rating), style = name)
            Text(getRating(anime.rating?.rawValue), style = info)
        }
    }
}

@Composable
private fun Screenshots(model: AnimeViewModel, screenshots: List<Screenshot>) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(model::showScreenshots) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            itemsIndexed(screenshots.take(6)) { index, item ->
                AsyncImage(
                    model = item.originalUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 97.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { model.showScreenshot(index) }
                )
            }
        }
    }

@Composable
private fun Video(
    model: AnimeViewModel,
    video: List<Video>,
    handler: UriHandler = LocalUriHandler.current
) = Column(verticalArrangement = spacedBy(4.dp)) {
    Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        ParagraphTitle(stringResource(text_video), Modifier.padding(bottom = 4.dp))
        IconButton(model::showVideo) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
    }
    LazyRow(horizontalArrangement = spacedBy(12.dp)) {
        items(video.take(3)) {
            AsyncImage(
                model = "https:${it.imageUrl}",
                contentDescription = null,
                modifier = Modifier
                    .size(172.dp, 130.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { handler.openUri(it.url) }
            )
        }
    }
}

// =========================================== Dialogs ===========================================

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogScreenshots(model: AnimeViewModel, state: AnimeState, list: List<Screenshot>) =
    Dialog(model::showScreenshots, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_screenshots)) },
                    navigationIcon = { NavigationIcon(model::showScreenshots) }
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
                        model = item.originalUrl,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(80.dp)
                            .clickable { model.showScreenshot(index) }
                    )
                }
            }
        }
    }.also { if (state.showScreenshot) DialogScreenshot(model, state, list) }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogScreenshot(model: AnimeViewModel, state: AnimeState, list: List<Screenshot>) {
    val pagerState = rememberPagerState(state.screenshot) { list.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest(model::setScreenshot)
    }

    Dialog(model::showScreenshot, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_image_of, state.screenshot + 1, list.size)) },
                    navigationIcon = { NavigationIcon(model::showScreenshot) }
                )
            }
        ) { values ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = values
            ) { AsyncImage(list[it].originalUrl, null) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogVideo(
    model: AnimeViewModel,
    list: List<Video>,
    handler: UriHandler = LocalUriHandler.current
) = Dialog(model::showVideo, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_video)) },
                navigationIcon = { NavigationIcon(model::showVideo) }
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

                items(list.filter { it.kind.rawValue in entry.kinds }.sortedBy { it.name }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSimilar(
    state: LazyListState,
    list: List<AnimeBasic>,
    toAnime: (String) -> Unit,
    hide: () -> Unit
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_similar)) },
                navigationIcon = { NavigationIcon(hide) })
        }
    ) { values ->
        LazyColumn(
            state = state,
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
            verticalArrangement = spacedBy(8.dp)
        ) {
            items(list) {
                ListItem(
                    headlineContent = { Text(it.russian ?: it.name) },
                    modifier = Modifier.clickable { toAnime(it.id.toString()) },
                    leadingContent = {
                        AsyncImage(
                            model = getImage(it.image.original),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    (0.5).dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    MaterialTheme.shapes.medium
                                )
                        )
                    }
                )
            }
        }
    }
}
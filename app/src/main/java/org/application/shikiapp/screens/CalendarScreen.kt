package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.events.CalendarEvent
import org.application.shikiapp.models.states.AnimeCalendarState
import org.application.shikiapp.models.ui.AnimeCalendar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.viewModels.CalendarViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.ui.templates.CalendarOngoingCard
import org.application.shikiapp.ui.templates.CatalogCardItem
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onNavigate: (Screen) -> Unit) {
    val model = viewModel<CalendarViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val tabs = listOf(stringResource(R.string.text_featured), stringResource(R.string.text_schedule))
    val pagerState = rememberPagerState(pageCount = tabs::size)

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collectLatest { page ->
            if (page != pagerState.settledPage) {
                onScroll(page)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_updates)) },
                actions = {
                    IconButton(
                        onClick = { model.onEvent(CalendarEvent.Reload) },
                        content = { Icon(Icons.Outlined.Refresh, null) }
                    )
                }
            )
        }
    ) { values ->
        Column {
            PrimaryTabRow(pagerState.currentPage, Modifier.padding(values)) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { onScroll(index) },
                        text = { Text(tab) }
                    )
                }
            }

            when (val data = response) {
                is Response.Error -> ErrorScreen(model::loadData)
                is Response.Loading -> LoadingScreen()
                is Response.Success -> CalendarView(data.data, state, pagerState, model::onEvent, onNavigate)

                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarView(
    calendar: AnimeCalendar,
    state: AnimeCalendarState,
    pagerState: PagerState,
    onEvent: (CalendarEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val topics = calendar.updates.collectAsLazyPagingItems()

    HorizontalPager(pagerState) { tab ->
        when (tab) {
            0 -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Trending(calendar.trending, onNavigate)
                }

                item {
                    Random(calendar.random, onNavigate)
                }

                item {
                    Updates(topics, onEvent, onNavigate)
                }
            }

            1 -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    text = "На данный момент календарь выхода серий отключён на стороне сервера",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    AnimeUpdatesFull(
        updates = topics,
        visible = state.showFullUpdates,
        onNavigate = onNavigate,
        hide = { onEvent(CalendarEvent.ShowFullUpdates) }
    )
}

@Composable
private fun Trending(trending: List<Content>, onNavigate: (Screen) -> Unit) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(stringResource(R.string.text_airing))
        IconButton(
            onClick = { onNavigate(Screen.Catalog(showOngoing = true)) },
            content = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        )
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(trending, Content::id) { anime ->
            CalendarOngoingCard(
                title = anime.title,
                score = anime.score,
                poster = anime.poster,
                onNavigate = { onNavigate(Screen.Anime(anime.id)) }
            )
        }
    }
}

@Composable
private fun Random(trending: List<Content>, onNavigate: (Screen) -> Unit) = Column {
    ParagraphTitle(stringResource(R.string.text_random), Modifier.padding(bottom = 8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(trending, Content::id) { anime ->
            CalendarOngoingCard(
                title = anime.title,
                score = anime.score,
                poster = anime.poster,
                onNavigate = { onNavigate(Screen.Anime(anime.id)) }
            )
        }
    }
}

@Composable
private fun Updates(
    updates: LazyPagingItems<Content>,
    onEvent: (CalendarEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(stringResource(R.string.text_updates_anime))
        IconButton(
            onClick = { onEvent(CalendarEvent.ShowFullUpdates) },
            content = { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        )
    }

    if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
    else LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(updates.itemCount.coerceAtMost(12)) { index ->
            updates[index]?.let { anime ->
                CalendarOngoingCard(
                    title = anime.title,
                    poster = anime.poster,
                    score = null,
                    onNavigate = { onNavigate(Screen.Anime(anime.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeUpdatesFull(
    updates: LazyPagingItems<Content>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_updates)) },
                navigationIcon = {
                    IconButton(hide) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                }
            )
        }
    ) { values ->
        if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
        else LazyColumn(contentPadding = values) {
            items(updates.itemCount) { index ->
                updates[index]?.let { anime ->
                    CatalogCardItem(
                        title = anime.title,
                        kind = anime.kind,
                        season = anime.season,
                        status = anime.status,
                        image = anime.poster,
                        onClick = { onNavigate(Screen.Anime(anime.id)) },
                        score = anime.score
                    )
                }
            }
        }
    }
}

//@Composable
//private fun Schedule(data: Success, onNavigate: (Screen) -> Unit) =
//    LazyColumn(
//        contentPadding = PaddingValues(8.dp, 16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(data.calendar) { (date, list) ->
//            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                ParagraphTitle(date)
//                LazyRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(list) { (_, _, _, anime) ->
//                        Column(
//                            modifier = Modifier
//                                .width(122.dp)
//                                .clickable { onNavigate(Screen.Anime(anime.id.toString())) },
//                        ) {
//                            RoundedRelatedPoster(anime.image.original, ContentScale.FillBounds)
//                            Text(
//                                text = anime.russian?.ifEmpty(anime::name) ?: BLANK,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                textAlign = TextAlign.Center,
//                                maxLines = 3,
//                                minLines = 3,
//                                overflow = TextOverflow.Ellipsis,
//                                style = MaterialTheme.typography.titleSmall,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
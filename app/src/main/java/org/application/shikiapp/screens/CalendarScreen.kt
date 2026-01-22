@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.models.ui.AnimeCalendar
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.viewModels.CalendarViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.ui.templates.CalendarOngoingCard
import org.application.shikiapp.ui.templates.CatalogCardItem
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.navigation.LocalBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun CalendarScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val model = viewModel<CalendarViewModel>()
    val response by model.response.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val tabs = arrayOf(stringResource(R.string.text_featured), stringResource(R.string.text_schedule))
    val pagerState = rememberPagerState(pageCount = tabs::size)

    var showFullUpdates by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(showFullUpdates) {
        barVisibility.toggle(showFullUpdates)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_updates)) },
                actions = { IconButton(model::reload) { VectorIcon(R.drawable.vector_refresh) } }
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) {
            PrimaryTabRow(pagerState.currentPage) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.targetPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(tab) }
                    )
                }
            }

            when (val data = response) {
                is Response.Error -> ErrorScreen(model::loadData)
                is Response.Loading -> LoadingScreen()
                is Response.Success -> CalendarView(
                    calendar = data.data,
                    pagerState = pagerState,
                    onShow = { showFullUpdates = true },
                    onNavigate = onNavigate
                )

                else -> Unit
            }
        }
    }

    (response as? Response.Success)?.let { success ->
        val topics = success.data.updates.collectAsLazyPagingItems()

        AnimeUpdatesFull(
            updates = topics,
            visible = showFullUpdates,
            onNavigate = onNavigate,
            onHide = { showFullUpdates = false }
        )
    }
}

@Composable
private fun CalendarView(
    calendar: AnimeCalendar,
    pagerState: PagerState,
    onShow: () -> Unit,
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
                    Updates(topics, onShow, onNavigate)
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
}

@Composable
private fun Trending(trending: List<Content>, onNavigate: (Screen) -> Unit) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(stringResource(R.string.text_airing))
        IconButton(
            onClick = { onNavigate(Screen.Catalog(showOngoing = true)) },
            content = { VectorIcon(R.drawable.vector_arrow_forward) }
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
private fun Updates(updates: LazyPagingItems<Content>, onShow: () -> Unit, onNavigate: (Screen) -> Unit) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(stringResource(R.string.text_updates_anime))
        IconButton(onShow) { VectorIcon(R.drawable.vector_arrow_forward) }
    }

    if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
    else LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(updates.itemCount.coerceAtMost(12), updates.itemKey(Content::id)) { index ->
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

@Composable
private fun AnimeUpdatesFull(
    updates: LazyPagingItems<Content>,
    visible: Boolean,
    onNavigate: (Screen) -> Unit,
    onHide: () -> Unit,
) = AnimatedVisibility(
    visible = visible,
    modifier = Modifier.zIndex(10f),
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, onHide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_updates)) },
                navigationIcon = { NavigationIcon(onHide) }
            )
        }
    ) { values ->
        if (updates.loadState.refresh is LoadState.Loading) LoadingScreen()
        else LazyColumn(contentPadding = values) {
            items(updates.itemCount, updates.itemKey(Content::id)) { index ->
                updates[index]?.let { anime ->
                    CatalogCardItem(
                        title = anime.title,
                        kind = anime.kind,
                        season = anime.season,
                        status = anime.status,
                        image = anime.poster,
                        score = anime.score,
                        onClick = { onNavigate(Screen.Anime(anime.id)) }
                    )
                }
            }
        }
    }
}
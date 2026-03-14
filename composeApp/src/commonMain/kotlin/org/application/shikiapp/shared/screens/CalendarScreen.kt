@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.viewModels.CalendarViewModel
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.ui.templates.CalendarOngoingCard
import org.application.shikiapp.shared.ui.templates.ErrorScreen
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.ParagraphTitle
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.enums.CatalogItem
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_airing
import shikiapp.composeapp.generated.resources.text_featured
import shikiapp.composeapp.generated.resources.text_random
import shikiapp.composeapp.generated.resources.text_schedule
import shikiapp.composeapp.generated.resources.text_updates
import shikiapp.composeapp.generated.resources.text_updates_anime
import shikiapp.composeapp.generated.resources.vector_arrow_forward
import shikiapp.composeapp.generated.resources.vector_refresh

@Composable
fun CalendarScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val model = viewModel { CalendarViewModel() }
    val response by model.response.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val tabs =
        arrayOf(stringResource(Res.string.text_featured), stringResource(Res.string.text_schedule))
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
                title = { Text(stringResource(Res.string.text_updates)) },
                actions = { IconButton(model::reload) { VectorIcon(Res.drawable.vector_refresh) } }
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
                is Response.Success -> {
                    CalendarView(
                        calendar = data.data,
                        pagerState = pagerState,
                        onShow = { showFullUpdates = true },
                        onNavigate = onNavigate
                    )
                }

                else -> Unit
            }
        }
    }

    (response as? Response.Success)?.let { success ->
        val topics = success.data.updates.collectAsLazyPagingItems()

        AnimeUpdatesFull(
            list = topics,
            isVisible = showFullUpdates,
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
            0 -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AnimeSection(
                            label = stringResource(Res.string.text_airing),
                            itemCount = calendar.trending.size,
                            getItem = calendar.trending::getOrNull,
                            onNavigate = onNavigate,
                            onIconClick = { onNavigate(Screen.Catalog(showOngoing = true)) }
                        )
                    }

                    item {
                        AnimeSection(
                            label = stringResource(Res.string.text_random),
                            itemCount = calendar.random.size,
                            getItem = calendar.random::getOrNull,
                            onNavigate = onNavigate
                        )
                    }

                    item {
                        AnimeSection(
                            label = stringResource(Res.string.text_updates_anime),
                            itemCount = topics.itemCount.coerceAtMost(12),
                            getItem = { index -> topics[index] },
                            isLoading = topics.loadState.refresh is LoadState.Loading,
                            showScore = false,
                            onNavigate = onNavigate,
                            onIconClick = onShow
                        )
                    }
                }
            }

            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    calendar.schedule.fastForEach { item ->
                        item {
                            AnimeSection(
                                label = item.date,
                                itemCount = item.animes.size,
                                getItem = item.animes::getOrNull,
                                onNavigate = onNavigate
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeSection(
    label: String,
    itemCount: Int,
    getItem: (Int) -> Content?,
    onNavigate: (Screen) -> Unit,
    isLoading: Boolean = false,
    showScore: Boolean = true,
    onIconClick: (() -> Unit)? = null
) = Column {
    val isCompact = rememberWindowSize().isCompact

    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(label)

        if (onIconClick == null) {
            Spacer(Modifier.size(48.dp))
        } else {
            IconButton(onIconClick) { VectorIcon(Res.drawable.vector_arrow_forward) }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        if (isCompact) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemCount) { index ->
                    getItem(index)?.let { anime ->
                        CalendarOngoingCard(
                            title = anime.title,
                            score = if (showScore) anime.score else null,
                            poster = anime.poster,
                            onNavigate = { onNavigate(Screen.Anime(anime.id)) }
                        )
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (index in 0 until itemCount) {
                    getItem(index)?.let { anime ->
                        CalendarOngoingCard(
                            title = anime.title,
                            score = if (showScore) anime.score else null,
                            poster = anime.poster,
                            onNavigate = { onNavigate(Screen.Anime(anime.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeUpdatesFull(
    list: LazyPagingItems<Content>,
    isVisible: Boolean,
    onNavigate: (Screen) -> Unit,
    onHide: () -> Unit,
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it },
    modifier = Modifier.zIndex(10f)
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onHide
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.text_updates)) },
                navigationIcon = { NavigationIcon(onHide) }
            )
        }
    ) { values ->
        CatalogList(
            menu = CatalogItem.ANIME,
            list = list as LazyPagingItems<BasicContent>,
            listState = rememberLazyListState(),
            gridState = rememberLazyGridState(),
            paddingValues = PaddingValues(top = values.calculateTopPadding()),
            onNavigate = onNavigate
        )
    }
}
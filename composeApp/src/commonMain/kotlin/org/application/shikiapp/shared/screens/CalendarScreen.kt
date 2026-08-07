package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.ui.list.ContentViewType
import org.application.shikiapp.shared.models.ui.list.asSource
import org.application.shikiapp.shared.models.viewModels.CalendarViewModel
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.ui.templates.*
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun CalendarScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current
    val isCompact = rememberWindowSize().isCompact

    val model = viewModel { CalendarViewModel() }
    val response by model.response.collectAsStateWithLifecycle()

    val updates = remember(response) {
        (response as? Response.Success)?.data?.updates ?: flowOf(PagingData.empty())
    }

    val topics = updates.collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    val scope = rememberCoroutineScope()
    val tabs = remember { arrayOf(Res.string.text_featured, Res.string.text_schedule) }
    val pagerState = rememberPagerState(pageCount = tabs::size)

    var showFullUpdates by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(showFullUpdates) {
        barVisibility.toggle(showFullUpdates)
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(Res.string.text_updates)) },
            actions = { IconButton(model::reload) { VectorIcon(Icons.Refresh) } }
        )

        PrimaryTabRow(pagerState.currentPage) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.targetPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(stringResource(tab)) }
                )
            }
        }

        when (val data = response) {
            is Response.Error -> ErrorScreen(model::loadData)
            is Response.Loading -> LoadingScreen()
            is Response.Success -> {
                CalendarView(
                    calendar = data.data,
                    topics = topics,
                    pagerState = pagerState,
                    isCompact = isCompact,
                    onNavigate = onNavigate,
                    onShow = { showFullUpdates = true }
                )
            }

            else -> Unit
        }
    }

    AnimatedDialogScreen(
        isVisible = showFullUpdates,
        title = stringResource(Res.string.text_updates),
        onHide = { showFullUpdates = false }
    ) { values ->
        ContentList(
            source = topics.asSource(BasicContent::id),
            mode = ContentViewType.ADAPTIVE_ITEM,
            listState = listState,
            gridState = gridState,
            isCompactWindow = isCompact,
            contentPadding = values,
            onItemClick = { id, _ -> onNavigate(Screen.Anime(id)) }
        )
    }
}

@Composable
private fun CalendarView(
    calendar: AnimeCalendar,
    topics: LazyPagingItems<Content>,
    pagerState: PagerState,
    isCompact: Boolean,
    onShow: () -> Unit,
    onNavigate: (Screen) -> Unit
) = HorizontalPager(pagerState) { tab ->
    when (tab) {
        0 -> LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimeSection(
                    label = stringResource(Res.string.text_airing),
                    isCompact = isCompact,
                    itemCount = calendar.trending.size,
                    getItem = calendar.trending::getOrNull,
                    onNavigate = onNavigate,
                    onIconClick = { onNavigate(Screen.Catalog(showOngoing = true)) }
                )
            }

            item {
                AnimeSection(
                    label = stringResource(Res.string.text_random),
                    isCompact = isCompact,
                    itemCount = calendar.random.size,
                    getItem = calendar.random::getOrNull,
                    onNavigate = onNavigate
                )
            }

            item {
                AnimeSection(
                    label = stringResource(Res.string.text_updates_anime),
                    isCompact = isCompact,
                    itemCount = topics.itemCount.coerceAtMost(12),
                    getItem = { index -> topics[index] },
                    isLoading = topics.loadState.refresh is LoadState.Loading,
                    showScore = false,
                    onNavigate = onNavigate,
                    onIconClick = onShow
                )
            }
        }

        1 -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            calendar.schedule.fastForEach { item ->
                item {
                    AnimeSection(
                        label = item.date,
                        isCompact = isCompact,
                        itemCount = item.animes.size,
                        getItem = item.animes::getOrNull,
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeSection(
    label: String,
    itemCount: Int,
    isCompact: Boolean,
    getItem: (Int) -> Content?,
    onNavigate: (Screen) -> Unit,
    isLoading: Boolean = false,
    showScore: Boolean = true,
    onIconClick: (() -> Unit)? = null
) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(label)

        if (onIconClick == null) {
            Spacer(Modifier.size(48.dp))
        } else {
            IconButton(onIconClick) { VectorIcon(Icons.ArrowForward) }
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        if (isCompact) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemCount) { index ->
                    getItem(index)?.let { anime ->
                        OngoingCard(
                            title = anime.title,
                            score = if (showScore) anime.score else null,
                            poster = anime.poster,
                            modifier = Modifier.width(120.dp),
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
                        OngoingCard(
                            title = anime.title,
                            score = if (showScore) anime.score else null,
                            poster = anime.poster,
                            modifier = Modifier.width(160.dp),
                            onNavigate = { onNavigate(Screen.Anime(anime.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OngoingCard(
    title: String,
    score: String?,
    poster: String,
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {
    MediaGridItem(
        title = title,
        poster = poster,
        score = score,
        onClick = onNavigate,
        modifier = modifier,
        titleConfig = MediaGridItemDefaults.titleConfig(
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            minLines = 2
        )
    )
}
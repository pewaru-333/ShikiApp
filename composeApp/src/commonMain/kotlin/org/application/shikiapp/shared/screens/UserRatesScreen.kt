@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.models.states.SortingState
import org.application.shikiapp.shared.models.states.UserRateState
import org.application.shikiapp.shared.models.states.UserRateUiEvent
import org.application.shikiapp.shared.models.states.rememberRateState
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.models.viewModels.UserRateViewModel
import org.application.shikiapp.shared.network.response.RatesResponse
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.ui.templates.DialogEditRate
import org.application.shikiapp.shared.ui.templates.ErrorScreen
import org.application.shikiapp.shared.ui.templates.LoadingScreen
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.ScaffoldSearchBar
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.OrderDirection
import org.application.shikiapp.shared.utils.enums.OrderRates
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.application.shikiapp.shared.utils.enums.WindowSize
import org.application.shikiapp.shared.utils.extensions.pairwise
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberToastState
import org.application.shikiapp.shared.utils.ui.IWindowSize
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_empty
import shikiapp.composeapp.generated.resources.text_episodes
import shikiapp.composeapp.generated.resources.text_error
import shikiapp.composeapp.generated.resources.text_login_to_modify_lists
import shikiapp.composeapp.generated.resources.text_profile_closed
import shikiapp.composeapp.generated.resources.text_rate_chapters
import shikiapp.composeapp.generated.resources.text_to_profile
import shikiapp.composeapp.generated.resources.vector_add
import shikiapp.composeapp.generated.resources.vector_anime
import shikiapp.composeapp.generated.resources.vector_arrow_down
import shikiapp.composeapp.generated.resources.vector_arrow_up
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_manga
import shikiapp.composeapp.generated.resources.vector_no_profile
import shikiapp.composeapp.generated.resources.vector_star

@Composable
fun UserRates(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val windowSize = rememberWindowSize()
    val toast = rememberToastState()

    val model = viewModel(::UserRateViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val orderState by model.orderState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = WatchStatus.entries::size)
    val listStates = WatchStatus.entries.map { rememberLazyListState() }
    val gridStates = WatchStatus.entries.map { rememberLazyGridState() }

    val rateState = rememberRateState()
    val rates by model.rates.collectAsStateWithLifecycle()
    val newRate by model.newRate.collectAsStateWithLifecycle()

    val type by model.type.collectAsStateWithLifecycle()
    val search by model.search.collectAsStateWithLifecycle()

    LaunchedEffect(type) {
        pagerState.requestScrollToPage(0)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { orderState }
            .pairwise()
            .collectLatest { (old, new) ->
                if (new != old) {
                    listStates[pagerState.currentPage].requestScrollToItem(0)
                    gridStates[pagerState.currentPage].requestScrollToItem(0)
                }
            }
    }

    LaunchedEffect(model.rateUiEvent) {
        model.rateUiEvent.collectLatest { event ->
            when (event) {
                UserRateUiEvent.Error -> toast.onShow(Res.string.text_error)

                is UserRateUiEvent.IncrementStart -> rateState.onIncrementStart(event.rateId)
                UserRateUiEvent.IncrementFinish -> rateState.onIncrementFinish()

                is UserRateUiEvent.UpdateStart -> rateState.onUpdateStart(event.rateId)
                UserRateUiEvent.UpdateFinish -> rateState.onUpdateFinish()

                is UserRateUiEvent.DeleteStart -> rateState.onDeleteStart(event.rateId)
                UserRateUiEvent.DeleteFinish -> rateState.onDeleteFinish()
            }
        }
    }

    when (response) {
        RatesResponse.Unlogged -> UnloggedScreen(onNavigate)
        RatesResponse.Error -> ErrorScreen(model::loadRates, if (!model.editable) onBack else null)
        is RatesResponse.Success, RatesResponse.Loading, RatesResponse.NoAccess -> {
            ScaffoldSearchBar(
                search = search,
                onSearch = model::setSearch,
                navigationIcon = { if (!model.editable) NavigationIcon(onBack) },
                actions = {
                    if (model.editable) {
                        FilledIconToggleButton(
                            checked = type == LinkedType.ANIME,
                            onCheckedChange = { model.setLinkedType(LinkedType.ANIME) },
                            content = { VectorIcon(Res.drawable.vector_anime) }
                        )
                        FilledIconToggleButton(
                            checked = type == LinkedType.MANGA,
                            onCheckedChange = { model.setLinkedType(LinkedType.MANGA) },
                            content = { VectorIcon(Res.drawable.vector_manga) }
                        )
                    }
                }
            ) {
                when (response) {
                    is RatesResponse.Loading -> LoadingScreen()
                    is RatesResponse.NoAccess -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(stringResource(Res.string.text_profile_closed))
                    }

                    else -> Column {
                        PrimaryScrollableTabRow(pagerState.currentPage, edgePadding = 8.dp) {
                            WatchStatus.entries.forEach { status ->
                                Tab(
                                    selected = pagerState.targetPage == status.ordinal,
                                    onClick = { scope.launch { pagerState.animateScrollToPage(status.ordinal) } },
                                    text = { Text(stringResource(type.getWatchStatusTitle(status))) }
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(OrderRates.entries, OrderRates::name) { order ->
                                SortChip(
                                    order = order,
                                    state = orderState,
                                    type = type,
                                    onClick = { model.onSortChanged(order) }
                                )
                            }
                        }

                        HorizontalPager(pagerState) { page ->
                            UserRateList(
                                rates = rates.getOrDefault(WatchStatus.entries[page], emptyList()),
                                listState = listStates[page],
                                gridState = gridStates[page],
                                type = type,
                                getRate = model::getRate,
                                editable = model.editable,
                                windowSize = windowSize,
                                rateState = rateState,
                                increment = model::increment,
                                onNavigate = onNavigate
                            )
                        }
                    }
                }
            }
        }
    }

    if (model.showEditDialog) {
        DialogEditRate(
            state = newRate,
            type = type,
            isExists = true,
            onEvent = model::onEvent,
            onUpdate = model::update,
            onDelete = model::delete,
            onDismiss = model::toggleDialog
        )
    }
}

@Composable
private fun UnloggedScreen(onNavigate: (Screen) -> Unit) =
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VectorIcon(Res.drawable.vector_no_profile, Modifier.size(96.dp))
            Text(
                text = stringResource(Res.string.text_login_to_modify_lists),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            FilledTonalButton(
                onClick = { onNavigate(Screen.Profile) },
                content = {
                    Text(stringResource(Res.string.text_to_profile))
                    VectorIcon(Res.drawable.vector_keyboard_arrow_right)
                }
            )
        }
    }

@Composable
private fun Progress(rate: UserRate, type: LinkedType, editable: Boolean, rateState: UserRateState, onIncrement: (Long) -> Unit) {
    val isIncrementing = rateState.isIncrementing(rate.id)

    val current = if (type == LinkedType.ANIME) rate.episodes else rate.chapters
    val maximum = (if (type == LinkedType.ANIME) rate.fullEpisodes else rate.fullChapters).toIntOrNull()

    val progressText = "$current / ${maximum ?: "?"}"
    val progressValue = maximum?.let { if (it > 0) current.toFloat() / it else 0f }

    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                text = stringResource(if (type == LinkedType.ANIME) Res.string.text_episodes else Res.string.text_rate_chapters),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = progressText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(Modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            if (progressValue != null) {
                LinearProgressIndicator(
                    progress = { progressValue.coerceIn(0f, 1f) },
                    drawStopIndicator = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape)
                )
            }

            if (editable && WatchStatus.WATCHING.name.equals(rate.status, true)) {
                AnimatedContent(isIncrementing) {
                    if (it) {
                        CircularProgressIndicator(Modifier.size(32.dp))
                    } else {
                        FilledTonalIconButton(
                            modifier = Modifier.size(32.dp),
                            onClick = { onIncrement(rate.id) },
                            content = { VectorIcon(Res.drawable.vector_add) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserRateCard(
    rate: UserRate,
    type: LinkedType,
    editable: Boolean,
    rateState: UserRateState,
    modifier: Modifier = Modifier,
    onGetRate: (UserRate) -> Unit,
    onIncrement: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = Box(modifier.fillMaxWidth()) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .combinedClickable(
                enabled = !rateState.isEditing(rate.id),
                onClick = { onNavigate(type.navigateTo(rate.contentId)) },
                onLongClick = { if (editable) onGetRate(rate) }
            )
            .padding(12.dp)
    ) {
        AnimatedAsyncImage(
            model = rate.poster,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(110.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = rate.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(rate.kindString),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(8.dp))

            if (rate.score > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VectorIcon(Res.drawable.vector_star, Modifier.size(18.dp), Color(0xFFFFC319))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = rate.scoreString,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Progress(rate, type, editable, rateState, onIncrement)
        }
    }

    AnimatedVisibility(rateState.isEditing(rate.id), Modifier, fadeIn(), fadeOut()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                .clickable(false) { /* Отключение клика по карточке под загрузкой */ },
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun UserRateList(
    rates: List<UserRate>,
    rateState: UserRateState,
    listState: LazyListState,
    gridState: LazyGridState,
    getRate: (UserRate) -> Unit,
    type: LinkedType,
    editable: Boolean,
    windowSize: IWindowSize,
    increment: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = when {
    rates.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(
            text = stringResource(Res.string.text_empty),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    windowSize.isCompact -> LazyColumn(Modifier.fillMaxSize(), listState) {
        items(rates, UserRate::id) { rate ->
            UserRateCard(
                rate = rate,
                type = type,
                editable = editable,
                rateState = rateState,
                onNavigate = onNavigate,
                onGetRate = getRate,
                onIncrement = increment
            )

            if (rate != rates.lastOrNull()) {
                HorizontalDivider(Modifier.padding(horizontal = 12.dp))
            }
        }
    }

    else -> {
        val minSize = when (windowSize.windowSize) {
            WindowSize.MEDIUM -> 220.dp
            else -> 300.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize),
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rates, UserRate::id) { rate ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    UserRateCard(
                        rate = rate,
                        type = type,
                        editable = editable,
                        rateState = rateState,
                        onNavigate = onNavigate,
                        onGetRate = getRate,
                        onIncrement = increment
                    )
                }
            }
        }
    }
}

@Composable
private fun SortChip(order: OrderRates, state: SortingState, type: LinkedType, onClick: () -> Unit) =
    FilterChip(
        onClick = onClick,
        selected = state.order == order,
        label = {
            Text(
                text = stringResource(
                    if (type == LinkedType.ANIME) order.title
                    else order.titleManga ?: order.title
                )
            )
        },
        leadingIcon = {
            if (state.order == order) {
                VectorIcon(
                    resId = when (state.direction) {
                        OrderDirection.ASCENDING -> Res.drawable.vector_arrow_up
                        OrderDirection.DESCENDING -> Res.drawable.vector_arrow_down
                    }
                )
            }
        }
    )
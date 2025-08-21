package org.application.shikiapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_profile_closed
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.models.states.SortingState
import org.application.shikiapp.models.states.UserRateState
import org.application.shikiapp.models.states.UserRateUiEvent
import org.application.shikiapp.models.states.rememberRateState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.RatesResponse
import org.application.shikiapp.network.response.RatesResponse.Error
import org.application.shikiapp.network.response.RatesResponse.Loading
import org.application.shikiapp.network.response.RatesResponse.NoAccess
import org.application.shikiapp.network.response.RatesResponse.Success
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.RateChapters
import org.application.shikiapp.ui.templates.RateEpisodes
import org.application.shikiapp.ui.templates.RateRewatches
import org.application.shikiapp.ui.templates.RateScore
import org.application.shikiapp.ui.templates.RateStatus
import org.application.shikiapp.ui.templates.RateText
import org.application.shikiapp.ui.templates.RateVolumes
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.OrderDirection
import org.application.shikiapp.utils.enums.OrderRates
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.extensions.pairwise
import org.application.shikiapp.utils.extensions.showToast
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRates(visibility: NavigationBarVisibility, onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<UserRateViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val orderState by model.orderState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = WatchStatus.entries::size)
    val listStates = WatchStatus.entries.map { rememberLazyListState() }

    val rateState = rememberRateState()
    val rates by model.rates.collectAsStateWithLifecycle()
    val newRate by model.newRate.collectAsStateWithLifecycle()

    val type by model.type.collectAsStateWithLifecycle()

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(Unit) {
        visibility.toggle(!model.editable)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collectLatest { page ->
            if (page != pagerState.settledPage) {
                onScroll(page)
            }
        }
    }

    LaunchedEffect(type) {
        snapshotFlow { type }
            .pairwise()
            .collectLatest { (old, new) ->
                if (new != old) {
                    pagerState.requestScrollToPage(0)
                }
            }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { orderState }
            .pairwise()
            .collectLatest { (old, new) ->
                if (new != old) {
                    listStates[pagerState.currentPage].requestScrollToItem(0)
                }
            }
    }

    LaunchedEffect(Unit) {
        model.rateUiEvent.collectLatest { event ->
            when(event) {
                UserRateUiEvent.Error -> context.showToast(R.string.text_error)

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
        Error -> ErrorScreen(model::loadRates)
        is Success, Loading, NoAccess -> Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { if (!model.editable) NavigationIcon(back) },
                    title = { Text(stringResource(type.getListTitle())) },
                    actions = {
                        if (model.editable) {
                            FilledIconToggleButton(
                                checked = type == LinkedType.ANIME,
                                onCheckedChange = { model.setLinkedType(LinkedType.ANIME) },
                                content = { Icon(painterResource(R.drawable.vector_anime), null) }
                            )
                            FilledIconToggleButton(
                                checked = type == LinkedType.MANGA,
                                onCheckedChange = { model.setLinkedType(LinkedType.MANGA) },
                                content = { Icon(painterResource(R.drawable.vector_manga), null) }
                            )
                        }
                    }
                )
            }
        ) { values ->
            if (response is Loading) LoadingScreen()
            else if (response is NoAccess) Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_profile_closed)) }
            else Column(Modifier.padding(values)) {
                PrimaryScrollableTabRow(pagerState.currentPage, edgePadding = 8.dp) {
                    WatchStatus.entries.forEach { status ->
                        Tab(
                            selected = pagerState.currentPage == status.ordinal,
                            onClick = { onScroll(status.ordinal) },
                            text = { Text(stringResource(type.getWatchStatusTitle(status))) }
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = spacedBy(8.dp)
                ) {
                    items(OrderRates.entries) { order ->
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
                        type = type,
                        getRate = model::getRate,
                        editable = model.editable,
                        rateState = rateState,
                        increment = model::increment,
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }

    if (model.showEditDialog) {
        DialogEditRate(
            state = newRate,
            type = type,
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
            Icon(
                modifier = Modifier.size(96.dp),
                painter = painterResource(R.drawable.vector_no_profile),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.text_login_to_modify_lists),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            FilledTonalButton(
                onClick = { onNavigate(Screen.Profile) }
            ) {
                Text(stringResource(R.string.text_to_profile))
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
            }
        }
    }

@Composable
private fun DialogEditRate(
    state: NewRateState,
    type: LinkedType,
    onEvent: (RateEvent) -> Unit,
    onUpdate: (String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) = AlertDialog(
    onDismissRequest = onDismiss,
    dismissButton = { TextButton(onDismiss) { Text(stringResource(text_cancel)) } },
    confirmButton = {
        TextButton(
            content = { Text(stringResource(text_save)) },
            enabled = !state.status.isNullOrEmpty(),
            onClick = { onUpdate(state.id) }
        )
    },
    title = {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            Text(stringResource(text_change))
            IconButton(
                onClick = { onDelete(state.id) },
                content = { Icon(painterResource(R.drawable.vector_trash), null) }
            )
        }
    },
    text = {
        Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
            RateStatus(onEvent, state.statusName, type)
            if (type == LinkedType.ANIME) {
                RateEpisodes(onEvent, state.episodes)
            }
            if (type == LinkedType.MANGA) {
                RateChapters(onEvent, state.chapters)
                RateVolumes(onEvent, state.volumes)
            }
            RateScore(onEvent, state.score)
            RateRewatches(onEvent, state.rewatches, type)
            RateText(onEvent, state.text)
        }
    }
)

@Composable
private fun Progress(rate: UserRate, type: LinkedType, editable: Boolean, rateState: UserRateState, onIncrement: (Long) -> Unit) {
    val isIncrementing = rateState.isIncrementing(rate.id)

    val current = if (type == LinkedType.ANIME) rate.episodes else rate.chapters
    val maximum = (if (type == LinkedType.ANIME) rate.fullEpisodes else rate.fullChapters).toIntOrNull()

    val progressText = "$current / ${maximum ?: "?"}"
    val progressValue = maximum?.let { if (it > 0) current.toFloat() / it else 0f }

    Column {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            Text(
                text = stringResource(if (type == LinkedType.ANIME) R.string.text_episodes else R.string.text_rate_chapters),
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

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
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
                            content = { Icon(Icons.Default.Add, null) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
) = Box(modifier.height(175.dp), Alignment.Center) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                enabled = !rateState.isEditing(rate.id),
                onClick = { onNavigate(type.navigateTo(rate.contentId)) },
                onLongClick = { if (editable) onGetRate(rate) }
            )
            .padding(12.dp)
    ) {
        AsyncImage(
            model = rate.poster,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.medium)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        )

        Column(Modifier.fillMaxHeight()) {
            Text(
                text = rate.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                )
            )

            Spacer(Modifier.height(4.dp))

            Row(Modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                Text(
                    text = stringResource(rate.kind),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            if (rate.score > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, Modifier.size(18.dp), Color(0xFFFFC319))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserRateList(
    rates: List<UserRate>,
    rateState: UserRateState,
    listState: LazyListState,
    getRate: (UserRate) -> Unit,
    type: LinkedType,
    editable: Boolean,
    increment: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = LazyColumn(Modifier.fillMaxSize(), listState) {
    if (rates.isEmpty()) {
        item {
            Box(Modifier.fillParentMaxSize(), Center) {
                Text(stringResource(R.string.text_empty))
            }
        }
    } else {
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
                Icon(
                    contentDescription = null,
                    painter = painterResource(
                        when (state.direction) {
                            OrderDirection.ASCENDING -> R.drawable.vector_arrow_up
                            OrderDirection.DESCENDING -> R.drawable.vector_arrow_down
                        }
                    )
                )
            }
        }
    )
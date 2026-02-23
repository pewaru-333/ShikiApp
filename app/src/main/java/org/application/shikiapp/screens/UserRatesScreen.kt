@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_profile_closed
import org.application.shikiapp.models.states.SortingState
import org.application.shikiapp.models.states.UserRateState
import org.application.shikiapp.models.states.UserRateUiEvent
import org.application.shikiapp.models.states.rememberRateState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.RatesResponse.Error
import org.application.shikiapp.network.response.RatesResponse.Loading
import org.application.shikiapp.network.response.RatesResponse.NoAccess
import org.application.shikiapp.network.response.RatesResponse.Success
import org.application.shikiapp.network.response.RatesResponse.Unlogged
import org.application.shikiapp.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.ui.templates.DialogEditRate
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.OrderDirection
import org.application.shikiapp.utils.enums.OrderRates
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.pairwise
import org.application.shikiapp.utils.extensions.showToast
import org.application.shikiapp.utils.navigation.LocalBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun UserRates(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val barVisibility = LocalBarVisibility.current

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

    LaunchedEffect(Unit) {
        barVisibility.toggle(!model.editable)
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

    LaunchedEffect(model.rateUiEvent) {
        model.rateUiEvent.collectLatest { event ->
            when (event) {
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
        Unlogged -> UnloggedScreen(onNavigate)
        Error -> ErrorScreen(model::loadRates)
        is Success, Loading, NoAccess -> Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { if (!model.editable) NavigationIcon(onBack) },
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
            else if (response is NoAccess) Box(Modifier.fillMaxSize(), Alignment.Center) { Text(stringResource(text_profile_closed)) }
            else Column(Modifier.padding(values)) {
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
                VectorIcon(R.drawable.vector_keyboard_arrow_right)
            }
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
                            content = { VectorIcon(R.drawable.vector_add) }
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
        AnimatedAsyncImage(
            model = rate.poster,
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
                    VectorIcon(R.drawable.vector_star, Modifier.size(18.dp), Color(0xFFFFC319))
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
    getRate: (UserRate) -> Unit,
    type: LinkedType,
    editable: Boolean,
    increment: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = LazyColumn(Modifier.fillMaxSize(), listState) {
    if (rates.isEmpty()) {
        item {
            Box(Modifier.fillParentMaxSize(), Alignment.Center) {
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
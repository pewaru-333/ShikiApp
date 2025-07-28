package org.application.shikiapp.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_chapters
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_profile_closed
import org.application.shikiapp.R.string.text_rate_episodes
import org.application.shikiapp.R.string.text_rate_score
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.RatesResponse
import org.application.shikiapp.network.response.RatesResponse.Error
import org.application.shikiapp.network.response.RatesResponse.Loading
import org.application.shikiapp.network.response.RatesResponse.NoAccess
import org.application.shikiapp.network.response.RatesResponse.Success
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRates(visibility: NavigationBarVisibility, onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<UserRateViewModel>()
    val response by model.response.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = WatchStatus.entries::size)
    val listStates = WatchStatus.entries.map { rememberLazyListState() }

    val rates by model.rates.collectAsStateWithLifecycle()
    val newRate by model.newRate.collectAsStateWithLifecycle()

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(Unit) {
        visibility.toggle(!model.editable)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::settledPage).collectLatest(::onScroll)
    }

    LaunchedEffect(model.type) {
        pagerState.requestScrollToPage(0)
    }

    LaunchedEffect(true) {
        model.changed.collectLatest {
            Toast.makeText(context, if (it) "Успешно!" else "Ошибка!", Toast.LENGTH_SHORT).show()
        }
    }

    when (response) {
        NoAccess -> Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_profile_closed)) }
        RatesResponse.Unlogged -> UnloggedScreen(onNavigate)
        Error -> ErrorScreen(model::loadRates)
        is Success, Loading -> Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { if (!model.editable) NavigationIcon(back) },
                    title = { Text(stringResource(model.type.getListTitle())) },
                    actions = {
                        if (model.editable) {
                            FilledIconToggleButton(
                                checked = model.type == LinkedType.ANIME,
                                onCheckedChange = { model.setLinkedType(LinkedType.ANIME) },
                                content = { Icon(painterResource(R.drawable.vector_anime), null) }
                            )
                            FilledIconToggleButton(
                                checked = model.type == LinkedType.MANGA,
                                onCheckedChange = { model.setLinkedType(LinkedType.MANGA) },
                                content = { Icon(painterResource(R.drawable.vector_manga), null) }
                            )
                        }
                    }
                )
            }
        ) { values ->
            if (response is Loading) LoadingScreen()
            else Column(Modifier.padding(values)) {
                ScrollableTabRow(pagerState.currentPage, edgePadding = 8.dp) {
                    WatchStatus.entries.forEach { status ->
                        Tab(
                            selected = pagerState.currentPage == status.ordinal,
                            onClick = { onScroll(status.ordinal) }
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp, 12.dp),
                                text = stringResource(model.type.getTitleResId(status))
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    key = { WatchStatus.entries[it] },
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapPositionalThreshold = 0.05f
                    )
                ) { page ->
                    UserRateList(
                        rates = rates.getOrDefault(WatchStatus.entries[page], emptyList()),
                        listState = listStates[page],
                        type = model.type,
                        getRate = model::getRate,
                        editable = model.editable,
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
            type = model.type,
            onEvent = model::onEvent,
            onUpdate = model::update,
            onDelete = model::delete,
            onDismiss = model::toggleDialog
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserRateList(
    rates: List<UserRate>,
    listState: LazyListState,
    getRate: (UserRate) -> Unit,
    type: LinkedType,
    editable: Boolean,
    increment: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) = LazyColumn(Modifier.fillMaxSize(), listState) {
    if (rates.isEmpty()) item {
        Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_empty)) }
    }
    else items(rates, UserRate::id) { rate ->
        Box(Modifier.fillMaxSize()) {
            ListItem(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .combinedClickable(
                        onClick = { onNavigate(type.navigateTo(rate.contentId)) },
                        onLongClick = { if (editable) getRate(rate) }
                    ),
                leadingContent = {
                    RoundedPoster(rate.poster)
                },
                overlineContent = {
                    Text(
                        text = rate.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(rate.kind),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = {
                    Column {
                        if (type == LinkedType.ANIME) {
                            Text(
                                text = stringResource(text_rate_episodes, rate.episodes, rate.fullEpisodes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (type == LinkedType.MANGA) {
                            Text(
                                text = stringResource(text_chapters, rate.chapters, rate.fullChapters),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = stringResource(text_rate_score, rate.scoreString),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
            if (editable && WatchStatus.WATCHING.name.equals(rate.status, true)) {
                Box(
                    contentAlignment = Center,
                    content = { Icon(Icons.Outlined.Add, null) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                        .size(40.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.shapes.medium
                        )
                        .clickable { increment(rate.id) }
                )
            }
        }
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
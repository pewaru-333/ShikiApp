package org.application.shikiapp.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_anime_list
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_chapters
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_profile_closed
import org.application.shikiapp.R.string.text_rate_episodes
import org.application.shikiapp.R.string.text_rate_score
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.events.RateEvent.SetChapters
import org.application.shikiapp.events.RateEvent.SetEpisodes
import org.application.shikiapp.events.RateEvent.SetRateId
import org.application.shikiapp.events.RateEvent.SetRewatches
import org.application.shikiapp.events.RateEvent.SetScore
import org.application.shikiapp.events.RateEvent.SetStatus
import org.application.shikiapp.events.RateEvent.SetText
import org.application.shikiapp.events.RateEvent.SetVolumes
import org.application.shikiapp.models.states.NewRateState
import org.application.shikiapp.models.states.RatesState
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.RatesResponse.Error
import org.application.shikiapp.network.response.RatesResponse.Loading
import org.application.shikiapp.network.response.RatesResponse.NoAccess
import org.application.shikiapp.network.response.RatesResponse.Success
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRates(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<UserRateViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        model.increment.collectLatest {
            Toast.makeText(context, if (it) "Успешно!" else "Ошибка!", Toast.LENGTH_SHORT).show()
        }
    }

    when (response) {
        NoAccess -> Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_profile_closed)) }
        Error -> ErrorScreen(model::loadRates)
        Loading -> LoadingScreen()
        is Success -> Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { NavigationIcon(back) },
                    title = {
                        Text(
                            stringResource(
                                if (model.type == LinkedType.ANIME) text_anime_list
                                else R.string.text_manga_list
                            )
                        )
                    }
                )
            }
        ) { values ->
            Column(Modifier.padding(values), spacedBy(16.dp)) {
                PrimaryScrollableTabRow(state.tab.ordinal, edgePadding = 8.dp) {
                    WatchStatus.entries.forEach { status ->
                        Tab(
                            selected = state.tab == status,
                            onClick = { model.setTab(status) }
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp, 12.dp),
                                text = stringResource(
                                    if (model.type == LinkedType.ANIME) status.titleAnime
                                    else status.titleManga
                                )
                            )
                        }
                    }
                }

                UserRateList(
                    ratesFlow = model.rates,
                    state = state,
                    type = model.type,
                    rateStateFlow = model.newRate,
                    editable = model.editable,
                    toggleDialog = model::toggleDialog,
                    increment = model::increment,
                    update = model::update,
                    onEvent = model::onEvent,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserRateList(
    ratesFlow: StateFlow<List<UserRate>>,
    rateStateFlow: StateFlow<NewRateState>,
    state: RatesState,
    type: LinkedType,
    editable: Boolean,
    toggleDialog: () -> Unit,
    increment: (Long) -> Unit,
    update: (String) -> Unit,
    onEvent: (RateEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val rates by ratesFlow.collectAsStateWithLifecycle()
    val rateState by rateStateFlow.collectAsStateWithLifecycle()

    LazyColumn(state = state.listStates.getValue(state.tab)) {
        if (rates.isEmpty()) item {
            Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_empty)) }
        }
        else items(rates) { rate ->
            Box(Modifier.fillMaxSize()) {
                ListItem(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .combinedClickable(
                            onClick = {
                                onNavigate(
                                    if (type == LinkedType.MANGA) Screen.Anime(rate.contentId)
                                    else Screen.Manga(rate.contentId)
                                )
                            },
                            onLongClick = {
                                if (editable) {
                                    onEvent(SetRateId(rate.id.toString()))
                                    onEvent(SetStatus(Enum.safeValueOf<WatchStatus>(rate.status) ?: WatchStatus.PLANNED, type))
                                    onEvent(SetScore(Score.entries.first { it.score == rate.score }))
                                    onEvent(SetChapters(rate.chapters.toString()))
                                    onEvent(SetEpisodes(rate.episodes.toString()))
                                    onEvent(SetVolumes(rate.volumes.toString()))
                                    onEvent(SetRewatches(rate.rewatches.toString()))
                                    onEvent(SetText(rate.text))
                                    toggleDialog()
                                }
                            }
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
                if (editable && WatchStatus.WATCHING.name.equals(rate.status, true))
                    Box(
                        contentAlignment = Center,
                        content = { Icon(Icons.Outlined.Add, null) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 12.dp)
                            .size(40.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
                            .clickable { increment(rate.id) }
                    )
            }
        }
    }

    if (state.showEditRate) AlertDialog(
        onDismissRequest = toggleDialog,
        confirmButton = {
            TextButton(
                enabled = !rateState.status.isNullOrEmpty(),
                onClick = { update(rateState.id) }
            ) { Text(stringResource(text_save)) }
        },
        dismissButton = { TextButton(toggleDialog) { Text(stringResource(text_cancel)) } },
        title = { Text(stringResource(text_change)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                RateStatus(onEvent, rateState.statusName, type)
                if (type == LinkedType.ANIME) {
                    RateEpisodes(onEvent, rateState.episodes)
                }
                if (type == LinkedType.MANGA) {
                    RateChapters(onEvent, rateState.chapters)
                    RateVolumes(onEvent, rateState.volumes)
                }
                RateScore(onEvent, rateState.score)
                RateRewatches(onEvent, rateState.rewatches, type)
                RateText(onEvent, rateState.text)
            }
        }
    )
}
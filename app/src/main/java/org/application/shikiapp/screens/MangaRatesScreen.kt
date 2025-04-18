package org.application.shikiapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_chapters
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_manga_list
import org.application.shikiapp.R.string.text_profile_closed
import org.application.shikiapp.R.string.text_rate_score
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.models.data.MangaRate
import org.application.shikiapp.models.viewModels.MangaRatesViewModel
import org.application.shikiapp.models.viewModels.MangaRatesViewModel.Response.Error
import org.application.shikiapp.models.viewModels.MangaRatesViewModel.Response.Loading
import org.application.shikiapp.models.viewModels.MangaRatesViewModel.Response.NoAccess
import org.application.shikiapp.models.viewModels.MangaRatesViewModel.Response.Success
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetChapters
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetRateId
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetRewatches
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetScore
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetStatus
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetText
import org.application.shikiapp.models.viewModels.UserRateViewModel.RateEvent.SetVolumes
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.SCORES
import org.application.shikiapp.utils.WATCH_STATUSES_A
import org.application.shikiapp.utils.WATCH_STATUSES_M
import org.application.shikiapp.utils.getFull
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaRatesScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<MangaRatesViewModel>()
    val response by model.response.collectAsStateWithLifecycle()

    when(val data = response) {
        NoAccess -> Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_profile_closed)) }
        Error -> ErrorScreen(model::getRates)
        Loading -> LoadingScreen()
        is Success -> {
            val userVM = if (Preferences.getUserId() == model.userId) viewModel<UserRateViewModel>() else null

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(text_manga_list)) },
                        navigationIcon = { NavigationIcon(back) }
                    )
                }
            ) { values ->
                Column(Modifier.padding(top = values.calculateTopPadding()), spacedBy(16.dp)) {
                    ScrollableTabRow(model.tab, edgePadding = 8.dp) {
                        WATCH_STATUSES_M.entries.forEachIndexed { index, entry ->
                            Tab(model.tab == index, { model.tab = index })
                            { Text(entry.value, Modifier.padding(8.dp, 12.dp)) }
                        }
                    }

                    MangaRatesList(
                        data = data.rates
                            .filter { it.status == WATCH_STATUSES_M.keys.elementAt(model.tab) }
                            .sortedBy { it.manga.russian },
                        userVM = userVM,
                        state = model.listState,
                        reload = model::reload,
                        onNavigate = onNavigate
                    )
                }
            }

            userVM?.let {
                val state by it.newRate.collectAsStateWithLifecycle()

                if (it.show) AlertDialog(
                    onDismissRequest = it::close,
                    confirmButton = {
                        TextButton(
                            onClick = { it.update(state.id); it.reload(manga = model) },
                            enabled = !state.status.isNullOrEmpty()
                        ) { Text(stringResource(text_save)) }
                    },
                    dismissButton = { TextButton(it::close) { Text(stringResource(text_cancel)) } },
                    title = { Text(stringResource(text_change)) },
                    text = {
                        Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                            RateStatus(it::onEvent, state.statusName, LINKED_TYPE[1])
                            RateChapters(it::onEvent, state.chapters)
                            RateVolumes(it::onEvent, state.volumes)
                            RateScore(it::onEvent, state.scoreName)
                            RateRewatches(it::onEvent, state.rewatches, LINKED_TYPE[1])
                            RateText(it::onEvent, state.text)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MangaRatesList(
    data: List<MangaRate>,
    userVM: UserRateViewModel?,
    state: LazyListState,
    reload: () -> Unit,
    onNavigate: (Screen) -> Unit
) = LazyColumn(
    state = state,
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = spacedBy(16.dp)
) {
    if (data.isEmpty()) item {
        Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_empty)) }
    }
    else items(data) { manga ->
        Row(
            horizontalArrangement = spacedBy(16.dp),
            modifier = Modifier
                .height(175.dp)
                .combinedClickable(
                    onClick = { onNavigate(Screen.Manga(manga.id.toString())) },
                    onLongClick = {
                        userVM?.let { itVM ->
                            itVM.onEvent(SetRateId(manga.id.toString()))
                            itVM.onEvent(SetStatus(WATCH_STATUSES_M.entries.first { it.key == manga.status }))
                            itVM.onEvent(SetScore(SCORES.entries.first { it.key == manga.score }))
                            itVM.onEvent(SetChapters(manga.chapters.toString()))
                            itVM.onEvent(SetVolumes(manga.volumes.toString()))
                            itVM.onEvent(SetRewatches(manga.rewatches.toString()))
                            itVM.onEvent(SetText(manga.text))
                            itVM.open()
                        }
                    }
                )
        ) {
            RoundedPoster(manga.manga.image.original, 122.dp)
            Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
                Column(verticalArrangement = spacedBy(4.dp)) {
                    Text(
                        text = if (manga.manga.russian.isNullOrEmpty()) manga.manga.name else manga.manga.russian,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = getKind(manga.manga.kind),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(text_chapters, manga.chapters, getFull(manga.chapters, manga.status)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(text_rate_score, manga.score.let { if (it != 0) it else '-' }),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                userVM?.let {
                    if (manga.status == WATCH_STATUSES_A.keys.elementAt(1))
                        Row(Modifier.fillMaxWidth(), Arrangement.End) {
                            Box(
                                contentAlignment = Center,
                                content = { Icon(Icons.Outlined.Add, null) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
                                    .clickable { it.increment(manga.id); reload() }
                            )
                        }
                }
            }
        }
    }
}
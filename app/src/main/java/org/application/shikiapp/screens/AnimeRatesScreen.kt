package org.application.shikiapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R.string.text_anime_list
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_rate_episodes
import org.application.shikiapp.R.string.text_rate_score
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.models.data.AnimeRate
import org.application.shikiapp.models.views.NewRateEvent.SetEpisodes
import org.application.shikiapp.models.views.NewRateEvent.SetRateId
import org.application.shikiapp.models.views.NewRateEvent.SetRewatches
import org.application.shikiapp.models.views.NewRateEvent.SetScore
import org.application.shikiapp.models.views.NewRateEvent.SetStatus
import org.application.shikiapp.models.views.UserRateState
import org.application.shikiapp.models.views.UserRateViewModel
import org.application.shikiapp.models.views.UserRatesViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.SCORES
import org.application.shikiapp.utils.WATCH_STATUSES
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun AnimeRatesScreen(id: Long, navigator: DestinationsNavigator) {
    val model = viewModel<UserRatesViewModel>(factory = factory { UserRatesViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()

    when(val data = response) {
        UserRateState.NoAccess -> Box(Modifier.fillMaxSize()) { Text("Профиль закрыт") }
        UserRateState.Error -> ErrorScreen(model.getUserRates())
        UserRateState.Loading -> LoadingScreen()
        is UserRateState.Success -> {
            var tab by remember { mutableIntStateOf(0) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(text_anime_list)) },
                        navigationIcon = { NavigationIcon(navigator::popBackStack) }
                    )
                }
            ) { paddingValues ->
                Column(
                    Modifier.padding(top = paddingValues.calculateTopPadding()),
                    spacedBy(16.dp)
                ) {
                    ScrollableTabRow(selectedTabIndex = tab, edgePadding = 8.dp) {
                        WATCH_STATUSES.entries.forEachIndexed { index, entry ->
                            Tab(tab == index, { tab = index })
                            { Text(entry.value, Modifier.padding(8.dp, 12.dp)) }
                        }
                    }

                    AnimeRatesList(
                        data = data.rates
                            .filter { it.status == WATCH_STATUSES.keys.elementAt(tab) }
                            .sortedBy { it.anime.russian },
                        model = model,
                        userId = id,
                        navigator = navigator
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimeRatesList(
    data: List<AnimeRate>, model: UserRatesViewModel, userId: Long, navigator: DestinationsNavigator
) {
    val userRateVM = if (Preferences.getUserId() == userId) viewModel<UserRateViewModel>() else null

    LazyColumn(contentPadding = PaddingValues(8.dp), verticalArrangement = spacedBy(16.dp)) {
        if (data.isEmpty()) item {
            Box(Modifier.fillMaxSize(), Center) { Text(stringResource(text_empty)) }
        }
        else items(data) { (id, score, status, _, episodes, _, _, _, rewatches, anime) ->
            Row(
                modifier = Modifier.combinedClickable(
                    onClick = { navigator.navigate(AnimeScreenDestination(anime.id.toString())) },
                    onLongClick = {
                        userRateVM?.let { itVM ->
                            itVM.onEvent(SetRateId(id.toString()))
                            itVM.onEvent(SetStatus(WATCH_STATUSES.entries.first { it.key == status }))
                            itVM.onEvent(SetScore(SCORES.entries.first { it.key == score }))
                            itVM.onEvent(SetEpisodes(episodes.toString()))
                            itVM.onEvent(SetRewatches(rewatches.toString()))
                            itVM.open()
                        }
                    }
                ),
                horizontalArrangement = spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = getImage(anime.image.original),
                    modifier = Modifier
                        .size(122.dp, 175.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High
                )
                Column(verticalArrangement = spacedBy(4.dp)) {
                    Text(
                        text = if (anime.russian.isNullOrEmpty()) anime.name else anime.russian,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = getKind(anime.kind),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(text_rate_episodes, episodes, anime.episodes),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(text_rate_score, score.let { if (it != 0) it else '-' }),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    userRateVM?.let {
        val state by it.newRate.collectAsStateWithLifecycle()

        if (it.show) AlertDialog(
            onDismissRequest = it::close,
            confirmButton = {
                TextButton(
                    onClick = { it.updateRate(state.id); it.reload(model) },
                    enabled = !state.status.isNullOrEmpty()
                ) { Text(stringResource(text_save)) }
            },
            dismissButton = { TextButton(it::close) { Text(stringResource(text_cancel)) } },
            title = { Text(stringResource(text_change)) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                    RateStatus(it, state)
                    RateEpisodes(it, state)
                    RateScore(it, state)
                    RateRewatches(it, state)
                }
            }
        )
    }
}
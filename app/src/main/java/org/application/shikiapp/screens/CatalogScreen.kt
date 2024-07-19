package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import org.application.AnimeListQuery.Anime
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_filter
import org.application.shikiapp.R.string.text_search
import org.application.shikiapp.models.views.AnimeListViewModel
import org.application.shikiapp.models.views.CatalogState
import org.application.shikiapp.models.views.CatalogViewModel
import org.application.shikiapp.models.views.DrawerEvent
import org.application.shikiapp.models.views.Items
import org.application.shikiapp.models.views.QueryEvent.SetDuration
import org.application.shikiapp.models.views.QueryEvent.SetGenre
import org.application.shikiapp.models.views.QueryEvent.SetKind
import org.application.shikiapp.models.views.QueryEvent.SetOrder
import org.application.shikiapp.models.views.QueryEvent.SetRating
import org.application.shikiapp.models.views.QueryEvent.SetScore
import org.application.shikiapp.models.views.QueryEvent.SetSeason
import org.application.shikiapp.models.views.QueryEvent.SetSeasonS
import org.application.shikiapp.models.views.QueryEvent.SetSeasonYF
import org.application.shikiapp.models.views.QueryEvent.SetSeasonYS
import org.application.shikiapp.models.views.QueryEvent.SetStatus
import org.application.shikiapp.models.views.QueryEvent.SetTitle
import org.application.shikiapp.models.views.QueryMap
import org.application.shikiapp.utils.DURATIONS
import org.application.shikiapp.utils.KINDS
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.RATINGS
import org.application.shikiapp.utils.SEASONS
import org.application.shikiapp.utils.STATUSES
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getSeason

@Composable
@Destination<RootGraph>
fun CatalogScreen(navigator: DestinationsNavigator) {
    val viewModel = viewModel<CatalogViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when(it) {
                DrawerEvent.ClearDrawer -> focus.clearFocus()
                DrawerEvent.ClickDrawer -> if (state.drawerState.isOpen) state.drawerState.close()
                else state.drawerState.open()
            }
        }
    }

    ModalNavigationDrawer(drawerMenu(viewModel, state), Modifier, state.drawerState) {
        Scaffold(topBar = topBar(viewModel, state)) { values ->
            when (state.menu) {
                0 -> AnimeList(viewModel, state, navigator, values)

                1 -> MangaList(navigator, values)
                2 -> RanobeList(navigator, values)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun topBar(model: CatalogViewModel, state: CatalogState): @Composable () -> Unit = {
    CenterAlignedTopAppBar(
        title = {
            TextField(
                value = state.search,
                onValueChange = model::setSearch,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.menu == 0,
                placeholder = { Text(stringResource(text_search)) },
                trailingIcon = { if (state.search.isEmpty()) Icon(Icons.Default.Search, null) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
        },
        modifier = Modifier.drawBehind {
            drawLine(Color.LightGray, Offset(0f, size.height), Offset(size.width, size.height), 4f)
        },
        navigationIcon = { IconButton(model::drawer) { Icon(Icons.Default.Menu, null) } },
        actions = { IconButton(model::showDialog) { Icon(painterResource(vector_filter), null) } }
    )
}

@Composable
private fun drawerMenu(model: CatalogViewModel, state: CatalogState): @Composable () -> Unit = {
    ModalDrawerSheet(Modifier.width(260.dp)) {
        Text(
            text = stringResource(R.string.text_catalog),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        Items.entries.forEach { item ->
            NavigationDrawerItem(
                label = { Text(text = item.title, style = MaterialTheme.typography.labelLarge) },
                selected = state.menu == item.ordinal,
                onClick = { model.pick(item.ordinal) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                icon = { Icon(painterResource(item.icon), null) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeList(
    model: CatalogViewModel,
    state: CatalogState,
    navigator: DestinationsNavigator,
    values: PaddingValues
) {
    val animeVM = viewModel<AnimeListViewModel>()
    val animeList = animeVM.list.collectAsLazyPagingItems()
    val filters by animeVM.filters.collectAsStateWithLifecycle()

    LaunchedEffect(state.search) { animeVM.onEvent(SetTitle(state.search)) }

    when (animeList.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(animeList.retry())
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding().plus(8.dp)),
                verticalArrangement = spacedBy(16.dp)
            ) {
                animeList(animeList, navigator)
                if (animeList.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (animeList.loadState.hasError) item { ErrorScreen(animeList.retry()) }
            }
        }
    }

    if (state.showFiltersAnime) Dialog(model::hideDialog, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.text_filters)) },
                    navigationIcon = { NavigationIcon(model::hideDialog) }
                )
            },
            floatingActionButton = {
                FloatingActionButton({ model.hideDialog(); animeList.refresh() })
                { Icon(Icons.Default.Search, null) }
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                item { AnimeSorting(animeVM, filters) }
                item { AnimeStatus(animeVM, filters) }
                item { AnimeKind(animeVM, filters) }
                item { AnimeSeason(animeVM, filters) }
                item { AnimeScore(animeVM, filters) }
                item { AnimeDuration(animeVM, filters) }
                item { AnimeRating(animeVM, filters) }
                item { AnimeGenres(animeVM, filters) }
            }
        }
    }
}

private fun LazyListScope.animeList(list: LazyPagingItems<Anime>, navigator: DestinationsNavigator) =
    items(list.itemCount, { it }) { index ->
        list[index]?.let { (id, name, russian, kind, season, poster) ->
            Row(
                Modifier
                    .height(198.dp)
                    .clickable { navigator.navigate(AnimeScreenDestination(id)) }, spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = poster?.originalUrl,
                    modifier = Modifier
                        .width(140.dp)
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium
                        ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    filterQuality = FilterQuality.High
                )
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = russian ?: name,
                        maxLines = 3,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(text = getKind(kind?.rawValue), style = MaterialTheme.typography.bodyLarge)
                    Text(text = getSeason(season), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeSorting(viewModel: AnimeListViewModel, filters: QueryMap) {
    var flag by remember { mutableStateOf(false) }

    ParagraphTitle(stringResource(R.string.text_sorting), Modifier.padding(bottom = 8.dp))
    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = filters.orderName,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            ORDERS.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(text = entry.value, style = MaterialTheme.typography.bodyLarge) },
                    onClick = { viewModel.onEvent(SetOrder(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun AnimeStatus(viewModel: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_status))
    Column {
        STATUSES.entries.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in filters.status,
                        onValueChange = { viewModel.onEvent(SetStatus(key)) },
                        role = Role.Checkbox
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(key in filters.status, null)
                Text(value, Modifier.padding(start = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimeKind(viewModel: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_kind))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        KINDS.entries.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in filters.kind,
                onClick = { viewModel.onEvent(SetKind(key)) },
                label = { Text(value) })
        }
    }
}

@Composable
private fun AnimeSeason(viewModel: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_season), Modifier.padding(bottom = 8.dp))
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = filters.seasonYS,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        viewModel.onEvent(SetSeasonYS(it))
                        viewModel.onEvent(SetSeason)
                    }
                },
                modifier = Modifier.width(160.dp),
                label = { Text(stringResource(R.string.text_start_year)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = filters.seasonYF,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        viewModel.onEvent(SetSeasonYF(it))
                        viewModel.onEvent(SetSeason)
                    }
                },
                modifier = Modifier.width(160.dp),
                label = { Text(stringResource(R.string.text_end_year)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            SEASONS.entries.forEach { (key, value) ->
                ElevatedFilterChip(
                    selected = key in filters.seasonS,
                    onClick = { viewModel.onEvent(SetSeasonS(key)); viewModel.onEvent(SetSeason) },
                    label = { Text(value) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeScore(viewModel: AnimeListViewModel, filters: QueryMap) {
    val interactionSource = remember(::MutableInteractionSource)

    ParagraphTitle(stringResource(R.string.text_score))
    Column {
        Slider(
            value = filters.score,
            onValueChange = { viewModel.onEvent(SetScore(it)) },
            steps = 8,
            valueRange = 1f..10f,
            interactionSource = interactionSource,
            thumb = {
                Label(
                    label = {
                        PlainTooltip(Modifier.sizeIn(maxWidth = 20.dp)) {
                            Text(
                                text = filters.score.toInt().toString(),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    interactionSource = interactionSource
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        tint = Color(0xFFFFC319)
                    )
                }
            }
        )
    }
}

@Composable
private fun AnimeDuration(viewModel: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_episode_duration))
    Column {
        DURATIONS.entries.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in filters.duration,
                        onValueChange = { viewModel.onEvent(SetDuration(key)) },
                        role = Role.Checkbox
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(key in filters.duration, null)
                Text(value, Modifier.padding(start = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimeRating(viewModel: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_rating))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        RATINGS.entries.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in filters.rating,
                onClick = { viewModel.onEvent(SetRating(key)) },
                label = { Text(value) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimeGenres(viewModel: AnimeListViewModel, filters: QueryMap) {
    val genres by viewModel.genres.collectAsStateWithLifecycle()

    ParagraphTitle(stringResource(R.string.text_genres))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        genres.forEach { (id, russian) ->
            ElevatedFilterChip(
                selected = id in filters.genre,
                onClick = { viewModel.onEvent(SetGenre(id)) },
                label = { Text(russian) })
        }
    }
}

@Composable
private fun MangaList(navigator: DestinationsNavigator, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        verticalArrangement = spacedBy(16.dp)
    ) {
        item { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Манга") } }
    }
}


@Composable
private fun RanobeList(navigator: DestinationsNavigator, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        verticalArrangement = spacedBy(16.dp)
    ) {
        item { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Ранобэ") } }
    }
}
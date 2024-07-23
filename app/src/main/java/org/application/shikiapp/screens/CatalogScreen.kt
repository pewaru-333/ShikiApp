package org.application.shikiapp.screens

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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ListItem
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import kotlinx.coroutines.flow.collectLatest
import org.application.AnimeListQuery.Anime
import org.application.CharacterListQuery.Character
import org.application.PeopleQuery.Person
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_filter
import org.application.shikiapp.R.string.text_search
import org.application.shikiapp.models.views.AnimeListViewModel
import org.application.shikiapp.models.views.CatalogState
import org.application.shikiapp.models.views.CatalogViewModel
import org.application.shikiapp.models.views.CatalogViewModel.DrawerEvent.ClearDrawer
import org.application.shikiapp.models.views.CatalogViewModel.DrawerEvent.ClickDrawer
import org.application.shikiapp.models.views.CharacterListViewModel
import org.application.shikiapp.utils.PeopleFilterItems
import org.application.shikiapp.models.views.PeopleFilters
import org.application.shikiapp.models.views.PeopleViewModel
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
import org.application.shikiapp.utils.CatalogItems
import org.application.shikiapp.utils.DURATIONS
import org.application.shikiapp.utils.KINDS
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.RATINGS
import org.application.shikiapp.utils.SEASONS
import org.application.shikiapp.utils.STATUSES
import com.ramcosta.composedestinations.navigation.DestinationsNavigator as Navigator

private var PADDING = 0.dp

@Composable
@Destination<RootGraph>
fun CatalogScreen(navigator: Navigator) {
    val model = viewModel<CatalogViewModel>()
    val state by model.state.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        model.event.collectLatest {
            when (it) {
                ClearDrawer -> focus.clearFocus()
                ClickDrawer -> if (state.drawerState.isOpen) state.drawerState.close()
                else state.drawerState.open()
            }
        }
    }

    ModalNavigationDrawer(drawerMenu(model, state), Modifier, state.drawerState) {
        Scaffold(topBar = topBar(model, state)) { values ->
            PADDING = values.calculateTopPadding().plus(8.dp)

            when (state.menu) {
                0 -> AnimeList(model, state, navigator)
                1 -> MangaList(navigator, values)
                2 -> RanobeList(navigator, values)
                3 -> CharacterList(state, navigator)
                4 -> PeopleList(model, state, navigator)
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
                enabled = state.menu in listOf(0, 3, 4),
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
        actions = {
            if (state.menu in listOf(0, 4)) IconButton({ model.showFilters(state.menu) }) {
                Icon(painterResource(vector_filter), null)
            }
        }
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

        CatalogItems.entries.forEach { item ->
            NavigationDrawerItem(
                label = { Text(stringResource(item.title), style = MaterialTheme.typography.labelLarge) },
                selected = state.menu == item.ordinal,
                onClick = { model.pick(item.ordinal) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                icon = { Icon(painterResource(item.icon), null) }
            )
        }
    }
}


@Composable
private fun AnimeList(model: CatalogViewModel, state: CatalogState, navigator: Navigator) {
    val animeVM = viewModel<AnimeListViewModel>()
    val list = animeVM.list.collectAsLazyPagingItems()
    val filters by animeVM.filters.collectAsStateWithLifecycle()

    LaunchedEffect(state.search) { animeVM.onEvent(SetTitle(state.search)) }

    when (list.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(list::retry)
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp, PADDING),
                verticalArrangement = spacedBy(16.dp)
            ) {
                animeList(list, navigator)
                if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (list.loadState.hasError) item { ErrorScreen(list::retry) }
            }
        }
    }

    if (state.showFiltersAnime) AnimeFiltersDialog(model, animeVM, list, filters)
}

private fun LazyListScope.animeList(list: LazyPagingItems<Anime>, navigator: Navigator) =
    items(list.itemCount, { it }) { index ->
        list[index]?.let { (id, name, russian, kind, season, poster) ->
            Row(
                Modifier
                    .height(198.dp)
                    .clickable { navigator.navigate(AnimeScreenDestination(id)) }, spacedBy(16.dp)
            ) {
                RoundedAnimePoster(poster?.originalUrl)
                AnimeShortDescription(russian ?: name, kind?.rawValue, season)
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AnimeFiltersDialog(
    model: CatalogViewModel,
    animeVM: AnimeListViewModel,
    list: LazyPagingItems<Anime>,
    filters: QueryMap
) = Dialog(model::hideFilters, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_filters)) },
                navigationIcon = { NavigationIcon(model::hideFilters) }
            )
        },
        floatingActionButton = {
            FloatingActionButton({ model.hideFilters(); list.refresh() })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeSorting(model: AnimeListViewModel, filters: QueryMap) {
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
                    onClick = { model.onEvent(SetOrder(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun AnimeStatus(model: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_status))
    Column {
        STATUSES.entries.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in filters.status,
                        onValueChange = { model.onEvent(SetStatus(key)) },
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
private fun AnimeKind(model: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_kind))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        KINDS.entries.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in filters.kind,
                onClick = { model.onEvent(SetKind(key)) },
                label = { Text(value) })
        }
    }
}

@Composable
private fun AnimeSeason(model: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_season), Modifier.padding(bottom = 8.dp))
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = filters.seasonYS,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        model.onEvent(SetSeasonYS(it))
                        model.onEvent(SetSeason)
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
                        model.onEvent(SetSeasonYF(it))
                        model.onEvent(SetSeason)
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
                    onClick = { model.onEvent(SetSeasonS(key)); model.onEvent(SetSeason) },
                    label = { Text(value) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeScore(model: AnimeListViewModel, filters: QueryMap) {
    val interactionSource = remember(::MutableInteractionSource)

    ParagraphTitle(stringResource(R.string.text_score))
    Column {
        Slider(
            value = filters.score,
            onValueChange = { model.onEvent(SetScore(it)) },
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
private fun AnimeDuration(model: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_episode_duration))
    Column {
        DURATIONS.entries.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in filters.duration,
                        onValueChange = { model.onEvent(SetDuration(key)) },
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
private fun AnimeRating(model: AnimeListViewModel, filters: QueryMap) {
    ParagraphTitle(stringResource(R.string.text_rating))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        RATINGS.entries.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in filters.rating,
                onClick = { model.onEvent(SetRating(key)) },
                label = { Text(value) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimeGenres(model: AnimeListViewModel, filters: QueryMap) {
    val genres by model.genres.collectAsStateWithLifecycle()

    ParagraphTitle(stringResource(R.string.text_genres))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        genres.forEach { (id, russian) ->
            ElevatedFilterChip(
                selected = id in filters.genre,
                onClick = { model.onEvent(SetGenre(id)) },
                label = { Text(russian) })
        }
    }
}

@Composable
private fun MangaList(navigator: Navigator, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        verticalArrangement = spacedBy(16.dp)
    ) {
        item { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Манга") } }
    }
}


@Composable
private fun RanobeList(navigator: Navigator, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        verticalArrangement = spacedBy(16.dp)
    ) {
        item { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Ранобэ") } }
    }
}

@Composable
private fun CharacterList(state: CatalogState, navigator: Navigator) {
    val model = viewModel<CharacterListViewModel>()
    val list = model.list.collectAsLazyPagingItems()

    LaunchedEffect(state.search) { model.setSearch(state.search) }

    when (list.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(list::retry)
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp, PADDING),
                verticalArrangement = spacedBy(8.dp)
            ) {
                characterList(list, navigator)
                if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (list.loadState.hasError) item { ErrorScreen(list::retry) }
            }
        }
    }
}

private fun LazyListScope.characterList(list: LazyPagingItems<Character>, navigator: Navigator) =
    items(list.itemCount) { index ->
        list[index]?.let { (id, name, russian, poster) ->
            ListItem(
                headlineContent = {
                    Text(russian ?: name, style = MaterialTheme.typography.titleLarge)
                },
                modifier = Modifier.clickable { navigator.navigate(CharacterScreenDestination(id)) },
                leadingContent = { RoundedPersonImage(poster?.originalUrl) }
            )
        }
    }

@Composable
private fun PeopleList(model: CatalogViewModel, state: CatalogState, navigator: Navigator) {
    val peopleVM = viewModel<PeopleViewModel>()
    val list = peopleVM.list.collectAsLazyPagingItems()
    val filters by peopleVM.filters.collectAsStateWithLifecycle()

    LaunchedEffect(state.search) { peopleVM.setSearch(state.search) }

    when (list.loadState.refresh) {
        is LoadState.Error -> ErrorScreen(list::retry)
        is LoadState.Loading -> LoadingScreen()
        is LoadState.NotLoading -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp, PADDING),
                verticalArrangement = spacedBy(8.dp)
            ) {
                peopleList(list, navigator)
                if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (list.loadState.hasError) item { ErrorScreen(list::retry) }
            }
        }
    }

    if (state.showFiltersPeople) PeopleFiltersDialog(model, peopleVM, filters)
}

private fun LazyListScope.peopleList(list: LazyPagingItems<Person>, navigator: Navigator) =
    items(list.itemCount) { index ->
        list[index]?.let { (id, name, russian, poster) ->
            ListItem(
                headlineContent = {
                    Text(russian ?: name, style = MaterialTheme.typography.titleLarge)
                },
                modifier = Modifier.clickable { navigator.navigate(PersonScreenDestination(id.toLong())) },
                leadingContent = { RoundedPersonImage(poster?.originalUrl) }
            )
        }
    }

@Composable
private fun PeopleFiltersDialog(
    model: CatalogViewModel,
    peopleVM: PeopleViewModel,
    filters: PeopleFilters
) = AlertDialog(
    onDismissRequest = model::hideFilters,
    confirmButton = {},
    dismissButton = { TextButton(model::hideFilters) { Text(stringResource(R.string.text_close)) } },
    title = { Text(stringResource(R.string.text_filters)) },
    text = {
        Column {
            PeopleFilterItems.entries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = entry.title in filters.query,
                            onValueChange = { peopleVM.setFlag(it, index) },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(entry.title in filters.query, null)
                    Text(
                        text = entry.title,
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
)
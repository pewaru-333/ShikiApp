package org.application.shikiapp.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.key
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
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.application.AnimeListQuery.Data.Anime
import org.application.CharacterListQuery.Data.Character
import org.application.MangaListQuery.Data.Manga
import org.application.PeopleQuery.Data.Person
import org.application.fragment.GenresF
import org.application.shikiapp.R.drawable.vector_filter
import org.application.shikiapp.R.string.text_catalog
import org.application.shikiapp.R.string.text_close
import org.application.shikiapp.R.string.text_end_year
import org.application.shikiapp.R.string.text_episode_duration
import org.application.shikiapp.R.string.text_filters
import org.application.shikiapp.R.string.text_genres
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_rating
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_search
import org.application.shikiapp.R.string.text_season
import org.application.shikiapp.R.string.text_sorting
import org.application.shikiapp.R.string.text_start_year
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.models.data.LinkedType
import org.application.shikiapp.models.states.CatalogState
import org.application.shikiapp.models.viewModels.AnimeListViewModel
import org.application.shikiapp.models.viewModels.CatalogFilters
import org.application.shikiapp.models.viewModels.CatalogViewModel
import org.application.shikiapp.models.viewModels.CatalogViewModel.DrawerEvent.Clear
import org.application.shikiapp.models.viewModels.CatalogViewModel.DrawerEvent.Click
import org.application.shikiapp.models.viewModels.CharacterListViewModel
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetDuration
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetGenre
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetKind
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetOrder
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetRating
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetRole
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetScore
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetSeason
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetSeasonS
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetSeasonYF
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetSeasonYS
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetStatus
import org.application.shikiapp.models.viewModels.FiltersViewModel.FilterEvent.SetTitle
import org.application.shikiapp.models.viewModels.MangaListViewModel
import org.application.shikiapp.models.viewModels.PeopleViewModel
import org.application.shikiapp.models.viewModels.RanobeListViewModel
import org.application.shikiapp.utils.DURATIONS
import org.application.shikiapp.utils.KINDS_A
import org.application.shikiapp.utils.KINDS_M
import org.application.shikiapp.utils.KINDS_R
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.RATINGS
import org.application.shikiapp.utils.SEASONS
import org.application.shikiapp.utils.STATUSES_A
import org.application.shikiapp.utils.STATUSES_M
import org.application.shikiapp.utils.enums.CatalogItems
import org.application.shikiapp.utils.enums.CatalogItems.ANIME
import org.application.shikiapp.utils.enums.CatalogItems.CHARACTERS
import org.application.shikiapp.utils.enums.CatalogItems.MANGA
import org.application.shikiapp.utils.enums.CatalogItems.PEOPLE
import org.application.shikiapp.utils.enums.CatalogItems.RANOBE
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.PeopleFilterItems
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onNavigate: (Screen) -> Unit) {
    val model = viewModel<CatalogViewModel>()
    val state by model.state.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        model.event.collectLatest {
            when (it) {
                Clear -> focus.clearFocus()
                Click -> if (state.drawerState.isOpen) state.drawerState.close()
                else state.drawerState.open()
                null -> Unit
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = state.drawerState,
        drawerContent = {
            ModalDrawerSheet(Modifier.width(260.dp)) {
                Text(
                    text = stringResource(text_catalog),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )

                CatalogItems.entries.forEach {
                    NavigationDrawerItem(
                        selected = state.menu == it,
                        onClick = { model.pick(it) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = { Icon(painterResource(it.icon), null) },
                        label = {
                            Text(
                                text = stringResource(it.title),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        TextField(
                            value = state.search,
                            onValueChange = model::setSearch,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(text_search)) },
                            singleLine = true,
                            trailingIcon = {
                                if (state.search.isEmpty()) Icon(Icons.Outlined.Search, null)
                            },
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
                    navigationIcon = {
                        IconButton(model::onDrawerClick) { Icon(Icons.Outlined.Menu, null) }
                    },
                    actions = {
                        if (state.menu != CHARACTERS)
                            IconButton(
                                onClick = { model.showFilters(state.menu) }
                            ) {
                                Icon(painterResource(vector_filter), null)
                            }
                    }
                )
            }
        ) { values ->
            CatalogList(
                state = state,
                paddingValues = values,
                onNavigate = onNavigate,
                hide = model::hideFilters
            )
        }
    }
}

// ============================================= Lists =============================================

@Suppress("UNCHECKED_CAST")
@Composable
private fun CatalogList(
    state: CatalogState,
    paddingValues: PaddingValues,
    onNavigate: (Screen) -> Unit,
    hide: () -> Unit
) {
    val model = when (state.menu) {
        ANIME -> viewModel<AnimeListViewModel>()
        MANGA -> viewModel<MangaListViewModel>()
        RANOBE -> viewModel<RanobeListViewModel>()
        CHARACTERS -> viewModel<CharacterListViewModel>()
        PEOPLE -> viewModel<PeopleViewModel>()
    }
    val list = (model.list as Flow<PagingData<Operation.Data>>).collectAsLazyPagingItems()
    val filters by model.filters.collectAsStateWithLifecycle()
    val genres by model.genres.collectAsStateWithLifecycle()

    LaunchedEffect(state.search) { model.onEvent(SetTitle(state.search)) }

    if (state.showFiltersA || state.showFiltersM || state.showFiltersR) DialogFilters(
        genres = genres,
        filters = filters,
        event = model::onEvent,
        refresh = list::refresh,
        hide = hide,
        type = when (model) {
            is RanobeListViewModel -> LinkedType.RANOBE
            is MangaListViewModel -> LinkedType.MANGA
            is AnimeListViewModel -> LinkedType.ANIME
            else -> null
        }
    )
    else if (state.showFiltersP) DialogFiltersP(hide, model::onEvent, filters.roles)

    when (list.loadState.refresh) {
        LoadState.Loading -> LoadingScreen()
        is LoadState.Error -> ErrorScreen(list::retry)
        is LoadState.NotLoading -> if (Preferences.listView == ListView.COLUMN)
            LazyColumn(
                contentPadding = paddingValues,
                state = when (state.menu) {
                    ANIME -> state.listA
                    MANGA -> state.listM
                    RANOBE -> state.listR
                    CHARACTERS -> state.listC
                    PEOPLE -> state.listP
                }
            ) {
                when (state.menu) {
                    ANIME -> animeList(list as LazyPagingItems<Anime>, onNavigate)
                    MANGA -> mangaList(list as LazyPagingItems<Manga>, onNavigate)
                    RANOBE -> mangaList(list as LazyPagingItems<Manga>, onNavigate)
                    CHARACTERS -> characterList(list as LazyPagingItems<Character>, onNavigate)
                    PEOPLE -> peopleList(list as LazyPagingItems<Person>, onNavigate)
                }
                if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                if (list.loadState.hasError) item { ErrorScreen(list::retry) }
            }
        else LazyVerticalGrid(
            columns = GridCells.FixedSize(116.dp),
            contentPadding = PaddingValues(0.dp, paddingValues.calculateTopPadding().plus(8.dp)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = spacedBy(4.dp),
            state = when (state.menu) {
                ANIME -> state.gridA
                MANGA -> state.gridM
                RANOBE -> state.gridR
                CHARACTERS -> state.gridC
                PEOPLE -> state.gridP
            }
        ) {
            when (state.menu) {
                ANIME -> animeList(list as LazyPagingItems<Anime>, onNavigate)
                MANGA -> mangaList(list as LazyPagingItems<Manga>, onNavigate)
                RANOBE -> mangaList(list as LazyPagingItems<Manga>, onNavigate)
                CHARACTERS -> characterList(list as LazyPagingItems<Character>, onNavigate)
                PEOPLE -> peopleList(list as LazyPagingItems<Person>, onNavigate)
            }
            if (list.loadState.append == LoadState.Loading)
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    LoadingScreen()
                }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

// ======================================= Dialogs Filters ========================================

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogFilters(
    genres: List<GenresF>,
    filters: CatalogFilters,
    type: LinkedType?,
    event: (FilterEvent) -> Unit,
    refresh: () -> Unit,
    hide: () -> Unit
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_filters)) },
                navigationIcon = { NavigationIcon(hide) },
                actions = {
                    IconButton(
                        onClick = { hide(); refresh() }
                    ) {
                        Icon(Icons.Outlined.Check, null)
                    }
                }
            )
        }
    ) { values ->
        Column(
            verticalArrangement = spacedBy(16.dp),
            modifier = Modifier
                .padding(8.dp, values.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Sorting(event, filters.orderName)
            Status(event, filters.status, type)
            Kind(event, filters.kind, type)
            Season(event, filters.seasonYS, filters.seasonYF, filters.seasonS)
            Score(event, filters.score)
            if (type == LinkedType.ANIME) {
                Duration(event, filters.duration)
                Rating(event, filters.rating)
            }
            Genres(event, genres, filters.genres)
        }

//        LazyColumn(
//            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
//            verticalArrangement = spacedBy(16.dp)
//        ) {
//            item { Sorting(event, filters.orderName) }
//            item { Status(event, filters.status, type) }
//            item { Kind(event, filters.kind, type) }
//            item { Season(event, filters.seasonYS, filters.seasonYF, filters.seasonS) }
//            item { Score(event, filters.score) }
//            if (type == LinkedType.ANIME) {
//                item { Duration(event, filters.duration) }
//                item { Rating(event, filters.rating) }
//            }
//            item { Genres(event, genres, filters.genres) }
//        }
    }
}

@Composable
private fun DialogFiltersP(
    hide: () -> Unit,
    event: (FilterEvent) -> Unit,
    roles: List<PeopleFilterItems>
) = AlertDialog(
    onDismissRequest = hide,
    confirmButton = {},
    dismissButton = { TextButton(hide) { Text(stringResource(text_close)) } },
    title = { Text(stringResource(text_filters)) },
    text = {
        Column {
            PeopleFilterItems.entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = entry in roles,
                            onValueChange = { event(SetRole(it, entry)) },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(entry in roles, null)
                    Text(
                        text = stringResource(entry.title),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
)

// ============================================ Filters ===========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Sorting(event: (FilterEvent) -> Unit, orderName: String) {
    var flag by remember { mutableStateOf(false) }

    ParagraphTitle(stringResource(text_sorting))
    ExposedDropdownMenuBox(
        expanded = flag,
        onExpandedChange = { flag = it }
    ) {
        OutlinedTextField(
            value = orderName,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = flag,
            onDismissRequest = { flag = false }
        ) {
            ORDERS.entries.forEach { entry ->
                DropdownMenuItem(
                    onClick = { event(SetOrder(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = {
                        Text(
                            text = entry.value,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun Status(event: (FilterEvent) -> Unit, status: List<String>, type: LinkedType?) {
    ParagraphTitle(stringResource(text_status))
    Column {
        (if (type == LinkedType.ANIME) STATUSES_A else STATUSES_M).entries.forEach { (key, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in status,
                        onValueChange = { event(SetStatus(key)) },
                        role = Role.Checkbox
                    )
            ) {
                Checkbox(key in status, null)
                Text(value, Modifier.padding(start = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Kind(event: (FilterEvent) -> Unit, kind: List<String>, type: LinkedType?) {
    ParagraphTitle(stringResource(text_kind))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        (when (type) {
            LinkedType.ANIME -> KINDS_A
            LinkedType.MANGA -> KINDS_M
            LinkedType.RANOBE -> KINDS_R
            else -> null
        })?.entries?.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in kind,
                onClick = { event(SetKind(key)) },
                label = { Text(value) })
        }
    }
}

@Composable
private fun Season(
    event: (FilterEvent) -> Unit,
    seasonYS: String,
    seasonYF: String,
    seasonS: List<String>
) {
    ParagraphTitle(stringResource(text_season), Modifier.padding(bottom = 8.dp))
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = seasonYS,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        event(SetSeasonYS(it)); event(SetSeason)
                    }
                },
                modifier = Modifier.width(160.dp),
                label = { Text(stringResource(text_start_year)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = seasonYF,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        event(SetSeasonYF(it)); event(SetSeason)
                    }
                },
                modifier = Modifier.width(160.dp),
                label = { Text(stringResource(text_end_year)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            SEASONS.entries.forEach { (key, value) ->
                ElevatedFilterChip(
                    selected = key in seasonS,
                    onClick = { event(SetSeasonS(key)); event(SetSeason) },
                    label = { Text(value) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Score(event: (FilterEvent) -> Unit, score: Float) {
    val interactionSource = remember(::MutableInteractionSource)

    ParagraphTitle(stringResource(text_score))
    Column {
        Slider(
            value = score,
            onValueChange = { event(SetScore(it)) },
            steps = 8,
            valueRange = 1f..10f,
            interactionSource = interactionSource,
            thumb = {
                Label(
                    interactionSource = interactionSource,
                    label = {
                        PlainTooltip(Modifier.sizeIn(maxWidth = 30.dp)) {
                            Text(text = score.toInt().toString(), textAlign = TextAlign.Center)
                        }
                    }
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
private fun Duration(event: (FilterEvent) -> Unit, duration: List<String>) {
    ParagraphTitle(stringResource(text_episode_duration))
    Column {
        DURATIONS.entries.forEach { (key, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = key in duration,
                        onValueChange = { event(SetDuration(key)) },
                        role = Role.Checkbox
                    )
            ) {
                Checkbox(key in duration, null)
                Text(value, Modifier.padding(start = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Rating(event: (FilterEvent) -> Unit, rating: List<String>) {
    ParagraphTitle(stringResource(text_rating))
    FlowRow(horizontalArrangement = spacedBy(8.dp)) {
        RATINGS.entries.forEach { (key, value) ->
            ElevatedFilterChip(
                selected = key in rating,
                onClick = { event(SetRating(key)) },
                label = { Text(value) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Genres(event: (FilterEvent) -> Unit, allGenres: List<GenresF>, genres: List<String>) {
    ParagraphTitle(stringResource(text_genres))
    FlowRow(Modifier, spacedBy(8.dp)) {
        allGenres.forEach { genre ->
            key(genre.id) {
                ElevatedFilterChip(
                    selected = genre.id in genres,
                    onClick = { event(SetGenre(genre.id)) },
                    label = { Text(genre.russian) }
                )
            }
        }
    }
}

// ========================================== Extensions ===========================================

private fun LazyListScope.animeList(list: LazyPagingItems<Anime>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogListItem(
                title = it.russian ?: it.name,
                kind = it.kind?.rawValue,
                modifier = Modifier.animateItem(),
                season = it.season,
                image = it.poster?.mainUrl,
                isBig = true,
                click = { onNavigate(Screen.Anime(it.id)) }
            )
        }
    }

private fun LazyListScope.mangaList(list: LazyPagingItems<Manga>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogListItem(
                title = it.russian ?: it.name,
                kind = it.kind?.rawValue,
                modifier = Modifier.animateItem(),
                season = null,
                image = it.poster?.mainUrl,
                isBig = true,
                click = { onNavigate(Screen.Manga(it.id)) }
            )
        }
    }

private fun LazyListScope.characterList(list: LazyPagingItems<Character>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogListItem(
                title = it.russian ?: it.name,
                kind = null,
                modifier = Modifier.animateItem(),
                season = null,
                image = it.poster?.mainUrl,
                isBig = false,
                click = { onNavigate(Screen.Character(it.id)) }
            )
        }
    }

private fun LazyListScope.peopleList(list: LazyPagingItems<Person>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogListItem(
                title = it.russian ?: it.name,
                kind = null,
                modifier = Modifier.animateItem(),
                season = null,
                image = it.poster?.mainUrl,
                isBig = false,
                click = { onNavigate(Screen.Person(it.id.toLong())) }
            )
        }
    }

private fun LazyGridScope.animeList(list: LazyPagingItems<Anime>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogGridItem(
                title = it.russian ?: it.name,
                image = it.poster?.mainUrl,
                modifier = Modifier.animateItem(),
                click = { onNavigate(Screen.Anime(it.id)) }
            )
        }
    }

private fun LazyGridScope.mangaList(list: LazyPagingItems<Manga>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogGridItem(
                title = it.russian ?: it.name,
                image = it.poster?.mainUrl,
                modifier = Modifier.animateItem(),
                click = { onNavigate(Screen.Manga(it.id)) }
            )
        }
    }

private fun LazyGridScope.characterList(list: LazyPagingItems<Character>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogGridItem(
                title = it.russian ?: it.name,
                image = it.poster?.mainUrl,
                modifier = Modifier.animateItem(),
                click = { onNavigate(Screen.Character(it.id)) }
            )
        }
    }

private fun LazyGridScope.peopleList(list: LazyPagingItems<Person>, onNavigate: (Screen) -> Unit) =
    items(
        count = list.itemCount,
        key = { it }
    ) { index ->
        list[index]?.let {
            CatalogGridItem(
                title = it.russian ?: it.name,
                image = it.poster?.mainUrl,
                modifier = Modifier.animateItem(),
                click = { onNavigate(Screen.Person(it.id.toLong())) }
            )
        }
    }
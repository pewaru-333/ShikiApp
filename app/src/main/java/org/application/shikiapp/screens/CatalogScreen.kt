package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
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
import org.application.shikiapp.events.DrawerEvent
import org.application.shikiapp.events.FilterEvent
import org.application.shikiapp.events.FilterEvent.SetDuration
import org.application.shikiapp.events.FilterEvent.SetGenre
import org.application.shikiapp.events.FilterEvent.SetKind
import org.application.shikiapp.events.FilterEvent.SetOrder
import org.application.shikiapp.events.FilterEvent.SetRating
import org.application.shikiapp.events.FilterEvent.SetRole
import org.application.shikiapp.events.FilterEvent.SetScore
import org.application.shikiapp.events.FilterEvent.SetSeason
import org.application.shikiapp.events.FilterEvent.SetSeasonS
import org.application.shikiapp.events.FilterEvent.SetSeasonYF
import org.application.shikiapp.events.FilterEvent.SetSeasonYS
import org.application.shikiapp.events.FilterEvent.SetStatus
import org.application.shikiapp.events.FilterEvent.SetTitle
import org.application.shikiapp.generated.fragment.GenresF
import org.application.shikiapp.models.states.CatalogState
import org.application.shikiapp.models.states.FiltersState
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.viewModels.CatalogViewModel
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.enums.CatalogItem
import org.application.shikiapp.utils.enums.CatalogItem.ANIME
import org.application.shikiapp.utils.enums.CatalogItem.CHARACTERS
import org.application.shikiapp.utils.enums.CatalogItem.CLUBS
import org.application.shikiapp.utils.enums.CatalogItem.MANGA
import org.application.shikiapp.utils.enums.CatalogItem.PEOPLE
import org.application.shikiapp.utils.enums.CatalogItem.RANOBE
import org.application.shikiapp.utils.enums.CatalogItem.USERS
import org.application.shikiapp.utils.enums.Duration
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.ListView
import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.enums.PeopleFilterItem
import org.application.shikiapp.utils.enums.Rating
import org.application.shikiapp.utils.enums.Season
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(visibility: NavigationBarVisibility, onNavigate: (Screen) -> Unit) {
    val model = viewModel<CatalogViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val catalogList = model.list.collectAsLazyPagingItems()
    val filters by model.currentFilters.collectAsStateWithLifecycle()
    val genres by model.genres.collectAsStateWithLifecycle()

    val focus = LocalFocusManager.current

    LaunchedEffect(Unit) {
        model.event.collectLatest {
            when (it) {
                DrawerEvent.Clear -> focus.clearFocus()
                DrawerEvent.Click -> if (state.drawerState.isOpen) state.drawerState.close()
                else state.drawerState.open()
                null -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        model.navEvent.collectLatest {
            if (it) model.onEvent(SetStatus("ongoing"))
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

                CatalogItem.entries.forEach {
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

                    if (it == PEOPLE) {
                        HorizontalDivider(Modifier.padding(8.dp))
                    }
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
                            onValueChange = { model.onEvent(SetTitle(it)) },
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
                        if (state.menu !in listOf(CHARACTERS, USERS, CLUBS))
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
                list = catalogList,
                state = state,
                paddingValues = values,
                onNavigate = onNavigate
            )

            DialogFilters(
                genres = genres,
                filters = filters,
                visible = state.showFiltersA || state.showFiltersM || state.showFiltersR,
                event = model::onEvent,
                refresh = catalogList::refresh,
                hide = model::hideFilters,
                type = when (state.menu) {
                    RANOBE -> LinkedType.RANOBE
                    MANGA -> LinkedType.MANGA
                    ANIME -> LinkedType.ANIME
                    else -> null
                }
            )

            if (state.showFiltersP) {
                DialogFiltersP(filters.roles, model::onEvent, model::hideFilters)
            }

            LaunchedEffect(state.showFiltersA, state.showFiltersM, state.showFiltersR) {
                if (state.showFiltersA || state.showFiltersM || state.showFiltersR) visibility.hide()
                else visibility.show()
            }
        }
    }
}

// ============================================= Lists =============================================

@Composable
private fun CatalogList(
    list: LazyPagingItems<Content>,
    state: CatalogState,
    paddingValues: PaddingValues,
    onNavigate: (Screen) -> Unit
) = when (list.loadState.refresh) {
    LoadState.Loading -> LoadingScreen()
    is LoadState.Error -> ErrorScreen(list::retry)
    is LoadState.NotLoading -> {
        when (state.menu) {
            USERS, CLUBS -> LazyVerticalGrid(
                columns = GridCells.FixedSize(70.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(list.itemCount) { index ->
                    list[index]?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onNavigate(
                                    if (state.menu == USERS) Screen.User(it.id.toLong())
                                    else Screen.Club(it.id.toLong())
                                )
                            }
                        ) {
                            AsyncImage(
                                model = it.poster,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            )

                            Text(
                                maxLines = 1,
                                text = it.title,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                if (list.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        LoadingScreen(Modifier.padding(8.dp))
                    }
                }
                if (list.loadState.hasError) {
                    item { ErrorScreen(list::retry) }
                }
            }

            else -> if (Preferences.listView == ListView.COLUMN)
                LazyColumn(
                    contentPadding = paddingValues,
                    state = state.listStates.getValue(state.menu)
                ) {
                    contentList(list) {
                        onNavigate(
                            when (state.menu) {
                                ANIME -> Screen.Anime(it)
                                MANGA, RANOBE -> Screen.Manga(it)
                                CHARACTERS -> Screen.Character(it)
                                PEOPLE -> Screen.Person(it.toLong())
                                USERS -> Screen.User(it.toLong())
                                CLUBS -> Screen.Club(it.toLong())
                            }
                        )
                    }
                    if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                    if (list.loadState.hasError) item { ErrorScreen(list::retry) }
                }
            else LazyVerticalGrid(
                columns = GridCells.FixedSize(116.dp),
                contentPadding = PaddingValues(0.dp, paddingValues.calculateTopPadding().plus(8.dp)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = spacedBy(4.dp),
                state = state.gridStates.getValue(state.menu)
            ) {
                contentList(list) {
                    onNavigate(
                        when (state.menu) {
                            ANIME -> Screen.Anime(it)
                            MANGA, RANOBE -> Screen.Manga(it)
                            CHARACTERS -> Screen.Character(it)
                            PEOPLE -> Screen.Person(it.toLong())
                            USERS -> Screen.User(it.toLong())
                            CLUBS -> Screen.Club(it.toLong())
                        }
                    )
                }
                if (list.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        LoadingScreen()
                    }
                }
                if (list.loadState.hasError) {
                    item { ErrorScreen(list::retry) }
                }
            }
        }
    }
}

// ======================================= Dialogs Filters ========================================

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogFilters(
    genres: List<GenresF>,
    filters: FiltersState,
    visible: Boolean,
    type: LinkedType?,
    event: (FilterEvent) -> Unit,
    refresh: () -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    modifier = Modifier.zIndex(10f),
    visible = visible,
    enter = scaleIn(),
    exit = scaleOut()
) {
    BackHandler(onBack = hide)
    Scaffold(
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
        LazyColumn(
            verticalArrangement = spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = values.calculateTopPadding(),
                end = 8.dp,
                bottom = 16.dp
            )
        ) {
            item { Sorting(event, filters.order) }
            item { Status(event, filters.status, type) }
            item { Kind(event, filters.kind, type) }
            item { Season(filters.seasonYS, filters.seasonYF, filters.seasonS, event) }
            item { Score(event, filters.score) }
            if (type == LinkedType.ANIME) {
                item { Duration(event, filters.duration) }
                item { Rating(event, filters.rating) }
            }
            item { Genres(event, genres, filters.genres) }
        }
    }
}

@Composable
private fun DialogFiltersP(
    roles: Set<PeopleFilterItem>,
    event: (FilterEvent) -> Unit,
    hide: () -> Unit
) = AlertDialog(
    onDismissRequest = hide,
    confirmButton = {},
    dismissButton = { TextButton(hide) { Text(stringResource(text_close)) } },
    title = { Text(stringResource(text_filters)) },
    text = {
        Column {
            PeopleFilterItem.entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = entry in roles,
                            onValueChange = { event(SetRole(entry)) },
                            role = Role.Checkbox
                        )
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
private fun Sorting(event: (FilterEvent) -> Unit, order: Order) {
    var flag by remember { mutableStateOf(false) }

    ParagraphTitle(stringResource(text_sorting))
    ExposedDropdownMenuBox(
        expanded = flag,
        onExpandedChange = { flag = it },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        OutlinedTextField(
            value = stringResource(order.title),
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
            Order.entries.forEach { entry ->
                DropdownMenuItem(
                    onClick = { event(SetOrder(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = {
                        Text(
                            text = stringResource(entry.title),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun Status(event: (FilterEvent) -> Unit, status: Set<String>, type: LinkedType?) {
    ParagraphTitle(stringResource(text_status))
    Column {
        Status.entries.filter { type in it.types }.forEach { entry ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = entry.name.lowercase() in status,
                        onValueChange = { event(SetStatus(entry.name.lowercase())) },
                        role = Role.Checkbox
                    )
            ) {
                Checkbox(entry.name.lowercase() in status, null)
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(
                        if (type == LinkedType.ANIME) entry.animeTitle ?: R.string.text_unknown
                        else entry.mangaTitle
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Kind(event: (FilterEvent) -> Unit, kind: Set<String>, type: LinkedType?) {
    ParagraphTitle(stringResource(text_kind))
    FlowRow(Modifier, spacedBy(8.dp), spacedBy(12.dp)) {
        Kind.entries.filter { it.linkedType == type }.forEach {
            FilterChip(
                modifier = Modifier.height(36.dp),
                selected = it.name.lowercase() in kind,
                onClick = { event(SetKind(it.name.lowercase())) },
                label = { Text(stringResource(it.title)) })
        }
    }
}

@Composable
private fun Season(
    seasonYS: String,
    seasonYF: String,
    seasonS: Set<String>,
    event: (FilterEvent) -> Unit
) {
    ParagraphTitle(stringResource(text_season), Modifier.padding(bottom = 8.dp))
    Column(Modifier, spacedBy(8.dp)) {
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
            Season.entries.forEach {
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = it.name.lowercase() in seasonS,
                    onClick = { event(SetSeasonS(it.name.lowercase())); event(SetSeason) },
                    label = { Text(stringResource(it.title)) })
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
private fun Duration(event: (FilterEvent) -> Unit, duration: Set<String>) {
    ParagraphTitle(stringResource(text_episode_duration))
    Column {
        Duration.entries.forEach { entry ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = entry.name.lowercase() in duration,
                        onValueChange = { event(SetDuration(entry.name.lowercase())) },
                        role = Role.Checkbox
                    )
            ) {
                Checkbox(entry.name.lowercase() in duration, null)
                Text(stringResource(entry.title), Modifier.padding(start = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Rating(event: (FilterEvent) -> Unit, rating: Set<String>) {
    ParagraphTitle(stringResource(text_rating))
    FlowRow(Modifier, spacedBy(8.dp), spacedBy(12.dp)) {
        Rating.entries.forEach {
            FilterChip(
                modifier = Modifier.height(36.dp),
                selected = it.name.lowercase() in rating,
                onClick = { event(SetRating(it.name.lowercase())) },
                label = { Text(stringResource(it.title)) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Genres(event: (FilterEvent) -> Unit, allGenres: List<GenresF>, genres: Set<String>) {
    ParagraphTitle(stringResource(text_genres))
    FlowRow(Modifier, spacedBy(8.dp), spacedBy(12.dp)) {
        allGenres.forEach { genre ->
            key(genre.id) {
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = genre.id in genres,
                    onClick = { event(SetGenre(genre.id)) },
                    label = { Text(genre.russian) }
                )
            }
        }
    }
}

// ========================================== Extensions ===========================================

private fun LazyListScope.contentList(list: LazyPagingItems<Content>, onNavigate: (String) -> Unit) =
    items(list.itemCount, list.itemKey(Content::id)) { index ->
        list[index]?.let {
            CatalogListItem(
                title = it.title,
                kind = it.kind,
                modifier = Modifier.animateItem(),
                season = it.season,
                image = it.poster,
                click = { onNavigate(it.id) }
            )
        }
    }

private fun LazyGridScope.contentList(list: LazyPagingItems<Content>, onNavigate: (String) -> Unit) =
    items(list.itemCount, list.itemKey(Content::id)) { index ->
        list[index]?.let {
            CatalogGridItem(
                title = it.title,
                image = it.poster,
                modifier = Modifier.animateItem(),
                click = { onNavigate(it.id) }
            )
        }
    }
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.FilterEvent
import org.application.shikiapp.events.FilterEvent.SetDuration
import org.application.shikiapp.events.FilterEvent.SetGenre
import org.application.shikiapp.events.FilterEvent.SetOrder
import org.application.shikiapp.events.FilterEvent.SetRating
import org.application.shikiapp.events.FilterEvent.SetSeason
import org.application.shikiapp.events.FilterEvent.SetStatus
import org.application.shikiapp.events.FilterEvent.SetTitle
import org.application.shikiapp.generated.fragment.Genres
import org.application.shikiapp.models.states.CatalogState
import org.application.shikiapp.models.states.DialogFilters
import org.application.shikiapp.models.states.ExpandedFilters
import org.application.shikiapp.models.states.FiltersState
import org.application.shikiapp.models.states.isFiltersVisible
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.models.viewModels.CatalogViewModel
import org.application.shikiapp.ui.templates.CatalogCardItem
import org.application.shikiapp.ui.templates.CatalogGridItem
import org.application.shikiapp.ui.templates.ErrorScreen
import org.application.shikiapp.ui.templates.LoadingScreen
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.UserGridItem
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.CatalogItem
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
import org.application.shikiapp.utils.extensions.pairwise
import org.application.shikiapp.utils.navigation.LocalBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun CatalogScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val model = viewModel<CatalogViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val filters by model.currentFilters.collectAsStateWithLifecycle()
    val genres by model.genres.collectAsStateWithLifecycle()

    val listStates = CatalogItem.entries.associateWith { rememberLazyListState() }
    val gridStates = CatalogItem.entries.associateWith { rememberLazyGridState() }

    fun toggleDrawer() {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    LaunchedEffect(state.dialogFilter) {
        barVisibility.toggle(state.isFiltersVisible)
    }

    LaunchedEffect(model.navEvent) {
        model.navEvent.collectLatest { args ->
            when {
                args.studio != null -> model.onEvent(FilterEvent.SetStudio(args.studio))
                args.publisher != null -> {
                    model.pick(if (args.linkedType == LinkedType.MANGA) MANGA else RANOBE)
                    model.onEvent(FilterEvent.SetPublisher(args.publisher))
                }

                args.showOngoing == true -> model.onEvent(SetStatus("ongoing"))
            }
        }
    }

    LaunchedEffect(filters) {
        snapshotFlow { filters }
            .pairwise()
            .collectLatest { (old, new) ->
                if (new != old) {
                    scope.launch {
                        val listState = listStates[state.menu]
                        val gridState = gridStates[state.menu]

                        snapshotFlow {
                            (listState?.layoutInfo?.totalItemsCount ?: 0) +
                                    (gridState?.layoutInfo?.totalItemsCount ?: 0)
                        }
                            .drop(1)
                            .first { it > 0 }

                        listState?.scrollToItem(0)
                        gridState?.scrollToItem(0)
                    }
                }
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet(drawerState, Modifier.width(260.dp)) {
                Text(
                    text = stringResource(R.string.text_catalog),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )

                CatalogItem.entries.forEach { item ->
                    NavigationDrawerItem(
                        selected = state.menu == item,
                        onClick = { model.pick(item); toggleDrawer() },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = { VectorIcon(item.icon) },
                        label = {
                            Text(
                                text = stringResource(item.title),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )

                    if (item == PEOPLE) {
                        HorizontalDivider(Modifier.padding(8.dp))
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SearchAppBar(
                    search = state.search,
                    onSearch = { model.onEvent(SetTitle(it)) },
                    navigationIcon = {
                        IconButton(::toggleDrawer) { VectorIcon(R.drawable.vector_menu) }
                    },
                    actions = {
                        if (state.menu.showFilter) {
                            IconButton(
                                onClick = { model.showFilters(state.menu) },
                                content = {
                                    BadgedBox(
                                        badge = { if (filters != FiltersState()) Badge() },
                                        content = { VectorIcon(R.drawable.vector_filter) }
                                    )
                                }
                            )
                        }
                    }
                )
            }
        ) { values ->
            key(state.menu) {
                val catalogList = model.list.collectAsLazyPagingItems()

                CatalogList(
                    menu = state.menu,
                    list = catalogList,
                    listState = listStates.getValue(state.menu),
                    gridState = gridStates.getValue(state.menu),
                    paddingValues = values,
                    onNavigate = onNavigate
                )
            }
        }
    }

    DialogFilters(
        state = state,
        filters = filters,
        visible = state.isFiltersVisible,
        type = state.menu.linkedType,
        genres = genres,
        onExpandedChange = model::toggleExpandedFilter,
        onFilterEvent = model::onEvent,
        onHide = model::showFilters
    )

    if (state.dialogFilter == DialogFilters.People) {
        DialogFiltersP(
            checked = { it in filters.roles },
            onValueChange = { model.onEvent(FilterEvent.SetRole(it)) },
            onHide = model::showFilters
        )
    }
}

@Composable
private fun SearchAppBar(
    search: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit = { onSearch(BLANK) },
    navigationIcon: @Composable (() -> Unit),
    actions: @Composable (RowScope.() -> Unit)
) {
    val searchState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(search)

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .pairwise()
            .collectLatest { (old, new) ->
                if (old != new) {
                    onSearch(new)
                }
            }
    }

    LaunchedEffect(search) {
        if (search.isEmpty()) {
            textFieldState.clearText()
        }
    }

    AppBarWithSearch(
        state = searchState,
        navigationIcon = navigationIcon,
        actions = actions,
        inputField = {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchState,
                onSearch = onSearch,
                leadingIcon = { VectorIcon(R.drawable.vector_search) },
                placeholder = { Text(stringResource(R.string.text_search)) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClear) { VectorIcon(R.drawable.vector_close) }
                    }
                }
            )
        }
    )
}

// ============================================= Lists =============================================

@Composable
private fun CatalogList(
    menu: CatalogItem,
    list: LazyPagingItems<BasicContent>,
    listState: LazyListState,
    gridState: LazyGridState,
    paddingValues: PaddingValues,
    onNavigate: (Screen) -> Unit
) = when (list.loadState.refresh) {
    LoadState.Loading -> LoadingScreen()
    is LoadState.Error -> ErrorScreen(list::retry)
    is LoadState.NotLoading -> {
        when (menu) {
            USERS, CLUBS -> LazyVerticalGrid(
                columns = GridCells.FixedSize(70.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(list.itemCount) { index ->
                    list[index]?.let {
                        UserGridItem(
                            title = it.title,
                            imageUrl = it.poster,
                            onClick = { onNavigate(menu.navigateTo(it.id)) }
                        )
                    }
                }

                if (list.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        content = { LoadingScreen(Modifier.padding(8.dp)) }
                    )
                }
                if (list.loadState.hasError) {
                    item { ErrorScreen(list::retry) }
                }
            }

            else -> if (Preferences.listView == ListView.COLUMN)
                LazyColumn(
                    contentPadding = paddingValues,
                    state = listState
                ) {
                    contentList(list) {
                        onNavigate(menu.navigateTo(it))
                    }
                    if (list.loadState.append == LoadState.Loading) item { LoadingScreen() }
                    if (list.loadState.hasError) item { ErrorScreen(list::retry) }
                }
            else LazyVerticalGrid(
                columns = GridCells.FixedSize(116.dp),
                contentPadding = PaddingValues(0.dp, paddingValues.calculateTopPadding().plus(8.dp)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = gridState
            ) {
                contentList(list) {
                    onNavigate(menu.navigateTo(it))
                }
                if (list.loadState.append == LoadState.Loading) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        content = { LoadingScreen() }
                    )
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
private fun DialogFilters(
    state: CatalogState,
    filters: FiltersState,
    visible: Boolean,
    genres: List<Genres>,
    type: LinkedType?,
    onExpandedChange: (ExpandedFilters) -> Unit,
    onFilterEvent: (FilterEvent) -> Unit,
    onHide: () -> Unit
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        modifier = Modifier.zIndex(10f),
        visible = visible,
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        )
    ) {
        BackHandler(visible, onHide)
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { NavigationIcon(onHide) },
                    title = { Text(stringResource(R.string.text_filters)) },
                    subtitle = { Text(stringResource(R.string.text_applied_immediately)) },
                    actions = {
                        IconButton(
                            onClick = { onFilterEvent(FilterEvent.ClearFilters) },
                            content = { VectorIcon(R.drawable.vector_refresh) }
                        )
                    },
                    modifier = Modifier.drawBehind {
                        drawLine(Color.LightGray, Offset(0f, size.height), Offset(size.width, size.height), 4f)
                    },
                )
            }
        ) { values ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = values.calculateTopPadding(),
                    end = 8.dp,
                    bottom = 16.dp
                )
            ) {
                item {
                    Sorting(
                        order = filters.order,
                        onClick = { onFilterEvent(SetOrder(it)) }
                    )
                }
                item {
                    Status(
                        type = type,
                        isExpanded = ExpandedFilters.Status in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Status) },
                        selected = { it in filters.status },
                        onClick = { onFilterEvent(SetStatus(it)) }
                    )
                }
                item {
                    Kind(
                        type = type,
                        isExpanded = ExpandedFilters.Kind in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Kind) },
                        selected = { it in filters.kind },
                        onClick = { onFilterEvent(FilterEvent.SetKind(it)) }
                    )
                }
                item {
                    Season(
                        seasonYS = filters.seasonYearStart,
                        seasonYF = filters.seasonYearFinal,
                        isExpanded = ExpandedFilters.Season in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Season) },
                        seasonSelected = { it in filters.seasonYearSeason },
                        onEvent = onFilterEvent
                    )
                }
                item {
                    Score(
                        score = filters.score,
                        isExpanded = ExpandedFilters.Score in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Score) },
                        onValueChange = { onFilterEvent(FilterEvent.SetScore(it)) }
                    )
                }

                if (type == LinkedType.ANIME) {
                    item {
                        Duration(
                            isExpanded = ExpandedFilters.Duration in state.expandedFilters,
                            onExpandedChange = { onExpandedChange(ExpandedFilters.Duration) },
                            selected = { it in filters.duration },
                            onClick = { onFilterEvent(SetDuration(it)) }
                        )
                    }
                    item {
                        Rating(
                            isExpanded = ExpandedFilters.Rating in state.expandedFilters,
                            onExpandedChange = { onExpandedChange(ExpandedFilters.Rating) },
                            selected = { it in filters.rating },
                            onClick = { onFilterEvent(SetRating(it)) }
                        )
                    }
                }

                item {
                    Genres(
                        genres = genres,
                        isExpanded = ExpandedFilters.Genres in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Genres) },
                        selected = { it in filters.genres },
                        onClick = { onFilterEvent(SetGenre(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogFiltersP(
    checked: (PeopleFilterItem) -> Boolean,
    onValueChange: (PeopleFilterItem) -> Unit,
    onHide: () -> Unit
) = AlertDialog(
    onDismissRequest = onHide,
    confirmButton = {},
    dismissButton = { TextButton(onHide) { Text(stringResource(R.string.text_close)) } },
    title = { Text(stringResource(R.string.text_filters)) },
    text = {
        Column {
            PeopleFilterItem.entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = checked(entry),
                            onValueChange = { onValueChange(entry) },
                            role = Role.Checkbox
                        )
                ) {
                    Checkbox(checked(entry), null)
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

@Composable
private fun Sorting(order: Order, onClick: (Order) -> Unit) {
    var flag by remember { mutableStateOf(false) }


    Column {
        ListItem(
            onClick = {},
            content = { Text(stringResource(R.string.text_sorting)) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
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
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = flag,
                    onDismissRequest = { flag = false }
                ) {
                    Order.entries.forEach { entry ->
                        DropdownMenuItem(
                            onClick = { onClick(entry); flag = false },
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
    }
}

@Composable
private fun Status(
    type: LinkedType?,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    selected: (String) -> Boolean,
    onClick: (String) -> Unit
) {
    val filteredStatuses = remember(type) {
        Status.entries.filter { type in it.types }
    }

    AnimatedColumn(R.string.text_status, isExpanded, onExpandedChange) {
        FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
            filteredStatuses.forEach { entry ->
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = selected(entry.name.lowercase()),
                    onClick = { onClick(entry.name.lowercase()) },
                    label = {
                        Text(
                            text = stringResource(
                                id = if (type == LinkedType.ANIME) entry.animeTitle ?: R.string.text_unknown
                                else entry.mangaTitle
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun Kind(
    type: LinkedType?,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    selected: (String) -> Boolean,
    onClick: (String) -> Unit
) {
    val linkedKinds = remember(type) {
        Kind.entries.filter { it.linkedType == type }
    }

    AnimatedColumn(R.string.text_kind, isExpanded, onExpandedChange) {
        FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
            linkedKinds.forEach {
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = selected(it.name.lowercase()),
                    onClick = { onClick(it.name.lowercase()) },
                    label = { Text(stringResource(it.title)) }
                )
            }
        }
    }
}

@Composable
private fun Season(
    seasonYS: String,
    seasonYF: String,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    seasonSelected: (String) -> Boolean,
    onEvent: (SetSeason) -> Unit
) {

    @Composable
    fun LocalTextField(
        text: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        @StringRes label: Int
    ) {
        val textFieldState = rememberTextFieldState(text)

        LaunchedEffect(textFieldState) {
            snapshotFlow { textFieldState.text.toString() }.collectLatest(onValueChange)
        }

        LaunchedEffect(text) {
            if (text.isEmpty()) {
                textFieldState.clearText()
            }
        }

        OutlinedTextField(
            state = textFieldState,
            modifier = modifier,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            lineLimits = TextFieldLineLimits.SingleLine,
            label = {
                Text(
                    text = stringResource(label),
                    maxLines = 1
                )
            },
            inputTransformation = InputTransformation.maxLength(4).then {
                if (!asCharSequence().isDigitsOnly()) {
                    revertAllChanges()
                }
            }
        )
    }

    AnimatedColumn(R.string.text_season, isExpanded, onExpandedChange) {
        Column(Modifier, Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                LocalTextField(
                    text = seasonYS,
                    onValueChange = { onEvent(SetSeason.SetStartYear(it)) },
                    modifier = Modifier.weight(1f),
                    label = R.string.text_start_year
                )
                LocalTextField(
                    text = seasonYF,
                    onValueChange = { onEvent(SetSeason.SetFinalYear(it)) },
                    modifier = Modifier.weight(1f),
                    label = R.string.text_end_year
                )
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Season.entries.forEach {
                    FilterChip(
                        modifier = Modifier.height(36.dp),
                        selected = seasonSelected(it.name.lowercase()),
                        onClick = { onEvent(SetSeason.ToggleSeasonYear(it.name.lowercase())) },
                        label = { Text(stringResource(it.title)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Score(
    score: Float,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    onValueChange: (Float) -> Unit
) {
    val interactionSource = remember(::MutableInteractionSource)

    AnimatedColumn(R.string.text_score, isExpanded, onExpandedChange) {
        Column {
            Slider(
                value = score,
                onValueChange = onValueChange,
                steps = 8,
                valueRange = 1f..10f,
                interactionSource = interactionSource,
                thumb = {
                    Label(
                        interactionSource = interactionSource,
                        label = {
                            PlainTooltip(Modifier.sizeIn(maxWidth = 30.dp)) {
                                Text(
                                    text = score.toInt().toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    ) {
                        VectorIcon(
                            resId = R.drawable.vector_star,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = Color(0xFFFFC319)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun Duration(
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    selected: (String) -> Boolean,
    onClick: (String) -> Unit
) = AnimatedColumn(R.string.text_episode_duration, isExpanded, onExpandedChange) {
    FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
        Duration.entries.forEach { entry ->
            FilterChip(
                modifier = Modifier.height(36.dp),
                selected = selected(entry.name.lowercase()),
                onClick = { onClick(entry.name.lowercase()) },
                label = { Text(stringResource(entry.title)) }
            )
        }
    }
}

@Composable
private fun Rating(
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    selected: (String) -> Boolean,
    onClick: (String) -> Unit
) = AnimatedColumn(R.string.text_rating, isExpanded, onExpandedChange) {
    FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
        Rating.entries.forEach {
            FilterChip(
                modifier = Modifier.height(36.dp),
                selected = selected(it.name.lowercase()),
                onClick = { onClick(it.name.lowercase()) },
                label = { Text(stringResource(it.title)) })
        }
    }
}

@Composable
private fun Genres(
    genres: List<Genres>,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    selected: (String) -> Boolean,
    onClick: (String) -> Unit
) = AnimatedColumn(R.string.text_genres, isExpanded, onExpandedChange) {
    FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
        genres.forEach { genre ->
            key(genre.id) {
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = selected(genre.id),
                    onClick = { onClick(genre.id) },
                    label = { Text(genre.russian) }
                )
            }
        }
    }
}

@Composable
private fun AnimatedColumn(
    @StringRes label: Int,
    isExpanded: Boolean,
    onExpandedChange: () -> Unit,
    content: @Composable () -> Unit
) = Column(verticalArrangement = Arrangement.Center) {
    HorizontalDivider()

    ListItem(
        onClick = { onExpandedChange() },
        content = { Text(stringResource(label)) },
        trailingContent = {
            IconButton(onExpandedChange) {
                VectorIcon(
                    resId = if (isExpanded) R.drawable.vector_keyboard_arrow_up
                    else R.drawable.vector_keyboard_arrow_down
                )
            }
        }
    )

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            (fadeIn() + expandVertically(expandFrom = Alignment.Top))
                .togetherWith(fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top))
        }
    ) { isExpanded ->
        if (isExpanded) {
            Box(
                content = { content() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// ========================================== Extensions ===========================================

private fun LazyListScope.contentList(list: LazyPagingItems<BasicContent>, onNavigate: (String) -> Unit) =
    items(list.itemCount, list.itemKey(BasicContent::id)) { index ->
        list[index]?.let { item ->
            when (item) {
                is Content -> {
                    CatalogCardItem(
                        title = item.title,
                        kind = item.kind,
                        modifier = Modifier.animateItem(),
                        season = item.season,
                        status = item.status,
                        image = item.poster,
                        onClick = { onNavigate(item.id) },
                        score = item.score
                    )
                }

                else -> {
                    CatalogCardItem(
                        title = item.title,
                        modifier = Modifier.animateItem(),
                        image = item.poster,
                        onClick = { onNavigate(item.id) },
                    )
                }
            }
        }
    }

private fun LazyGridScope.contentList(list: LazyPagingItems<BasicContent>, onNavigate: (String) -> Unit) =
    items(list.itemCount, list.itemKey(BasicContent::id)) { index ->
        list[index]?.let { item ->
            CatalogGridItem(
                title = item.title,
                image = item.poster,
                score = (item as? Content)?.score,
                kind = (item as? Content)?.kind,
                season = (item as? Content)?.season?.asString()?.split(" ")?.lastOrNull(),
                modifier = Modifier.animateItem(),
                onClick = { onNavigate(item.id) }
            )
        }
    }
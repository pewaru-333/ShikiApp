@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.generated.shikiapp.fragment.Genres
import org.application.shikiapp.shared.events.FilterEvent
import org.application.shikiapp.shared.events.FilterEvent.SetDuration
import org.application.shikiapp.shared.events.FilterEvent.SetGenre
import org.application.shikiapp.shared.events.FilterEvent.SetOrder
import org.application.shikiapp.shared.events.FilterEvent.SetRating
import org.application.shikiapp.shared.events.FilterEvent.SetSeason
import org.application.shikiapp.shared.events.FilterEvent.SetStatus
import org.application.shikiapp.shared.events.FilterEvent.SetTitle
import org.application.shikiapp.shared.models.states.CatalogState
import org.application.shikiapp.shared.models.states.DialogFilters
import org.application.shikiapp.shared.models.states.ExpandedFilters
import org.application.shikiapp.shared.models.states.FiltersState
import org.application.shikiapp.shared.models.states.isFiltersVisible
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.asSource
import org.application.shikiapp.shared.models.viewModels.CatalogViewModel
import org.application.shikiapp.shared.ui.templates.ContentList
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.ScaffoldSearchBar
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.enums.CatalogItem
import org.application.shikiapp.shared.utils.enums.Duration
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Order
import org.application.shikiapp.shared.utils.enums.PeopleFilterItem
import org.application.shikiapp.shared.utils.enums.Rating
import org.application.shikiapp.shared.utils.enums.Season
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.extensions.isDigitsOnly
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_applied_immediately
import shikiapp.composeapp.generated.resources.text_catalog
import shikiapp.composeapp.generated.resources.text_clear
import shikiapp.composeapp.generated.resources.text_close
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_end_year
import shikiapp.composeapp.generated.resources.text_episode_duration
import shikiapp.composeapp.generated.resources.text_filters
import shikiapp.composeapp.generated.resources.text_genres
import shikiapp.composeapp.generated.resources.text_kind
import shikiapp.composeapp.generated.resources.text_rating
import shikiapp.composeapp.generated.resources.text_score
import shikiapp.composeapp.generated.resources.text_season
import shikiapp.composeapp.generated.resources.text_sorting
import shikiapp.composeapp.generated.resources.text_start_year
import shikiapp.composeapp.generated.resources.text_status
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.vector_filter
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_down
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_up
import shikiapp.composeapp.generated.resources.vector_menu
import shikiapp.composeapp.generated.resources.vector_refresh
import shikiapp.composeapp.generated.resources.vector_star

@Composable
fun CatalogScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val model = viewModel(::CatalogViewModel)
    val state by model.state.collectAsStateWithLifecycle()
    val filters by model.currentFilters.collectAsStateWithLifecycle()
    val genres by model.genres.collectAsStateWithLifecycle()

    val listStates = CatalogItem.entries.associateWith { rememberLazyListState() }
    val gridStates = CatalogItem.entries.associateWith { rememberLazyGridState() }

    val isCompact = rememberWindowSize().isCompact

    fun toggleDrawer() {
        scope.launch {
            if (drawerState.isClosed) drawerState.open() else drawerState.close()
        }
    }

    LaunchedEffect(state.dialogFilter) {
        barVisibility.toggle(state.isFiltersVisible)
    }

    val menuRow: @Composable (() -> Unit)? = if (isCompact) null else {
        {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp, 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CatalogItem.entries, CatalogItem::name) { item ->
                    FilterChip(
                        selected = state.menu == item,
                        onClick = { model.pick(item) },
                        label = {
                            Text(
                                text = stringResource(item.title),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            VectorIcon(item.icon, Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }

    val scaffoldContent = @Composable {
        ScaffoldSearchBar(
            search = state.search,
            onSearch = { model.onEvent(SetTitle(it)) },
            menuRow = menuRow,
            navigationIcon = {
                if (isCompact) {
                    IconButton(::toggleDrawer) { VectorIcon(Res.drawable.vector_menu) }
                }
            },
            actions = {
                if (state.menu.showFilter) {
                    IconButton(
                        onClick = { model.showFilters(state.menu) },
                        content = {
                            BadgedBox(
                                badge = { if (filters != FiltersState()) Badge() },
                                content = { VectorIcon(Res.drawable.vector_filter) }
                            )
                        }
                    )
                }
            },
            content = {
                key(state.menu) {
                    val catalogList = model.list.collectAsLazyPagingItems()
                    val isRefreshing = catalogList.loadState.refresh is LoadState.Loading

                    ContentList(
                        mode = state.menu.viewType,
                        isCompactWindow = isCompact,
                        source = catalogList.asSource(BasicContent::id),
                        listState = listStates.getValue(state.menu),
                        gridState = gridStates.getValue(state.menu),
                        onItemClick = { id, _ -> onNavigate(state.menu.navigateTo(id)) }
                    )

                    LaunchedEffect(isRefreshing) {
                        if (isRefreshing) {
                            listStates[state.menu]?.requestScrollToItem(0)
                            gridStates[state.menu]?.requestScrollToItem(0)
                        }
                    }
                }
            }
        )
    }

    if (isCompact) {
        ModalNavigationDrawer(
            content = scaffoldContent,
            drawerState = drawerState,
            drawerContent = {
                DismissibleDrawerSheet(drawerState, Modifier.width(260.dp)) {
                    Text(
                        text = stringResource(Res.string.text_catalog),
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

                        if (item == CatalogItem.PEOPLE) {
                            HorizontalDivider(Modifier.padding(8.dp))
                        }
                    }
                }
            }
        )
    } else {
        scaffoldContent()
    }

    DialogFilters(
        state = state,
        filters = filters,
        isVisible = state.isFiltersVisible,
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

// ======================================= Dialogs Filters ========================================

@Composable
private fun DialogFilters(
    state: CatalogState,
    filters: FiltersState,
    isVisible: Boolean,
    genres: List<Genres>,
    type: LinkedType?,
    onExpandedChange: (ExpandedFilters) -> Unit,
    onFilterEvent: (FilterEvent) -> Unit,
    onHide: () -> Unit
) {
    val density = LocalDensity.current
    val isCompact = rememberWindowSize().isCompact

    val filtersListContent = @Composable { innerPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                end = 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
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

    if (isCompact) {
        AnimatedVisibility(
            modifier = Modifier.zIndex(10f),
            visible = isVisible,
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f)
        ) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = isVisible,
                onBackCompleted = onHide
            )
            Scaffold(
                content = { filtersListContent(it) },
                topBar = {
                    val lineColor = MaterialTheme.colorScheme.outlineVariant

                    TopAppBar(
                        navigationIcon = { NavigationIcon(onHide) },
                        title = { Text(stringResource(Res.string.text_filters)) },
                        subtitle = { Text(stringResource(Res.string.text_applied_immediately)) },
                        actions = {
                            IconButton(
                                onClick = { onFilterEvent(FilterEvent.ClearFilters) },
                                content = { VectorIcon(Res.drawable.vector_refresh) }
                            )
                        },
                        modifier = Modifier.drawBehind {
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 4f
                            )
                        }
                    )
                }
            )
        }
    } else {
        if (isVisible) {
            AlertDialog(
                modifier = Modifier.padding(vertical = 16.dp),
                onDismissRequest = onHide,
                containerColor = ListItemDefaults.containerColor,
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(Res.string.text_filters),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = stringResource(Res.string.text_applied_immediately),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                text = {
                    filtersListContent(PaddingValues(0.dp))
                },
                confirmButton = {
                    TextButton(onHide) {
                        Text(stringResource(Res.string.text_confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onFilterEvent(FilterEvent.ClearFilters) },
                        content = { Text(stringResource(Res.string.text_clear)) }
                    )
                }
            )
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
    dismissButton = { TextButton(onHide) { Text(stringResource(Res.string.text_close)) } },
    title = { Text(stringResource(Res.string.text_filters)) },
    text = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
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
            content = { Text(stringResource(Res.string.text_sorting)) }
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

    AnimatedColumn(Res.string.text_status, isExpanded, onExpandedChange) {
        FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
            filteredStatuses.forEach { entry ->
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = selected(entry.name.lowercase()),
                    onClick = { onClick(entry.name.lowercase()) },
                    label = {
                        Text(
                            text = stringResource(
                                resource = if (type == LinkedType.ANIME) entry.animeTitle ?: Res.string.text_unknown
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

    AnimatedColumn(Res.string.text_kind, isExpanded, onExpandedChange) {
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
        label: StringResource
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

    AnimatedColumn(Res.string.text_season, isExpanded, onExpandedChange) {
        Column(Modifier, Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                LocalTextField(
                    text = seasonYS,
                    onValueChange = { onEvent(SetSeason.SetStartYear(it)) },
                    modifier = Modifier.weight(1f),
                    label = Res.string.text_start_year
                )
                LocalTextField(
                    text = seasonYF,
                    onValueChange = { onEvent(SetSeason.SetFinalYear(it)) },
                    modifier = Modifier.weight(1f),
                    label = Res.string.text_end_year
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

    AnimatedColumn(Res.string.text_score, isExpanded, onExpandedChange) {
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
                            resId = Res.drawable.vector_star,
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
) = AnimatedColumn(Res.string.text_episode_duration, isExpanded, onExpandedChange) {
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
) = AnimatedColumn(Res.string.text_rating, isExpanded, onExpandedChange) {
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
) = AnimatedColumn(Res.string.text_genres, isExpanded, onExpandedChange) {
    FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(12.dp)) {
        genres.fastForEach { genre ->
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
    label: StringResource,
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
                    resId = if (isExpanded) Res.drawable.vector_keyboard_arrow_up
                    else Res.drawable.vector_keyboard_arrow_down
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
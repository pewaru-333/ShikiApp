@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFlexBoxApi::class, ExperimentalFoundationStyleApi::class
)

package org.application.shikiapp.shared.screens

import androidx.compose.animation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.LocalMaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.application.shikiapp.generated.shikiapp.fragment.Genres
import org.application.shikiapp.shared.events.FilterEvent
import org.application.shikiapp.shared.events.FilterEvent.*
import org.application.shikiapp.shared.models.states.*
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.asSource
import org.application.shikiapp.shared.models.viewModels.CatalogViewModel
import org.application.shikiapp.shared.ui.templates.ContentList
import org.application.shikiapp.shared.ui.templates.NavigationIcon
import org.application.shikiapp.shared.ui.templates.ScaffoldSearchBar
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.isDigitsOnly
import org.application.shikiapp.shared.utils.extensions.pairwise
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

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
                        label = { Text(stringResource(item.title)) },
                        leadingIcon = { VectorIcon(item.icon, Modifier.size(18.dp)) }
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
                    IconButton(::toggleDrawer) { VectorIcon(Icons.Menu) }
                }
            },
            actions = {
                if (state.menu.showFilter) {
                    IconButton(
                        onClick = { model.showFilters(state.menu) },
                        content = {
                            BadgedBox(
                                badge = { if (filters != FiltersState()) Badge() },
                                content = { VectorIcon(Icons.Filter) }
                            )
                        }
                    )
                }
            },
            content = {
                key(state.menu) {
                    val catalogList = model.list.collectAsLazyPagingItems()

                    ContentList(
                        mode = state.menu.viewType,
                        isCompactWindow = isCompact,
                        source = catalogList.asSource(BasicContent::id),
                        listState = listStates.getValue(state.menu),
                        gridState = gridStates.getValue(state.menu),
                        onItemClick = { id, _ -> onNavigate(state.menu.navigateTo(id)) }
                    )

                    LaunchedEffect(Unit) {
                        snapshotFlow { filters }
                            .pairwise()
                            .collectLatest { (old, new) ->
                                if (old != new) {
                                    listStates[state.menu]?.requestScrollToItem(0)
                                    gridStates[state.menu]?.requestScrollToItem(0)
                                }
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
                val filteredStatuses = remember(type) {
                    Status.entries.filter { type in it.types }
                }

                AnimatedColumn(
                    label = Res.string.text_status,
                    isExpanded = ExpandedFilters.Status in state.expandedFilters,
                    onExpandedChange = { onExpandedChange(ExpandedFilters.Status) },
                    content = {
                        FlexBoxRow(
                            entries = filteredStatuses,
                            selected = { it.name.lowercase() in filters.status },
                            onClick = { onFilterEvent(SetStatus(it.name.lowercase())) },
                            getLabel = { ResourceText.StringResource(it.getTitle(type)) }
                        )
                    }
                )
            }
            item {
                val linkedKinds = remember(type) {
                    Kind.entries.filter { it.linkedType == type }
                }

                AnimatedColumn(
                    label = Res.string.text_kind,
                    isExpanded = ExpandedFilters.Kind in state.expandedFilters,
                    onExpandedChange = { onExpandedChange(ExpandedFilters.Kind) },
                    content = {
                        FlexBoxRow(
                            entries = linkedKinds,
                            getLabel = { ResourceText.StringResource(it.title) },
                            selected = { it.name.lowercase() in filters.kind },
                            onClick = { onFilterEvent(FilterEvent.SetKind(it.name.lowercase())) }
                        )
                    }
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
                    AnimatedColumn(
                        label = Res.string.text_episode_duration,
                        isExpanded = ExpandedFilters.Duration in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Duration) },
                        content = {
                            FlexBoxRow(
                                entries = Duration.entries,
                                getLabel = { ResourceText.StringResource(it.title) },
                                selected = { it.name.lowercase() in filters.duration },
                                onClick = { onFilterEvent(SetDuration(it.name.lowercase())) }
                            )
                        }
                    )
                }
                item {
                    AnimatedColumn(
                        label = Res.string.text_rating,
                        isExpanded = ExpandedFilters.Rating in state.expandedFilters,
                        onExpandedChange = { onExpandedChange(ExpandedFilters.Rating) },
                        content = {
                            FlexBoxRow(
                                entries = Rating.entries,
                                getLabel = { ResourceText.StringResource(it.title) },
                                selected = { it.name.lowercase() in filters.rating },
                                onClick = { onFilterEvent(SetRating(it.name.lowercase())) }
                            )
                        }
                    )
                }
            }

            item {
                AnimatedColumn(
                    label = Res.string.text_genres,
                    isExpanded = ExpandedFilters.Genres in state.expandedFilters,
                    onExpandedChange = { onExpandedChange(ExpandedFilters.Genres) },
                    content = {
                        FlexBoxRow(
                            entries = genres,
                            getLabel = { ResourceText.StaticString(it.russian) },
                            selected = { it.id in filters.genres },
                            onClick = { onFilterEvent(SetGenre(it.id)) }
                        )
                    }
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
                                content = { VectorIcon(Icons.Refresh) }
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
    var expanded by remember { mutableStateOf(false) }

    Column {
        ListItem(
            onClick = {},
            content = { Text(stringResource(Res.string.text_sorting)) }
        )

        Box(
            modifier = Modifier.styleable {
                fillWidth()
                contentPaddingHorizontal(16.dp)
                contentPaddingBottom(16.dp)
            }
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = stringResource(order.title),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = MenuDefaults.groupStandardContainerColor,
                    shape = MenuDefaults.standaloneGroupShape,
                ) {
                    Order.entries.fastForEachIndexed { index, entry ->
                        DropdownMenuItem(
                            selected = order == entry,
                            shapes = MenuDefaults.itemShape(index, Order.entries.size),
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            text = { Text(stringResource(entry.title)) },
                            onClick = {
                                onClick(entry)
                                expanded = false
                            }
                        )
                    }
                }
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
        label: StringResource,
        imeAction: ImeAction
    ) {
        val textFieldState = rememberTextFieldState(text)
        val focusRequester = remember(::FocusRequester)

        val interactionSource = remember(::MutableInteractionSource)
        val styleState = remember { MutableStyleState(interactionSource) }

        val textFieldStyle = Style {
            background(LocalMaterialTheme.currentValue.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            shape(RoundedCornerShape(12.dp))
            border(1.dp, Color.Transparent)
            contentPadding(16.dp, 12.dp)

            focused {
                border(1.dp, LocalMaterialTheme.currentValue.colorScheme.primary)
            }
        }

        LaunchedEffect(textFieldState) {
            snapshotFlow { textFieldState.text.toString() }.collectLatest(onValueChange)
        }

        LaunchedEffect(text) {
            if (text.isEmpty() && textFieldState.text.isNotEmpty()) {
                textFieldState.clearText()
            }
        }

        BasicTextField(
            state = textFieldState,
            interactionSource = interactionSource,
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            modifier = modifier
                .focusRequester(focusRequester)
                .styleable(styleState, textFieldStyle),
            inputTransformation = InputTransformation.maxLength(4).then {
                if (!asCharSequence().isDigitsOnly()) {
                    revertAllChanges()
                }
            },
            decorator = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            maxLines = 1,
                            text = stringResource(label),
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    innerTextField()
                }
            }
        )
    }

    AnimatedColumn(Res.string.text_season, isExpanded, onExpandedChange) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                LocalTextField(
                    text = seasonYS,
                    onValueChange = { onEvent(SetSeason.SetStartYear(it)) },
                    modifier = Modifier.weight(1f),
                    imeAction = ImeAction.Next,
                    label = Res.string.text_start_year
                )
                LocalTextField(
                    text = seasonYF,
                    onValueChange = { onEvent(SetSeason.SetFinalYear(it)) },
                    modifier = Modifier.weight(1f),
                    imeAction = ImeAction.Done,
                    label = Res.string.text_end_year
                )
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Season.entries.fastForEach {
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
        Slider(
            value = score,
            onValueChange = onValueChange,
            steps = 8,
            valueRange = 1f..10f,
            interactionSource = interactionSource,
            thumb = { sliderState ->
                Label(
                    interactionSource = interactionSource,
                    isPersistent = sliderState.isDragging,
                    label = {
                        PlainTooltip(
                            modifier = Modifier
                                .sizeIn(45.dp, 25.dp, 45.dp, 25.dp)
                                .wrapContentWidth(),
                            content = {
                                Text(
                                    text = score.toInt().toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    },
                    content = {
                        VectorIcon(
                            imageVector = Icons.Star,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = Color(0xFFFFC319)
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun <T> FlexBoxRow(
    entries: List<T>,
    getLabel: (T) -> ResourceText,
    selected: (T) -> Boolean,
    onClick: (T) -> Unit
) {
    FlexBox(
        config = {
            direction(FlexDirection.Row)
            wrap(FlexWrap.Wrap)
            gap(8.dp, 12.dp)
        },
        content = {
            entries.fastForEach {
                FilterChip(
                    modifier = Modifier.height(36.dp),
                    selected = selected(it),
                    onClick = { onClick(it) },
                    label = { Text(getLabel(it).asComposableString()) }
                )
            }
        }
    )
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
                    imageVector = if (isExpanded) Icons.KeyboardArrowUp
                    else Icons.KeyboardArrowDown
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
                modifier = Modifier.styleable {
                    fillWidth()
                    contentPaddingHorizontal(16.dp)
                    contentPaddingBottom(16.dp)
                }
            )
        }
    }
}
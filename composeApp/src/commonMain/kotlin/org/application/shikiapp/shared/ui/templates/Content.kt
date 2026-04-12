@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.ui.Franchise
import org.application.shikiapp.shared.models.ui.History
import org.application.shikiapp.shared.models.ui.Label
import org.application.shikiapp.shared.models.ui.Publisher
import org.application.shikiapp.shared.models.ui.Related
import org.application.shikiapp.shared.models.ui.Score
import org.application.shikiapp.shared.models.ui.Statistics
import org.application.shikiapp.shared.models.ui.Studio
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.models.ui.list.ContentSource
import org.application.shikiapp.shared.models.ui.list.ContentViewType
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.screens.LabelInfoItem
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.ListView
import org.application.shikiapp.shared.utils.enums.RelationKind
import org.application.shikiapp.shared.utils.extensions.add
import org.application.shikiapp.shared.utils.extensions.appendLoadState
import org.application.shikiapp.shared.utils.extensions.substringAfter
import org.application.shikiapp.shared.utils.extensions.substringBefore
import org.application.shikiapp.shared.utils.extensions.toContent
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_anime_list
import shikiapp.composeapp.generated.resources.text_chronology
import shikiapp.composeapp.generated.resources.text_date_from
import shikiapp.composeapp.generated.resources.text_date_till
import shikiapp.composeapp.generated.resources.text_description
import shikiapp.composeapp.generated.resources.text_directly
import shikiapp.composeapp.generated.resources.text_empty
import shikiapp.composeapp.generated.resources.text_episode
import shikiapp.composeapp.generated.resources.text_episode_next
import shikiapp.composeapp.generated.resources.text_episodes
import shikiapp.composeapp.generated.resources.text_franchise
import shikiapp.composeapp.generated.resources.text_in_lists
import shikiapp.composeapp.generated.resources.text_kind
import shikiapp.composeapp.generated.resources.text_manga_list
import shikiapp.composeapp.generated.resources.text_publisher
import shikiapp.composeapp.generated.resources.text_rate_chapters
import shikiapp.composeapp.generated.resources.text_rating
import shikiapp.composeapp.generated.resources.text_related
import shikiapp.composeapp.generated.resources.text_score
import shikiapp.composeapp.generated.resources.text_show_all_s
import shikiapp.composeapp.generated.resources.text_show_all_u
import shikiapp.composeapp.generated.resources.text_similar
import shikiapp.composeapp.generated.resources.text_source
import shikiapp.composeapp.generated.resources.text_spoiler
import shikiapp.composeapp.generated.resources.text_statistics
import shikiapp.composeapp.generated.resources.text_status
import shikiapp.composeapp.generated.resources.text_studio
import shikiapp.composeapp.generated.resources.text_subtitles
import shikiapp.composeapp.generated.resources.text_user_rates
import shikiapp.composeapp.generated.resources.text_voices_one
import shikiapp.composeapp.generated.resources.text_volumes
import shikiapp.composeapp.generated.resources.vector_anime
import shikiapp.composeapp.generated.resources.vector_arrow_forward
import shikiapp.composeapp.generated.resources.vector_calendar
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_down
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_up
import shikiapp.composeapp.generated.resources.vector_more
import shikiapp.composeapp.generated.resources.vector_similar
import shikiapp.composeapp.generated.resources.vector_star
import shikiapp.composeapp.generated.resources.vector_statistics
import shikiapp.composeapp.generated.resources.vector_subtitles
import shikiapp.composeapp.generated.resources.vector_timer
import shikiapp.composeapp.generated.resources.vector_voice_actors

@Composable
fun ScaffoldContent(
    title: @Composable (() -> Unit),
    watchButton: @Composable (() -> Unit)? = null,
    userRate: AsyncData<UserRate?>?,
    isFavoured: AsyncData<Boolean>,
    onBack: () -> Unit,
    onToggleFavourite: () -> Unit,
    onEvent: (ContentDetailEvent) -> Unit,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val isVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = { NavigationIcon(onBack) },
                actions = {
                    IconComment { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Comments)) }

                    watchButton?.invoke()

                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Sheet)) },
                        content = { VectorIcon(Res.drawable.vector_more) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButtonContent(
                userRate = userRate,
                isFavoured = isFavoured,
                isVisible = isVisible,
                onToggleFavourite = onToggleFavourite,
                onEvent = onEvent
            )
        }
    ) { values ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = values.toContent(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun ContentList(
    source: ContentSource<BasicContent>,
    mode: ContentViewType,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
    staggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    isCompactWindow: Boolean = rememberWindowSize().isCompact,
    onItemClick: (id: String, kind: Kind?) -> Unit,
    onImageClick: (String?) -> Unit = {}
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val userMinSize = when {
            maxWidth >= 840.dp -> 120.dp
            maxWidth >= 600.dp -> 90.dp
            else -> 70.dp
        }

        val catalogMinSize = when {
            maxWidth >= 840.dp -> 180.dp
            maxWidth >= 600.dp -> 140.dp
            else -> 116.dp
        }

        if (source.isLoadingRefresh) {
            LoadingScreen()
            return@BoxWithConstraints
        }
        if (source.isError) {
            ErrorScreen(onRetry = source.onRetry)
            return@BoxWithConstraints
        }
        if (source.isEmpty) {
            Box(Modifier.fillMaxSize().padding(contentPadding), Alignment.Center) {
                Text(stringResource(Res.string.text_empty))
            }
            return@BoxWithConstraints
        }

        when (mode) {
            ContentViewType.GRID_ITEM_SMALL -> {
                val titleConfig = MediaGridItemDefaults.titleConfig(
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(userMinSize),
                    contentPadding = contentPadding.add(PaddingValues(horizontal = 8.dp)),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(source.itemCount, source.itemKey) { index ->
                        source.itemProvider(index)?.let { item ->
                            CircleContentItem(
                                title = item.title,
                                poster = item.poster,
                                onClick = { onItemClick(item.id, null) },
                                titleConfig = titleConfig
                            )
                        }
                    }

                    appendLoadState(source) { GridItemSpan(maxLineSpan) }
                }
            }

            ContentViewType.LIST_ITEM -> {
                LazyColumn(state = listState, contentPadding = contentPadding) {
                    items(source.itemCount, source.itemKey) { index ->
                        source.itemProvider(index)?.let { item ->
                            ListContentItem(item.title, item.poster) {
                                onItemClick(item.id, null)
                            }
                        }
                    }

                    appendLoadState(source)
                }
            }

            ContentViewType.STAGGERED_GRID_ITEM_IMAGES -> {
                LazyVerticalStaggeredGrid(
                    state = staggeredGridState,
                    columns = StaggeredGridCells.Adaptive(120.dp),
                    contentPadding = contentPadding.add(PaddingValues(8.dp)),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(source.itemCount, source.itemKey) { index ->
                        source.itemProvider(index)?.let { item ->
                            AnimatedAsyncImage(
                                model = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { onImageClick(item.poster) }
                            )
                        }
                    }

                    appendLoadState(source)
                }
            }

            ContentViewType.ADAPTIVE_ITEM -> {
                if (isCompactWindow && Preferences.listView == ListView.COLUMN) {
                    LazyColumn(state = listState, contentPadding = contentPadding) {
                        items(source.itemCount, source.itemKey) { index ->
                            source.itemProvider(index)?.let { item ->
                                val content = item as? Content
                                val history = item as? History

                                val navId = history?.contentId ?: item.id
                                val navKind = history?.kind ?: content?.kind

                                MediaListItem(
                                    title = item.title,
                                    poster = item.poster,
                                    description = history?.description,
                                    score = content?.score,
                                    status = content?.status,
                                    kind = content?.kind,
                                    date = history?.date,
                                    season = content?.season?.asComposableString()?.takeIf(String::isNotEmpty),
                                    onClick = {
                                        if (navId.isNotEmpty()) {
                                            onItemClick(navId, navKind)
                                        }
                                    }
                                )
                            }
                        }

                        appendLoadState(source)
                    }
                } else {
                    val titleConfig = MediaGridItemDefaults.titleConfig(
                        minLines = 2,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )
                    )
                    val textStyle = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.7f
                        )
                    )

                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(catalogMinSize),
                        contentPadding = contentPadding.add(PaddingValues(8.dp)),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(source.itemCount, source.itemKey) { index ->
                            source.itemProvider(index)?.let { item ->
                                val content = item as? Content
                                val history = item as? History

                                val navId = history?.contentId ?: item.id
                                val navKind = history?.kind ?: content?.kind

                                val season = content?.season?.asComposableString()?.substringAfterLast(' ')
                                val kindName = content?.kind?.let { stringResource(it.title) }
                                val kindSeason = if (kindName != null && season != null) {
                                    "$kindName • $season"
                                } else {
                                    kindName ?: season.orEmpty()
                                }

                                MediaGridItem(
                                    title = item.title,
                                    poster = item.poster,
                                    score = content?.score,
                                    titleConfig = titleConfig,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    onClick = {
                                        if (navId.isNotEmpty()) {
                                            onItemClick(navId, navKind)
                                        }
                                    },
                                    subtitleContent = {
                                        if (kindSeason.isNotEmpty()) {
                                            Text(
                                                text = kindSeason,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = textStyle
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        appendLoadState(source) { GridItemSpan(maxLineSpan) }
                    }
                }
            }
        }
    }
}

@Composable
fun Description(description: AnnotatedString, withDivider: Boolean = true) {
    val hasSpoiler = remember(description) {
        description.text.contains("спойлер", ignoreCase = true)
    }
    val mainText = remember(description, hasSpoiler) {
        if (hasSpoiler) description.substringBefore("спойлер") else description
    }
    val spoilerText = remember(description, hasSpoiler) {
        if (hasSpoiler) description.substringAfter("спойлер") else AnnotatedString(BLANK)
    }

    var hasOverflow by remember { mutableStateOf(false) }
    var isVisible by rememberSaveable { mutableStateOf(false) }
    var maxLines by rememberSaveable { mutableIntStateOf(8) }

    val isTextExpanded = !hasOverflow || maxLines > 8

    Column(Modifier.animateContentSize()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_description), Modifier.padding(bottom = 4.dp))

            if (hasOverflow || maxLines > 8) {
                IconButton(
                    onClick = {
                        maxLines = if (maxLines == 8) Int.MAX_VALUE else 8
                    }
                ) {
                    VectorIcon(
                        resId = if (maxLines == 8) Res.drawable.vector_keyboard_arrow_down
                        else Res.drawable.vector_keyboard_arrow_up
                    )
                }
            }
        }

        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { hasOverflow = it.hasVisualOverflow }
        )

        if (hasSpoiler && isTextExpanded) {
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { isVisible = !isVisible }
                    .padding(12.dp, 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VectorIcon(
                        modifier = Modifier.size(20.dp),
                        resId = if (isVisible) Res.drawable.vector_keyboard_arrow_down
                        else Res.drawable.vector_keyboard_arrow_up
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = stringResource(Res.string.text_spoiler),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                AnimatedVisibility(isVisible) {
                    Column(Modifier.padding(top = 8.dp)) {
                        Text(
                            text = spoilerText.apply(AnnotatedString::trimStart),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (withDivider) {
            HorizontalDivider(Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun Names(russian: String?, english: String?, japanese: String?) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        when {
            !russian.isNullOrEmpty() -> {
                Text(
                    text = russian,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                english?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                japanese?.let {
                    Text(
                        text = it,
                        style = (if (english != null) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.titleMedium).copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            !english.isNullOrEmpty() -> {
                Text(
                    text = english,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                japanese?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            !japanese.isNullOrEmpty() -> {
                Text(
                    text = japanese,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

@Composable
fun SimilarFull(
    list: List<Content>,
    listState: LazyListState,
    isVisible: Boolean,
    onNavigate: (String) -> Unit,
    onHide: () -> Unit
) = AnimatedDialogScreen(isVisible, stringResource(Res.string.text_similar), onHide) { values ->
    LazyColumn(Modifier, listState, values) {
        items(list, Content::id) { item ->
            MediaListItem(
                title = item.title,
                poster = item.poster,
                score = item.score,
                status = item.status,
                kind = item.kind,
                season = item.season.asComposableString().takeIf(String::isNotEmpty),
                onClick = { onNavigate(item.id) },
            )
        }
    }
}

@Composable
fun Profiles(
    list: List<BasicContent>,
    title: String,
    onShowFull: () -> Unit,
    onNavigate: (String) -> Unit
) = Column {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(title)
        IconButton(onShowFull) { VectorIcon(Res.drawable.vector_arrow_forward) }
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list, BasicContent::id) {
            CircleContentItem(
                title = it.title,
                poster = it.poster,
                onClick = { onNavigate(it.id) },
                titleConfig = MediaGridItemDefaults.titleConfig(
                    minLines = 2,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
            )
        }
    }
}

@Composable
fun <T : BasicContent> ProfilesFull(
    list: List<T>,
    isVisible: Boolean,
    title: String,
    state: LazyListState,
    onHide: () -> Unit,
    onNavigate: (String) -> Unit
) = AnimatedDialogScreen(isVisible, title, onHide) { values ->
    LazyColumn(Modifier, state, values) {
        items(list, BasicContent::id) {
            ListContentItem(
                name = it.title,
                image = it.poster,
                roles = (it as? Content)?.season?.asComposableString(),
                onClick = { onNavigate(it.id) }
            )
        }
    }
}

@Composable
fun Related(list: List<Related>, onShowAll: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(Res.string.text_related))
            TextButton(onShowAll) { Text(stringResource(Res.string.text_show_all_u)) }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list.take(6), Related::id) { related ->
                RelatedCard(
                    title = related.title,
                    poster = related.poster,
                    relationText = related.relationText.ifEmpty { stringResource(related.kind.title) },
                    onClick = { onNavigate(related.linkedType.navigateTo(related.id)) }
                )
            }
        }
    }

@Composable
fun RelatedFull(
    related: List<Related>,
    chronology: List<Content>,
    franchise: List<Pair<RelationKind, List<Franchise>>>,
    isVisible: Boolean,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedDialogScreen(isVisible, stringResource(Res.string.text_related), onHide) { values ->
    val tabs = arrayOf(
        stringResource(Res.string.text_directly),
        stringResource(Res.string.text_chronology),
        stringResource(Res.string.text_franchise)
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = tabs::size)

    Column(Modifier.padding(values)) {
        PrimaryTabRow(pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.targetPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> LazyColumn {
                    items(related, Related::id) { item ->
                        MediaListItem(
                            title = item.title,
                            poster = item.poster,
                            score = item.score,
                            status = item.status,
                            kind = item.kind,
                            role = item.relationText,
                            season = item.season.asComposableString().takeIf(String::isNotEmpty),
                            onClick = { onNavigate(item.linkedType.navigateTo(item.id)) },
                        )
                    }
                }

                1 -> LazyColumn {
                    items(chronology, Content::id) { item ->
                        MediaListItem(
                            title = item.title,
                            poster = item.poster,
                            score = item.score,
                            status = item.status,
                            kind = item.kind,
                            season = item.season.asComposableString().takeIf(String::isNotEmpty),
                            onClick = { onNavigate(item.kind.linkedType.navigateTo(item.id)) }
                        )
                    }
                }

                2 -> LazyColumn {
                    franchise.fastForEach { (relation, items) ->
                        stickyHeader { TextStickyHeader(stringResource(relation.title)) }
                        items(items, Franchise::id) { item ->
                            MediaListItem(
                                title = item.title,
                                poster = item.poster,
                                kind = item.kind,
                                season = item.year.asComposableString(),
                                onClick = { onNavigate(item.linkedType.navigateTo(item.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RelatedFull(
    related: Map<LinkedType, List<Related>>,
    isVisible: Boolean,
    onHide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedDialogScreen(isVisible, stringResource(Res.string.text_related), onHide) { values ->
    Column(Modifier.padding(values)) { // Без этого stickyHeader не двигается при прокрутке
        LazyColumn {
            related.forEach { (type, items) ->
                stickyHeader { TextStickyHeader(stringResource(type.title)) }
                items(items, Related::id) { item ->
                    MediaListItem(
                        title = item.title,
                        poster = item.poster,
                        kind = item.kind,
                        role = item.relationText.takeIf(String::isNotEmpty),
                        season = item.season.asComposableString(),
                        onClick = { onNavigate(item.linkedType.navigateTo(item.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreInfo(score: String) = Column {
    Text(
        text = stringResource(Res.string.text_score),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VectorIcon(Res.drawable.vector_star, Modifier.size(16.dp), Color(0xFFFFC319))
        Text(
            text = score,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun StatusInfo(status: StringResource, airedOn: String, releasedOn: String) = Column {
    Text(
        text = stringResource(Res.string.text_status),
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Light
        )
    )
    Text(
        text = stringResource(status),
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
    Text(
        maxLines = 1,
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        style = MaterialTheme.typography.labelMedium,
        text = buildString {
            if (airedOn.isNotEmpty()) {
                append(stringResource(Res.string.text_date_from, airedOn))
            }

            if (releasedOn.isNotEmpty()) {
                if (isNotEmpty()) append(' ')

                append(stringResource(Res.string.text_date_till, releasedOn))
            }
        }
    )
}

@Composable
fun Statuses(
    scores: Map<Label, Score>,
    sum: Int,
    label: StringResource,
    content: (@Composable () -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            content?.invoke()
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            scores.entries.forEach { (key, value) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(
                            text = key.asComposableString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    LinearProgressIndicator(
                        drawStopIndicator = { },
                        progress = { if (sum > 0) value.toFloat() / sum else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun Statistics(id: Long, statistics: Pair<Statistics, Statistics>, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        statistics.first.let {
            if (it.sum > 0) {
                Statuses(
                    scores = it.scores,
                    sum = it.sum,
                    label = Res.string.text_anime_list,
                    content = {
                        TextButton(
                            onClick = { onNavigate(Screen.UserRates(id.toString(), LinkedType.ANIME)) },
                            content = { Text(stringResource(Res.string.text_show_all_s)) }
                        )
                    }
                )
            }
        }

        statistics.second.let {
            if (it.sum > 0) {
                HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))

                Statuses(
                    scores = it.scores,
                    sum = it.sum,
                    label = Res.string.text_manga_list,
                    content = {
                        TextButton(
                            onClick = { onNavigate(Screen.UserRates(id.toString(), LinkedType.MANGA)) },
                            content = { Text(stringResource(Res.string.text_show_all_s)) }
                        )
                    }
                )
            }
        }
    }

@Composable
fun Statistics(
    statistics: Pair<Statistics?, Statistics?>,
    isVisible: Boolean,
    onHide: () -> Unit
) = AnimatedDialogScreen(isVisible, stringResource(Res.string.text_statistics), onHide) { values ->
    LazyColumn(
        contentPadding = values.toContent(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        statistics.first?.let {
            item { Statuses(it.scores, it.sum, Res.string.text_user_rates) }
        }
        statistics.second?.let {
            item { Statuses(it.scores, it.sum, Res.string.text_in_lists) }
        }
    }
}

@Composable
private fun DetailBox(icon: DrawableResource, label: String, value: String? = null, onClick: (() -> Unit)? = null) =
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
    ) {
        if (value != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(56.dp)
                    .padding(12.dp, 8.dp),
            ) {
                VectorIcon(
                    resId = icon,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                if (onClick != null) {
                    VectorIcon(
                        resId = Res.drawable.vector_keyboard_arrow_right,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(56.dp)
                    .padding(10.dp, 6.dp),
            ) {
                VectorIcon(
                    resId = icon,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

@Composable
fun <T : BaseDialogState> MenuItems(
    items: List<T>,
    getTitle: (T) -> StringResource,
    onClick: (BaseDialogState) -> Unit
) = Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
    items.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
            row.fastForEach { entry ->
                AssistChip(
                    border = null,
                    onClick = { onClick(entry) },
                    trailingIcon = { VectorIcon(Res.drawable.vector_keyboard_arrow_right) },
                    label = {
                        Text(
                            text = stringResource(getTitle(entry)),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        trailingIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun CircleBorderedImage(image: String?, onOpenFullscreenPoster: () -> Unit) =
    CircleContentImage(
        image = image,
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .clickable(onClick = onOpenFullscreenPoster)
    )

@Composable
fun AnimatedDialogScreen(
    isVisible: Boolean,
    title: String,
    onHide: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInHorizontally { it } + fadeIn(),
    exit = slideOutHorizontally { it } + fadeOut()
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isVisible,
        onBackCompleted = onHide
    )
    Scaffold(
        content = content,
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { NavigationIcon(onHide) },
                actions = actions
            )
        }
    )
}

fun LazyListScope.title(title: String) = item {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    )
}

fun LazyListScope.info(
    poster: String,
    kind: StringResource,
    score: String,
    status: StringResource,
    airedOn: String,
    releasedOn: String,
    episodes: String? = null,
    volumes: String? = null,
    chapters: String? = null,
    isOngoingManga: Boolean? = null,
    publisher: Publisher? = null,
    origin: StringResource? = null,
    rating: StringResource? = null,
    onOpenFullscreenPoster: () -> Unit
) = item {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Poster(link = poster, onOpenFullscreen = onOpenFullscreenPoster)
        Column(Modifier.height(300.dp), Arrangement.SpaceBetween) {
            LabelInfoItem(stringResource(Res.string.text_kind), stringResource(kind))

            episodes?.let { LabelInfoItem(stringResource(Res.string.text_episodes), it) }

            StatusInfo(status, airedOn, releasedOn)

            publisher?.let { LabelInfoItem(stringResource(Res.string.text_publisher), it.title) }

            if (isOngoingManga == false) {
                volumes?.let { LabelInfoItem(stringResource(Res.string.text_volumes), it) }
                chapters?.let { LabelInfoItem(stringResource(Res.string.text_rate_chapters), it) }
            }

            origin?.let { LabelInfoItem(stringResource(Res.string.text_source), stringResource(it)) }

            ScoreInfo(score)

            rating?.let { LabelInfoItem(stringResource(Res.string.text_rating), stringResource(it)) }
        }
    }
}

fun LazyListScope.genres(genres: List<String>?) = genres?.let { list ->
    item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(list) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(it) }
                )
            }
        }
    }
}

fun LazyListScope.summary(
    similar: List<Content>,
    studio: Studio? = null,
    publisher: Publisher? = null,
    linkedType: LinkedType? = null,
    duration: ResourceText? = null,
    nextEpisodeAt: String? = null,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit
) = item {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        studio?.let { studio ->
            item {
                DetailBox(
                    icon = Res.drawable.vector_anime,
                    label = stringResource(Res.string.text_studio),
                    value = studio.title,
                    onClick = { onNavigate(Screen.Catalog(studio = studio.id)) }
                )
            }
        }

        publisher?.let { publisher ->
            item {
                DetailBox(
                    icon = Res.drawable.vector_anime,
                    label = stringResource(Res.string.text_publisher),
                    value = publisher.title,
                    onClick = {
                        onNavigate(
                            Screen.Catalog(
                                publisher = publisher.id,
                                linkedType = linkedType
                            )
                        )
                    }
                )
            }
        }

        duration?.let { duration ->
            item {
                DetailBox(
                    icon = Res.drawable.vector_timer,
                    label = stringResource(Res.string.text_episode),
                    value = duration.asComposableString()
                )
            }
        }

        nextEpisodeAt?.let { text ->
            if (text.isNotEmpty()) {
                item {
                    DetailBox(
                        icon = Res.drawable.vector_calendar,
                        label = stringResource(Res.string.text_episode_next),
                        value = text
                    )
                }
            }
        }

        if (similar.isNotEmpty()) {
            item {
                DetailBox(
                    icon = Res.drawable.vector_similar,
                    label = stringResource(Res.string.text_similar),
                    onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Similar)) }
                )
            }
        }

        item {
            DetailBox(
                icon = Res.drawable.vector_statistics,
                label = stringResource(Res.string.text_statistics),
                onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Stats)) }
            )
        }

        if (duration != null) {
            item {
                DetailBox(
                    icon = Res.drawable.vector_subtitles,
                    label = stringResource(Res.string.text_subtitles),
                    onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Anime.Fansubbers)) }
                )
            }

            item {
                DetailBox(
                    icon = Res.drawable.vector_voice_actors,
                    label = stringResource(Res.string.text_voices_one),
                    onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Anime.Fandubbers)) }
                )
            }
        }
    }
}

fun LazyListScope.description(text: AnnotatedString) = text.let { description ->
    if (description.isNotEmpty()) {
        item { Description(description) }
    }
}

fun LazyListScope.related(list: List<Related>, onShow: () -> Unit, onNavigate: (Screen) -> Unit) =
    list.let { related ->
        if (related.isNotEmpty()) {
            item {
                Related(
                    list = related,
                    onShowAll = onShow,
                    onNavigate = onNavigate
                )
            }
        }
    }

fun LazyListScope.profiles(
    profiles: List<BasicContent>,
    title: StringResource,
    onShow: () -> Unit,
    onNavigate: (String) -> Unit
) = profiles.let { list ->
    if (list.isNotEmpty()) {
        item {
            Profiles(
                list = list,
                title = stringResource(title),
                onShowFull = onShow,
                onNavigate = { onNavigate(it) }
            )
        }
    }
}

fun LazyListScope.about(
    title: StringResource,
    contentList: List<CommentContent>,
    onImageClick: (List<CommentContent.ImageContent>, Int) -> Unit
) {
    item {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))

            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    htmlContent(contentList, onImageClick)
}
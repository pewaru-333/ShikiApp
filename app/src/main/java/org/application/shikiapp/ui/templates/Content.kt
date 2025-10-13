@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.ui.templates

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.models.ui.Franchise
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.Label
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Score
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.RelationKind
import org.application.shikiapp.utils.enums.UserMenu
import org.application.shikiapp.utils.extensions.substringAfter
import org.application.shikiapp.utils.extensions.substringBefore
import org.application.shikiapp.utils.navigation.Screen
import kotlin.math.roundToInt

@Composable
fun Description(description: AnnotatedString, withDivider: Boolean = true) {
    val hasSpoiler = description.text.contains("спойлер", ignoreCase = true)
    val mainText = if (hasSpoiler) description.substringBefore("спойлер") else description
    val spoilerText = if (hasSpoiler) description.substringAfter("спойлер") else AnnotatedString(BLANK)

    var hasOverflow by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var maxLines by remember { mutableIntStateOf(8) }

    val isTextExpanded = !hasOverflow || maxLines > 8

    Column(Modifier.animateContentSize()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            ParagraphTitle(stringResource(R.string.text_description), Modifier.padding(bottom = 4.dp))

            if (hasOverflow || maxLines > 8) {
                IconButton(
                    onClick = {
                        maxLines = if (maxLines == 8) Int.MAX_VALUE else 8
                    }
                ) {
                    VectorIcon(
                        resId = if (maxLines == 8) R.drawable.vector_keyboard_arrow_down
                        else R.drawable.vector_keyboard_arrow_up
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
                        resId = if (isVisible) R.drawable.vector_keyboard_arrow_down
                        else R.drawable.vector_keyboard_arrow_up
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.text_spoiler),
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
    visible: Boolean,
    onNavigate: (String) -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_similar)) },
                navigationIcon = { NavigationIcon(hide) })
        }
    ) { values ->
        LazyColumn(Modifier, listState, values) {
            items(list) { item ->
                CatalogCardItem(
                    title = item.title,
                    kind = item.kind,
                    season = item.season,
                    status = item.status,
                    image = item.poster,
                    score = item.score,
                    onClick = { onNavigate(item.id) }
                )
            }
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
        IconButton(onShowFull) { VectorIcon(R.drawable.vector_arrow_forward) }
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onNavigate(it.id) }
            ) {
                AsyncImage(
                    model = it.poster,
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border((0.4).dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
                )

                Text(
                    minLines = 2,
                    maxLines = 2,
                    text = it.title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun ProfilesFull(
    list: List<BasicContent>,
    visible: Boolean,
    title: String,
    state: LazyListState,
    onHide: () -> Unit,
    onNavigate: (String) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, onHide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { NavigationIcon(onHide) }
            )
        }
    ) { values ->
        LazyColumn(Modifier, state, values) {
            items(list) {
                BasicContentItem(
                    name = it.title,
                    link = it.poster,
                    modifier = Modifier.clickable { onNavigate(it.id) }
                )
            }
        }
    }
}

@Composable
fun Related(list: List<Related>, showAllRelated: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ParagraphTitle(stringResource(R.string.text_related))
            TextButton(showAllRelated) { Text(stringResource(R.string.text_show_all_u)) }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(list.take(6)) { related ->
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
    franchise: Map<RelationKind, List<Franchise>>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    val tabs = listOf(
        stringResource(R.string.text_directly),
        stringResource(R.string.text_chronology),
        stringResource(R.string.text_franchise)
    )

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = tabs::size)

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collectLatest { page ->
            if (page != pagerState.settledPage) {
                onScroll(page)
            }
        }
    }

    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_related)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) {
            PrimaryTabRow(pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { onScroll(index) },
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
                        items(related) { item ->
                            CatalogCardItem(
                                title = item.title,
                                kind = item.kind,
                                season = item.season,
                                status = item.status,
                                image = item.poster,
                                onClick = { onNavigate(item.linkedType.navigateTo(item.id)) },
                                score = item.score,
                                relationText = item.relationText
                            )
                        }
                    }

                    1 -> LazyColumn {
                        items(chronology) { item ->
                            CatalogCardItem(
                                title = item.title,
                                kind = item.kind,
                                season = item.season,
                                status = item.status,
                                image = item.poster,
                                onClick = { onNavigate(item.kind.linkedType.navigateTo(item.id)) },
                                score = item.score,
                            )
                        }
                    }

                    2 -> LazyColumn {
                        franchise.forEach { (relation, items) ->
                            stickyHeader {
                                TextStickyHeader(stringResource(relation.title))
                            }

                            items(items) { item ->
                                FranchiseCard(
                                    id = item.id,
                                    title = item.title,
                                    poster = item.poster,
                                    kind = item.kind,
                                    season = item.year.asString(),
                                    type = item.linkedType,
                                    onNavigate = onNavigate,
                                )
                            }
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
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_related)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) { // Без этого stickyHeader не двигается при прокрутке
            LazyColumn {
                related.forEach { (type, items) ->
                    stickyHeader {
                        TextStickyHeader(stringResource(type.title))
                    }

                    items(items) { item ->
                        FranchiseCard(
                            id = item.id,
                            title = item.title,
                            poster = item.poster,
                            kind = item.kind,
                            season = item.season.asString(),
                            type = item.linkedType,
                            role = item.relationText,
                            onNavigate = onNavigate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreInfo(score: String) = Column {
    Text(
        text = stringResource(R.string.text_score),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        VectorIcon(R.drawable.vector_star, Modifier.size(16.dp), Color(0xFFFFC319))
        Text(
            text = score,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun StatusInfo(@StringRes status: Int, airedOn: String, releasedOn: String) = Column {
    Text(
        text = stringResource(R.string.text_status),
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
            airedOn.let {
                if (it.isNotEmpty()) {
                    append(stringResource(R.string.text_date_from, it))
                }
            }

            releasedOn.let {
                if (it.isNotEmpty()) {
                    append(" ${stringResource(R.string.text_date_till, it)}")
                }
            }
        }
    )
}

@Composable
fun Statuses(
    scores: Map<Label, Score>,
    sum: Int,
    @StringRes label: Int,
    content: (@Composable () -> Unit)? = null,
) {
    val textStyle = MaterialTheme.typography.labelLarge
    val minBarWidth = 40.dp.value.roundToInt()
    val gap = 8.dp.value.roundToInt()

    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()

    val maxStatusTextWidth = remember(scores) {
        scores.keys.maxOf {
            textMeasurer.measure(AnnotatedString(it.asString(context)), textStyle).size.width
        }
    }

    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        ParagraphTitle(stringResource(label), Modifier.padding(bottom = 4.dp))
        content?.invoke()
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        scores.entries.filter { it.value.toInt() > 0 }.forEach { (key, value) ->
            Layout(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .height(24.dp)
                    )

                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )

                    Text(
                        text = key.asString(),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
            ) { measurables, constraints ->
                val (horizontalBar, textCount, textStatus) = measurables

                val countTextPlaceable = textCount.measure(Constraints())
                val statusTextPlaceable = textStatus.measure(
                    Constraints(0, constraints.maxWidth, 0, constraints.maxHeight)
                )

                val countTextWidth = countTextPlaceable.width + 4.dp.roundToPx()
                val idealWidth = (constraints.maxWidth * (value.toFloat() / sum)).roundToInt()
                val maxAvailableWidth = constraints.maxWidth - maxStatusTextWidth - gap


                val calculatedBarWidth = maxOf(minBarWidth, countTextWidth, idealWidth)
                val finalBarWidth = minOf(calculatedBarWidth, maxAvailableWidth)


                val barPlaceable = horizontalBar.measure(
                    Constraints(finalBarWidth, finalBarWidth, 0, constraints.maxHeight)
                )

                val rowHeight = maxOf(
                    barPlaceable.height,
                    statusTextPlaceable.height,
                    countTextPlaceable.height
                )

                layout(constraints.maxWidth, rowHeight) {
                    barPlaceable.placeRelative(
                        x = 0,
                        y = Alignment.CenterVertically.align(
                            barPlaceable.height,
                            rowHeight
                        )
                    )

                    countTextPlaceable.placeRelative(
                        x = finalBarWidth - countTextPlaceable.width - 4.dp.roundToPx(),
                        y = Alignment.CenterVertically.align(
                            countTextPlaceable.height,
                            rowHeight
                        )
                    )

                    statusTextPlaceable.placeRelative(
                        x = constraints.maxWidth - statusTextPlaceable.width,
                        y = Alignment.CenterVertically.align(
                            statusTextPlaceable.height,
                            rowHeight
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Statistics(id: Long, statistics: Pair<Statistics?, Statistics?>, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        statistics.first?.let {
            Statuses(
                scores = it.scores,
                sum = it.sum,
                label = R.string.text_anime_list,
                content = {
                    TextButton(
                        onClick = { onNavigate(Screen.UserRates(id.toString(), LinkedType.ANIME)) },
                        content = { Text(stringResource(R.string.text_show_all_s)) }
                    )
                }
            )
        }

        statistics.second?.let {
            HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))

            Statuses(
                scores = it.scores,
                sum = it.sum,
                label = R.string.text_manga_list,
                content = {
                    TextButton(
                        onClick = { onNavigate(Screen.UserRates(id.toString(), LinkedType.MANGA)) },
                        content = { Text(stringResource(R.string.text_show_all_s)) }
                    )
                }
            )
        }
    }

@Composable
fun Statistics(statistics: Pair<Statistics?, Statistics?>, visible: Boolean, hide: () -> Unit) =
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        BackHandler(visible, hide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.text_statistics)) },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statistics.first?.let {
                    item {
                        Statuses(
                            scores = it.scores,
                            sum = it.sum,
                            label = R.string.text_user_rates
                        )
                    }
                }

                statistics.second?.let {
                    item {
                        Statuses(
                            scores = it.scores,
                            sum = it.sum,
                            label = R.string.text_in_lists
                        )
                    }
                }
            }
        }
    }

@Composable
fun DialogScreenshot(
    list: List<String>,
    screenshot: Int,
    visible: Boolean,
    setScreenshot: (Int) -> Unit,
    hide: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = list::size)

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collectLatest(setScreenshot)
    }

    LaunchedEffect(screenshot) {
        if (screenshot != pagerState.currentPage) {
            pagerState.scrollToPage(screenshot)
        }
    }

    AnimatedVisibility(visible, Modifier, fadeIn(), fadeOut()) {
        BackHandler(visible, hide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                R.string.text_image_of,
                                pagerState.currentPage + 1,
                                list.size
                            )
                        )
                    },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            HorizontalPager(pagerState, Modifier.fillMaxSize(), values) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    ZoomableAsyncImage(list[it], null, Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun DialogFavourites(
    favourites: Map<FavouriteItem, List<BasicContent>>,
    visible: Boolean,
    listStates: List<LazyListState>,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = favourites::size)
    val navigate = remember(pagerState) {
        { id: String -> FavouriteItem.entries[pagerState.currentPage].linkedType.navigateTo(id) }
    }

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collectLatest { page ->
            if (page != pagerState.settledPage) {
                onScroll(page)
            }
        }
    }

    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_favourite)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) {
            PrimaryScrollableTabRow(pagerState.currentPage, edgePadding = 8.dp) {
                FavouriteItem.entries.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { onScroll(index) },
                        text = { Text(stringResource(item.title)) }
                    )
                }
            }

            HorizontalPager(pagerState) { tab ->
                LazyColumn(Modifier.fillMaxSize(), listStates[tab]) {
                    items(
                        favourites.getOrDefault(FavouriteItem.entries[tab], emptyList()),
                        BasicContent::id
                    ) {
                        BasicContentItem(
                            name = it.title,
                            link = it.poster,
                            modifier = Modifier.clickable { onNavigate(navigate(it.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogHistory(
    history: LazyPagingItems<History>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_history)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            items(history.itemCount, history.itemKey(History::id)) { index ->
                history[index]?.let { node ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .clickable(enabled = node.contentId != null) {
                                node.contentId?.let { onNavigate(node.kind.linkedType.navigateTo(it)) }
                            }
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = node.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp, 121.dp)
                                .clip(MaterialTheme.shapes.small)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 121.dp)
                        ) {
                            Column {
                                Text(
                                    text = node.title,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = node.description,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Text(
                                text = node.date,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogFriends(
    friends: LazyPagingItems<BasicContent>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_friends)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            friends(friends, onNavigate)
        }
    }
}

@Composable
fun DialogClubs(
    clubs: List<BasicContent>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(visible, hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_clubs)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            clubs(clubs, onNavigate)
        }
    }
}

@Composable
fun UserBriefItem(user: User) = ListItem(
    headlineContent = { Text(user.lastOnline, style = MaterialTheme.typography.bodyMedium) },
    modifier = Modifier.offset((-16).dp, (-8).dp),
    overlineContent = { Text(user.nickname, style = MaterialTheme.typography.titleLarge) },
    leadingContent = {
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(88.dp)
                .clip(MaterialTheme.shapes.small)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
        )
    },
    supportingContent = {
        Text(
            text = user.commonInfo,
            style = MaterialTheme.typography.bodySmall
        )
    }
)

@Composable
fun UserMenuItems(setMenu: (UserMenu) -> Unit) =
    Column(Modifier.wrapContentHeight(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
        UserMenu.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
                row.forEach { entry ->
                    FilterChip(
                        selected = true,
                        label = { Text(stringResource(entry.title)) },
                        trailingIcon = { VectorIcon(R.drawable.vector_keyboard_arrow_right) },
                        onClick = { setMenu(entry) },
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }
package org.application.shikiapp.screens

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
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_add_fav
import org.application.shikiapp.R.string.text_add_rate
import org.application.shikiapp.R.string.text_change_rate
import org.application.shikiapp.R.string.text_comments
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_external_links
import org.application.shikiapp.R.string.text_favourite
import org.application.shikiapp.R.string.text_history
import org.application.shikiapp.R.string.text_image_of
import org.application.shikiapp.R.string.text_related
import org.application.shikiapp.R.string.text_remove_fav
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_statistics
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_user_rates
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.ExternalLink
import org.application.shikiapp.models.ui.Franchise
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.Label
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Score
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.RelationKind
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.enums.UserMenu
import org.application.shikiapp.utils.enums.backgroundColor
import org.application.shikiapp.utils.enums.textColor
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.substringAfter
import org.application.shikiapp.utils.extensions.substringBefore
import org.application.shikiapp.utils.navigation.Screen
import kotlin.math.roundToInt

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) = Box(modifier.fillMaxSize(), Alignment.Center) {
    CircularProgressIndicator()
}

@Composable
fun ErrorScreen(retry: () -> Unit = {}) = Column(Modifier.fillMaxSize(), Center, CenterHorizontally) {
    Text("Ошибка загрузки!")
    Button(retry) { Text("Повторить") }
}

@Composable
fun ParagraphTitle(text: String, modifier: Modifier = Modifier) = Text(
    text = text,
    modifier = modifier,
    color = MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W500)
)

@Composable
fun Poster(link: String) = AsyncImage(
    model = link,
    contentDescription = null,
    contentScale = ContentScale.FillHeight,
    filterQuality = FilterQuality.High,
    modifier = Modifier
        .size(175.dp, 300.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
)

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogCardItem(
    title: String,
    kind: Kind,
    season: ResourceText,
    score: String?,
    status: Status,
    image: String?,
    relationText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Row(
    modifier = modifier
        .clickable(onClick = onClick)
        .padding(8.dp)
) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (score != null) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(Modifier.padding(6.dp, 4.dp), spacedBy(4.dp), CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC319),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = score,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    Spacer(Modifier.width(12.dp))

    Column(Modifier.weight(1f), spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            text = buildString {
                append(stringResource(kind.title))

                season.asString().let {
                    if (it.isNotEmpty()) {
                        append(" · $it")
                    }
                }

                relationText?.let {
                    append(" · $it")
                }
            }
        )

        Surface(Modifier, MaterialTheme.shapes.small, status.backgroundColor) {
            Text(
                text = stringResource(status.getTitle(kind)),
                modifier = Modifier.padding(8.dp, 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = status.textColor,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogCardItem(
    title: String,
    image: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable(onClick = onClick)
) {
    AsyncImage(
        model = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
    )

    Spacer(Modifier.width(12.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )

}

@Composable
fun CatalogListItem(
    title: String,
    kind: Kind?,
    modifier: Modifier = Modifier,
    season: ResourceText,
    image: String?,
    click: () -> Unit
) = ListItem(
    modifier = modifier.clickable(onClick = click),
    headlineContent = { Text(stringResource(kind?.title ?: R.string.blank)) },
    supportingContent = season.let { { Text(it.asString()) } },
    overlineContent = {
        Text(
            text = title,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.W500
            )
        )
    },
    leadingContent = {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .size(120.dp, 180.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        )
    }
)

@Composable
fun CatalogGridItem(
    title: String,
    image: String?,
    score: String?,
    kind: Kind?,
    season: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors().copy(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (score != null) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                Row(Modifier.padding(6.dp, 4.dp), spacedBy(4.dp), CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC319),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = score,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Surface(
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp),
        ) {
            BasicText(
                maxLines = 1,
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                text = buildString {
                    kind?.let { append(stringResource(it.title)) }

                    if (kind != null && season != null) {
                        append(" • ")
                        append(season)
                    }
                },
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            )
        }
    }

    Column(
        verticalArrangement = Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
fun UserGridItem(title: String, imageUrl: String?, onClick: () -> Unit) =
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(4.dp)
                .clip(CircleShape)
        )

        Text(
            maxLines = 1,
            text = title,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
    }

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
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle("Описание", Modifier.padding(bottom = 4.dp))

            if (hasOverflow || maxLines > 8) {
                IconButton(
                    onClick = {
                        maxLines = if (maxLines == 8) Int.MAX_VALUE else 8
                    }
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = if (maxLines == 8) Icons.Outlined.KeyboardArrowDown
                        else Icons.Outlined.KeyboardArrowUp,
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
                Row(verticalAlignment = CenterVertically) {
                    Icon(
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        imageVector = if (isVisible) Icons.Default.KeyboardArrowDown
                        else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Спойлер",
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
fun Comment(comment: Comment, onNavigate: (Screen) -> Unit) = Column {
    ListItem(
        modifier = Modifier.offset(x = (-8).dp),
        headlineContent = { Text(comment.user.nickname) },
        supportingContent = { Text(convertDate(comment.createdAt)) },
        leadingContent = {
            AsyncImage(
                model = comment.user.image.x160,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable { onNavigate(Screen.User(comment.userId)) },
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
            )
        }
    )
    HtmlComment(comment.htmlBody.trimIndent())
    HorizontalDivider(Modifier.padding(top = 8.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    list: LazyPagingItems<Comment>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_comments)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding())
        ) {
            when (list.loadState.refresh) {
                is Error -> item { ErrorScreen(list::retry) }
                Loading -> item { LoadingScreen() }
                is NotLoading -> items(list.itemCount) { Comment(list[it]!!, onNavigate) }
            }
            if (list.loadState.append == Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

@Composable
fun OneLineImage(name: String, link: String?, modifier: Modifier = Modifier) = ListItem(
    headlineContent = { Text(name) },
    modifier = modifier,
    leadingContent = {
        AsyncImage(
            model = link,
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        )
    }
)

@Composable
fun HistoryItem(note: History, onNavigate: (Screen) -> Unit) =
    ListItem(
        trailingContent = { Text(note.date) },
        supportingContent = { Text(note.description) },
        modifier = Modifier.clickable {
            note.kind?.let { kind ->
                val type = Kind.entries.first { it.safeEquals(kind) }.linkedType

                onNavigate(type.navigateTo(note.contentId))
            }
        },
        headlineContent = {
            Text(
                text = note.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            AsyncImage(
                model = note.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp, 121.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        (0.5).dp,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.shapes.medium
                    )
            )
        }
    )

@Composable
fun Related(list: List<Related>, showAllRelated: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            ParagraphTitle("Связанное")
            TextButton(showAllRelated) { Text("Показать всё") }
        }

        LazyRow(
            horizontalArrangement = spacedBy(12.dp),
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
private fun RelatedCard(title: String, poster: String, relationText: String, onClick: () -> Unit) =
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .height(170.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = poster,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            BasicText(
                maxLines = relationText.split(" ").size.coerceAtMost(3),
                text = buildString {
                    relationText.uppercase().let { text ->
                        text.split(" ").let { words ->
                            if (words.size == 1) {
                                append(words.first())
                            } else {
                                words.forEach { word ->
                                    append("$word\n")
                                }
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            maxLines = 2,
            minLines = 2,
            text = title,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarFull(
    list: List<BasicContent>,
    listState: LazyListState,
    visible: Boolean,
    onNavigate: (String) -> Unit,
    hide: () -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_similar)) },
                navigationIcon = { NavigationIcon(hide) })
        }
    ) { values ->
        LazyColumn(
            state = listState,
            contentPadding = values,
        ) {
            items(list) {
                Row(
                    horizontalArrangement = spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(8.dp)
                        .clickable { onNavigate(it.id) }
                ) {
                    AsyncImage(
                        model = it.poster,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.small)
                            .border(
                                (0.5).dp,
                                MaterialTheme.colorScheme.onSurface,
                                MaterialTheme.shapes.small
                            )
                    )

                    Column(Modifier.fillMaxHeight(), Center) {
                        Text(
                            maxLines = 3,
                            text = it.title,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
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
    Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        ParagraphTitle(title)
        IconButton(onShowFull) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) {
            Column(
                verticalArrangement = spacedBy(4.dp),
                horizontalAlignment = CenterHorizontally,
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

@OptIn(ExperimentalMaterial3Api::class)
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
    BackHandler(onBack = onHide)
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
                OneLineImage(
                    name = it.title,
                    link = it.poster,
                    modifier = Modifier.clickable { onNavigate(it.id) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
    val tabs = listOf("Напрямую", "Хронология", "Франшиза")

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = tabs::size)

    fun onScroll(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::settledPage).collectLatest(::onScroll)
    }

    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_related)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) {
            TabRow(pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { onScroll(index) }
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(8.dp, 12.dp),
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.05f
                )
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

@OptIn(ExperimentalMaterial3Api::class)
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
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_related)) },
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
fun TextStickyHeader(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.tertiaryContainer)
        .padding(16.dp, 8.dp)
)

@Composable
fun FranchiseCard(
    id: String,
    title: String,
    poster: String,
    kind: Kind,
    season: Any?,
    type: LinkedType,
    role: String? = null,
    onNavigate: (Screen) -> Unit
) = Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .clickable { onNavigate(type.navigateTo(id)) }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = poster,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp, 120.dp)
                .clip(MaterialTheme.shapes.small)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                maxLines = 3,
                text = title,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (role != null) {
                Text(
                    text = role,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = buildString {
                    append(stringResource(kind.title))

                    season?.let { append(" · $it") }
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }

@Composable
fun ScoreInfo(score: String) = Column {
    Text(
        text = stringResource(text_score),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    )
    Row(horizontalArrangement = spacedBy(4.dp), verticalAlignment = CenterVertically) {
        Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
        Text(
            text = score,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun StatusInfo(@StringRes status: Int, airedOn: String, releasedOn: String) = Column {
    Text(
        text = stringResource(text_status),
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
                    append("с $it")
                }
            }

            releasedOn.let {
                if (it.isNotEmpty()) {
                    append(" по $it")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    state: SheetState,
    rate: UserRate?,
    favoured: Boolean,
    toggleFavourite: () -> Unit,
    onEvent: (ContentDetailEvent) -> Unit,
) = ModalBottomSheet(
    sheetState = state,
    onDismissRequest = { onEvent(ContentDetailEvent.ShowSheet) },
) {
    if (Preferences.token != null) {
        ListItem(
            leadingContent = { Icon(Icons.Outlined.Edit, null) },
            headlineContent = {
                Text(stringResource(rate?.let { text_change_rate } ?: text_add_rate))
            },
            modifier = Modifier.clickable {
                onEvent(ContentDetailEvent.Media.ShowRate)
            }
        )
        ListItem(
            headlineContent = { Text(stringResource(if (favoured) text_remove_fav else text_add_fav)) },
            modifier = Modifier.clickable(onClick = toggleFavourite),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (favoured) Color.Red else LocalContentColor.current
                )
            }
        )
    }
    ListItem(
        headlineContent = { Text(stringResource(text_external_links)) },
        leadingContent = { Icon(Icons.AutoMirrored.Outlined.List, null) },
        modifier = Modifier.clickable {
            onEvent(ContentDetailEvent.Media.ShowLinks)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        BackHandler(onBack = hide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_image_of, pagerState.currentPage + 1, list.size)) },
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

    Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        ParagraphTitle(stringResource(label), Modifier.padding(bottom = 4.dp))
        content?.invoke()
    }
    Column(verticalArrangement = spacedBy(8.dp)) {
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

                val rowHeight = maxOf(barPlaceable.height, statusTextPlaceable.height, countTextPlaceable.height)

                layout(constraints.maxWidth, rowHeight) {
                    barPlaceable.placeRelative(
                        x = 0,
                        y = CenterVertically.align(barPlaceable.height, rowHeight)
                    )

                    countTextPlaceable.placeRelative(
                        x = finalBarWidth - countTextPlaceable.width - 4.dp.roundToPx(),
                        y = CenterVertically.align(countTextPlaceable.height, rowHeight)
                    )

                    statusTextPlaceable.placeRelative(
                        x = constraints.maxWidth - statusTextPlaceable.width,
                        y = CenterVertically.align(statusTextPlaceable.height, rowHeight)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(id: Long, statistics: Pair<Statistics?, Statistics?>, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(16.dp)) {
        statistics.first?.let {
            Statuses(
                scores = it.scores,
                sum = it.sum,
                label = R.string.text_anime_list,
                content = {
                    TextButton(
                        onClick = { onNavigate(Screen.UserRates(id, LinkedType.ANIME)) },
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
                        onClick = { onNavigate(Screen.UserRates(id, LinkedType.MANGA)) },
                        content = { Text(stringResource(R.string.text_show_all_s)) }
                    )
                }
            )
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(statistics: Pair<Statistics?, Statistics?>, visible: Boolean, hide: () -> Unit) =
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        BackHandler(onBack = hide)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_statistics)) },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                statistics.first?.let {
                    item {
                        Statuses(
                            scores = it.scores,
                            sum = it.sum,
                            label = text_user_rates
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetColumn(list: List<String>, state: SheetState, label: String, onHide: () -> Unit) =
    ModalBottomSheet(onHide, sheetState = state) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = label,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn {
            items(list) { item ->
                ListItem(
                    headlineContent = { Text(item) },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LinksSheet(
    list: List<ExternalLink>,
    state: SheetState,
    hide: () -> Unit,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(hide, sheetState = state) {
    LazyColumn {
        items(list) {
            ListItem(
                modifier = Modifier.clickable { handler.openUri(it.url.toString()) },
                headlineContent = { Text(it.title) },
                leadingContent = {
                    AsyncImage(
                        contentDescription = null,
                        model = "https://www.google.com/s2/favicons?domain=${it.url.host}&sz=128",
                        modifier = Modifier.size(24.dp),
                        filterQuality = FilterQuality.High
                    )
                }
            )
        }
    }

    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogFavourites(
    hide: () -> Unit,
    setTab: (FavouriteItem) -> Unit,
    tab: FavouriteItem,
    visible: Boolean,
    favourites: Favourites,
    onNavigate: (Screen) -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    val navigate = remember(tab) {
        when (tab) {
            FavouriteItem.ANIME -> { id: Long -> Screen.Anime(id.toString()) }
            FavouriteItem.MANGA, FavouriteItem.RANOBE -> { id: Long -> Screen.Manga(id.toString()) }
            FavouriteItem.CHARACTERS -> { id: Long -> Screen.Character(id.toString()) }
            else -> { id: Long -> Screen.Person(id) }
        }
    }

    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_favourite)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(top = values.calculateTopPadding()), spacedBy(8.dp)) {
            ScrollableTabRow(tab.ordinal, edgePadding = 8.dp) {
                FavouriteItem.entries.forEach {
                    Tab(
                        selected = tab == it,
                        onClick = { setTab(it) }
                    ) {
                        Text(stringResource(it.title), Modifier.padding(8.dp, 12.dp))
                    }
                }
            }
            LazyColumn {
                items(tab.getFavouriteList(favourites)) {
                    OneLineImage(
                        name = it.russian.orEmpty().ifEmpty(it::name),
                        link = it.image,
                        modifier = Modifier.clickable {
                            onNavigate(navigate(it.id))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogHistory(
    hide: () -> Unit,
    visible: Boolean,
    history: LazyPagingItems<History>,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_history)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = values) {
            items(history.itemCount) { index ->
                history[index]?.let { HistoryItem(it, onNavigate) }
            }
        }
    }
}

@Composable
fun UserMenuItems(setMenu: (UserMenu) -> Unit) =
    Column(Modifier.wrapContentHeight(), spacedBy(8.dp), CenterHorizontally) {
        UserMenu.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), spacedBy(48.dp), CenterVertically) {
                row.forEach { entry ->
                    FilterChip(
                        selected = true,
                        label = { Text(stringResource(entry.title)) },
                        trailingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
                        onClick = { setMenu(entry) },
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogFriends(
    friends: LazyPagingItems<UserBasic>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogClubs(
    clubs: List<ClubBasic>,
    visible: Boolean,
    hide: () -> Unit,
    onNavigate: (Screen) -> Unit
) = AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it })
) {
    BackHandler(onBack = hide)
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

// ========================================== Extensions ===========================================

fun LazyListScope.friends(list: LazyPagingItems<UserBasic>, onNavigate: (Screen) -> Unit) {
    when (list.loadState.refresh) {
        is Error -> item { ErrorScreen(list::retry) }
        Loading -> item { LoadingScreen() }
        is NotLoading -> {
            items(list.itemCount) { index ->
                list[index]?.let {
                    OneLineImage(
                        name = it.nickname,
                        link = it.image.x160,
                        modifier = Modifier.clickable { onNavigate(Screen.User(it.id)) }
                    )
                }
            }
            if (list.loadState.append == Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

fun LazyListScope.clubs(list: List<ClubBasic>, onNavigate: (Screen) -> Unit) =
    if (list.isEmpty()) item {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text(stringResource(text_empty)) }
    }
    else items(list) {
        OneLineImage(
            name = it.name,
            link = it.logo.original,
            modifier = Modifier.clickable { onNavigate(Screen.Club(it.id)) }
        )
    }
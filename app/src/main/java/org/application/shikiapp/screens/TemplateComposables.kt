package org.application.shikiapp.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.flow.collectLatest
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_add_fav
import org.application.shikiapp.R.string.text_add_rate
import org.application.shikiapp.R.string.text_anime_list
import org.application.shikiapp.R.string.text_authors
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_change_rate
import org.application.shikiapp.R.string.text_characters
import org.application.shikiapp.R.string.text_comment
import org.application.shikiapp.R.string.text_comments
import org.application.shikiapp.R.string.text_empty
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_external_links
import org.application.shikiapp.R.string.text_favourite
import org.application.shikiapp.R.string.text_history
import org.application.shikiapp.R.string.text_image_of
import org.application.shikiapp.R.string.text_manga_list
import org.application.shikiapp.R.string.text_rate
import org.application.shikiapp.R.string.text_rate_chapters
import org.application.shikiapp.R.string.text_related
import org.application.shikiapp.R.string.text_remove
import org.application.shikiapp.R.string.text_remove_fav
import org.application.shikiapp.R.string.text_rereadings
import org.application.shikiapp.R.string.text_rewatches
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_show_all_s
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_statistics
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_user_rates
import org.application.shikiapp.R.string.text_volumes
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.RateEvent
import org.application.shikiapp.events.RateEvent.SetChapters
import org.application.shikiapp.events.RateEvent.SetEpisodes
import org.application.shikiapp.events.RateEvent.SetRateId
import org.application.shikiapp.events.RateEvent.SetRewatches
import org.application.shikiapp.events.RateEvent.SetScore
import org.application.shikiapp.events.RateEvent.SetStatus
import org.application.shikiapp.events.RateEvent.SetText
import org.application.shikiapp.events.RateEvent.SetVolumes
import org.application.shikiapp.generated.fragment.UserRateF
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.ShortInfo
import org.application.shikiapp.models.data.Stats
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.ui.CharacterMain
import org.application.shikiapp.models.ui.ExternalLink
import org.application.shikiapp.models.ui.PersonMain
import org.application.shikiapp.models.ui.Related
import org.application.shikiapp.models.ui.Similar
import org.application.shikiapp.models.ui.Statistics
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.HtmlComment
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Score
import org.application.shikiapp.utils.enums.UserMenu
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.safeValueOf
import org.application.shikiapp.utils.extensions.substringAfter
import org.application.shikiapp.utils.extensions.substringBefore
import org.application.shikiapp.utils.getWatchStatus
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
    modifier = Modifier
        .size(175.dp, 300.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentDescription = null,
    contentScale = ContentScale.FillHeight,
    filterQuality = FilterQuality.High
)

@Composable
fun CircleImage(link: String) = AsyncImage(
    model = link,
    contentDescription = null,
    modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .border((0.4).dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
    alignment = Alignment.Center,
    contentScale = ContentScale.Crop,
    filterQuality = FilterQuality.High,
)

@Composable
fun UserBriefItem(user: org.application.shikiapp.models.ui.User) = ListItem(
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
    @StringRes kind: Int,
    season: ResourceText,
    score: String?,
    image: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    shape = RectangleShape,
    colors = CardDefaults.cardColors().copy(
        containerColor = ListItemDefaults.containerColor
    )
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.Top
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
                    append(stringResource(kind))

                    season.asString().let {
                        if (it.isNotEmpty()) {
                            append(" · $it")
                        }
                    }
                }
            )

            Spacer(Modifier.weight(1f))

            if (score != null) {
                Row(Modifier.offset(x = (-4).dp), spacedBy(4.dp), CenterVertically) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Star,
                        tint = Color(0xFFFFC319),
                        contentDescription = null
                    )

                    Text(
                        text = score,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogListItem(
    title: String,
    @StringRes kind: Int,
    modifier: Modifier = Modifier,
    season: ResourceText,
    image: String?,
    click: () -> Unit
) = ListItem(
    modifier = modifier.clickable(onClick = click),
    headlineContent = { Text(stringResource(kind)) },
    supportingContent = season.let { { Text(it.asString()) } } ,
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
    Column {
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
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 8.dp)
        )
    }
}

@Composable
fun UserGridItem(title: String, imageUrl: String?, onClick: () -> Unit) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
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
fun RoundedPoster(link: String) = AsyncImage(
    model = link,
    contentDescription = null,
    contentScale = ContentScale.FillBounds,
    filterQuality = FilterQuality.High,
    modifier = Modifier
        .size(120.dp, 180.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
)

@Composable
fun RoundedRelatedPoster(link: String) = AsyncImage(
    model = link,
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentScale = ContentScale.Crop,
    filterQuality = FilterQuality.High,
)

@Composable
fun Names(names: List<String?>) {
    Column(Modifier.padding(horizontal = 8.dp), spacedBy(8.dp)) {
        names[0]?.let { Text(text = it, style = MaterialTheme.typography.titleLarge) }
        names[1]?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
        names[2]?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
fun Birthday(text: String) = Column(Modifier.padding(horizontal = 8.dp)) {
    Text("Дата рождения:", style = MaterialTheme.typography.titleSmall)
    Text(text, style = MaterialTheme.typography.labelMedium)
}

@Composable
fun Deathday(text: String) = Column(Modifier.padding(horizontal = 8.dp)) {
    Text("Дата смерти:", style = MaterialTheme.typography.titleSmall)
    Text(text, style = MaterialTheme.typography.labelMedium)
}

@Composable
fun AnimatedAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        filterQuality = FilterQuality.High,
        success = { SubcomposeAsyncImageContent() },
        loading = {
            val shimmerColors = listOf(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
            )

            val transition = rememberInfiniteTransition()
            val translateAnim = transition.animateFloat(
                initialValue = 0f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    repeatMode = RepeatMode.Reverse,
                    animation = tween(
                        durationMillis = 1200,
                        easing = FastOutSlowInEasing
                    )
                )
            )

            val brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset.Zero,
                end = Offset(x = translateAnim.value, y = translateAnim.value)
            )

            Spacer(
                modifier = modifier
                    .fillMaxSize()
                    .background(brush)
            )
        },
        error = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Icon(
                    contentDescription = null,
                    painter = painterResource(R.drawable.vector_bad),
                    modifier = Modifier.fillMaxSize(0.75f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun TextCircleImage(text: String) = Text(
    text = text,
    modifier = Modifier.width(64.dp),
    textAlign = TextAlign.Center,
    overflow = TextOverflow.Ellipsis,
    minLines = 2,
    maxLines = 2,
    style = MaterialTheme.typography.labelMedium
)

@Composable
fun RelatedText(text: String) = Text(
    text = text,
    modifier = Modifier.fillMaxWidth(),
    textAlign = TextAlign.Center,
    overflow = TextOverflow.Ellipsis,
    maxLines = 3,
    minLines = 3,
    style = MaterialTheme.typography.labelLarge
)

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
                    .padding(12.dp, 8.dp)
                    .clickable { isVisible = !isVisible }
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
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
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
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
        )
    }
)

@Composable
fun HistoryItem(note: org.application.shikiapp.models.ui.History, onNavigate: (Screen) -> Unit) =
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
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }

fun localizeNames(text: String) : String {
    val fullPattern = Regex("""<span class="name-en">(.*?)</span><span class="name-ru">(.*?)</span>""")
    val englishPattern = Regex("""<span class="name-en">(.*?)</span>""")
    val russianPattern = Regex("""<span class="name-ru">(.*?)</span>""")

    var modifiedHtml = text.replace(fullPattern, """<span class="name-ru">$2</span>""")

    if (!modifiedHtml.contains("<span class=\"name-ru\">")) {
        modifiedHtml = modifiedHtml.replace(englishPattern, """<span class="name-en">$1</span>""")
    }

    return modifiedHtml
        .replace(russianPattern, "$1")
        .replace(englishPattern, "$1")
}

fun fromHtml(text: String?) = if (text == null) AnnotatedString(BLANK)
else AnnotatedString.Companion.fromHtml(
    htmlString = localizeNames(text),
    linkStyles = TextLinkStyles(
        SpanStyle(
            color = Color(0xFF33BBFF),
            textDecoration = TextDecoration.Underline,
            platformStyle = PlatformSpanStyle.Default
        )
    )
)

@Composable
fun Related(list: List<Related>, hide: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_related), Modifier.padding(bottom = 4.dp))
            TextButton(hide) { Text(stringResource(text_show_all_w)) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list.take(4)) { related ->
                Column(
                    modifier = Modifier
                        .width(120.dp)
                        .clickable {
                            related.animeId?.let { onNavigate(Screen.Anime(it)) }
                            related.mangaId?.let { onNavigate(Screen.Manga(it)) }
                        }
                ) {
                    RoundedRelatedPoster(related.poster)
                    RelatedText(related.title)
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarFull(
    list: List<Similar>,
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
            verticalArrangement = spacedBy(8.dp)
        ) {
            items(list) {
                ListItem(
                    headlineContent = { Text(it.title) },
                    modifier = Modifier.clickable { onNavigate(it.id) },
                    leadingContent = {
                        AsyncImage(
                            model = it.poster,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    width = (0.5).dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun Characters(list: List<CharacterMain>, show: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_characters))
            IconButton(show) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        }
        LazyRow(
            horizontalArrangement = spacedBy(12.dp),
            verticalAlignment = CenterVertically
        ) {
            items(list) {
                Column(
                    modifier = Modifier.clickable { onNavigate(Screen.Character(it.id)) },
                    verticalArrangement = spacedBy(4.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    CircleImage(it.poster)
                    TextCircleImage(it.name)
                }
            }
        }
    }

@Composable
fun Authors(list: List<PersonMain>, show: () -> Unit, onNavigate: (Screen) -> Unit) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_authors))
            IconButton(show) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        }
        LazyRow(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = CenterVertically
        ) {
            items(list) {
                Column(
                    modifier = Modifier.clickable { onNavigate(Screen.Person(it.id)) },
                    verticalArrangement = spacedBy(4.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    CircleImage(it.poster)
                    TextCircleImage(it.name)
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RelatedFull(
    list: List<Related>,
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
        LazyColumn(contentPadding = values) {
            items(list) { related ->
                ListItem(
                    supportingContent = { Text(related.relationText) },
                    headlineContent = {
                        Text(
                            text = related.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.clickable {
                        related.animeId?.let { onNavigate(Screen.Anime(it)) }
                        related.mangaId?.let { onNavigate(Screen.Manga(it)) }
                    },
                    leadingContent = {
                        AsyncImage(
                            model = related.poster,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp, 121.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    width = (0.5).dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CharactersFull(
    list: List<CharacterMain>,
    state: LazyListState,
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
                title = { Text(stringResource(text_characters)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(Modifier, state, values) {
            items(list) {
                OneLineImage(
                    name = it.name,
                    link = it.poster,
                    modifier = Modifier.clickable { onNavigate(Screen.Character(it.id)) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AuthorsFull(
    roles: List<PersonMain>,
    state: LazyListState,
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
                title = { Text(stringResource(text_authors)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(Modifier, state, values) {
            items(roles) {
                OneLineImage(
                    name = it.name,
                    link = it.poster,
                    modifier = Modifier.clickable { onNavigate(Screen.Person(it.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    state: SheetState,
    rate: UserRateF?,
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
        headlineContent = { Text(stringResource(text_similar)) },
        leadingContent = { Icon(painterResource(R.drawable.vector_similar), null) },
        modifier = Modifier.clickable {
            onEvent(ContentDetailEvent.Media.ShowSimilar)
        }
    )
    ListItem(
        headlineContent = { Text(stringResource(text_statistics)) },
        leadingContent = { Icon(Icons.Outlined.Info, null) },
        modifier = Modifier.clickable {
            onEvent(ContentDetailEvent.Media.ShowStats)
        }
    )
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
        if (screenshot != pagerState.currentPage) pagerState.scrollToPage(screenshot)
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
fun CreateRate(id: String, type: LinkedType, rateF: UserRateF?, reload: () -> Unit, hide: () -> Unit) {
    val model = viewModel<UserRateViewModel>()
    val state by model.newRate.collectAsStateWithLifecycle()
    val exists by rememberSaveable { mutableStateOf(rateF != null) }

    LaunchedEffect(rateF) {
        rateF?.let { rate ->
            model.onEvent(SetRateId(rate.id))
            model.onEvent(SetStatus(Enum.safeValueOf<WatchStatus>(rate.status.rawValue), type))
            model.onEvent(SetScore(Score.entries.first { it.score == rate.score }))
            model.onEvent(SetChapters(rate.chapters.toString()))
            model.onEvent(SetEpisodes(rate.episodes.toString()))
            model.onEvent(SetVolumes(rate.volumes.toString()))
            model.onEvent(SetRewatches(rate.rewatches.toString()))
            model.onEvent(SetText(rate.text))
        }
    }

    AlertDialog(
        onDismissRequest = hide,
        confirmButton = {
            TextButton(
                content = { Text(stringResource(text_save)) },
                enabled = !state.status.isNullOrEmpty(),
                onClick = { if (exists) model.update(state.id, reload) else model.create(id, type, reload) }
            )
        },
        dismissButton = {
            if (exists) TextButton({ model.delete(state.id, reload) })
            { Text(stringResource(text_remove)) }
        },
        title = {
            Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
                Text(stringResource(if (exists) text_change else text_rate))
                IconButton(hide) { Icon(Icons.Outlined.Close, null) }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                RateStatus(model::onEvent, state.statusName, type)
                if (type == LinkedType.ANIME) {
                    RateEpisodes(model::onEvent, state.episodes)
                }
                if (type == LinkedType.MANGA) {
                    RateChapters(model::onEvent, state.chapters)
                    RateVolumes(model::onEvent, state.volumes)
                }
                RateScore(model::onEvent, state.score)
                RateRewatches(model::onEvent, state.rewatches, type)
                RateText(model::onEvent, state.text)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RateStatus(event: (RateEvent) -> Unit, @StringRes statusName: Int, type: LinkedType) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = stringResource(statusName),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(text_status)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            WatchStatus.entries.forEach {
                DropdownMenuItem(
                    onClick = { event(SetStatus(it, type)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    text = {
                        Text(
                            text = stringResource(type.getTitleResId(it)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateScore(event: (RateEvent) -> Unit, score: Score?) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = stringResource(score?.title ?: R.string.blank),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(text_score)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            Score.entries.forEach {
                DropdownMenuItem(
                    text = { Text(stringResource(it.title), style = MaterialTheme.typography.bodyLarge) },
                    onClick = { event(SetScore(it)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun RateEpisodes(event: (RateEvent) -> Unit, episodes: String?) = OutlinedTextField(
    value = episodes ?: BLANK,
    onValueChange = { event(SetEpisodes(it)) },
    label = { Text(stringResource(text_episodes)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateVolumes(event: (RateEvent) -> Unit, volumes: String?) = OutlinedTextField(
    value = volumes ?: BLANK,
    onValueChange = { event(SetVolumes(it)) },
    label = { Text(stringResource(text_volumes)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateChapters(event: (RateEvent) -> Unit, chapters: String?) = OutlinedTextField(
    value = chapters ?: BLANK,
    onValueChange = { event(SetChapters(it)) },
    label = { Text(stringResource(text_rate_chapters)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateRewatches(event: (RateEvent) -> Unit, count: String?, type: LinkedType) = OutlinedTextField(
    value = count ?: BLANK,
    onValueChange = { event(SetRewatches(it)) },
    label = { Text(stringResource(if (type == LinkedType.ANIME) text_rewatches else text_rereadings)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateText(event: (RateEvent) -> Unit, text: String?) = OutlinedTextField(
    value = text ?: BLANK,
    onValueChange = { event(SetText(it)) },
    label = { Text(stringResource(text_comment)) }
)

@Composable
fun Statuses(statistics: Statistics, @StringRes label: Int) {
    val textStyle = MaterialTheme.typography.labelLarge
    val minBarWidth = 40.dp.value.roundToInt()
    val gap = 8.dp.value.roundToInt()

    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()

    val maxStatusTextWidth = remember(statistics) {
        statistics.scores.keys.maxOf {
            textMeasurer.measure(AnnotatedString(it.asString(context)), textStyle).size.width
        }
    }

    ParagraphTitle(stringResource(label), Modifier.padding(bottom = 4.dp))
    Column(verticalArrangement = spacedBy(8.dp)) {
        statistics.scores.entries.forEach { (key, value) ->
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
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelLarge
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
                val idealWidth = (constraints.maxWidth * (value.toFloat() / statistics.sum)).roundToInt()
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
                statistics.first?.let { item { Statuses(it, text_user_rates) } }
                statistics.second?.let { item { Statuses(it, R.string.text_in_lists) } }
            }
        }
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

@Composable
fun UserStats(stats: Stats, id: Long, onNavigate: (Screen) -> Unit) {
    Column(Modifier.fillMaxWidth(), spacedBy(12.dp)) {
        if (stats.statuses.anime.sumOf(ShortInfo::size) > 0)
            Stats(id, stats.statuses.anime, LinkedType.ANIME, onNavigate)
        if (stats.statuses.manga.sumOf(ShortInfo::size) > 0) {
            HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))
            Stats(id, stats.statuses.manga, LinkedType.MANGA, onNavigate)
        }
    }
}

@Composable
fun Stats(
    id: Long,
    stats: List<ShortInfo>,
    type: LinkedType,
    onNavigate: (Screen) -> Unit
) {
    val sum = stats.sumOf { it.size }.takeIf { it != 0L } ?: 1

    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(
                text = stringResource(
                    if (type == LinkedType.ANIME) text_anime_list
                    else text_manga_list
                )
            )
            TextButton(
                onClick = { onNavigate(Screen.UserRates(id, type)) }
            )
            {
                Text(stringResource(text_show_all_s))
            }
        }
        stats.filter { it.size > 0 }.forEach { (_, _, name, size) ->
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(size.toFloat() / sum + 0.15f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = size.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = stringResource(getWatchStatus(name, type)),
                    modifier = Modifier.padding(end = 4.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
        }
    }
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
    history: LazyPagingItems<org.application.shikiapp.models.ui.History>,
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
    Column(Modifier.wrapContentHeight(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
        UserMenu.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(48.dp), Alignment.CenterVertically) {
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
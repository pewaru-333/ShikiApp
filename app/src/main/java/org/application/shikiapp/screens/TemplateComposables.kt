package org.application.shikiapp.screens

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import org.application.fragment.CharacterFragment
import org.application.fragment.PersonFragment
import org.application.fragment.RelatedFragment
import org.application.fragment.ScoresF
import org.application.fragment.StatsF
import org.application.fragment.UserRateF
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
import org.application.shikiapp.R.string.text_in_lists
import org.application.shikiapp.R.string.text_manga_list
import org.application.shikiapp.R.string.text_profile
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
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Favourites
import org.application.shikiapp.models.data.History
import org.application.shikiapp.models.data.ShortInfo
import org.application.shikiapp.models.data.Stats
import org.application.shikiapp.models.data.User
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.views.UserRateViewModel
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetChapters
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetEpisodes
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetRateId
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetRewatches
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetScore
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetStatus
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetText
import org.application.shikiapp.models.views.UserRateViewModel.RateEvent.SetVolumes
import org.application.shikiapp.models.views.UserState
import org.application.shikiapp.models.views.UserViewModel
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.utils.FAVOURITES_ITEMS
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ProfileMenus
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.SCORES
import org.application.shikiapp.utils.WATCH_STATUSES_A
import org.application.shikiapp.utils.WATCH_STATUSES_M
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getSeason
import org.application.shikiapp.utils.getWatchStatus

@Composable
fun LoadingScreen() = Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

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
fun Poster(link: String?) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    modifier = Modifier
        .size(175.dp, 300.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentDescription = null,
    contentScale = ContentScale.FillHeight,
    filterQuality = FilterQuality.High
)

@Composable
fun CircleImage(link: String?) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
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
fun UserBriefItem(user: User) = ListItem(
    headlineContent = { user.lastOnline?.let { Text(it, style = MaterialTheme.typography.bodyMedium) } },
    modifier = Modifier.offset((-16).dp, (-8).dp),
    overlineContent = { Text(user.nickname, style = MaterialTheme.typography.titleLarge) },
    leadingContent = {
        AsyncImage(
            model = user.image.x80,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.small)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
        )
    },
    supportingContent = {
        Text(
            text = fromHtml(user.commonInfo.joinToString(" / ")),
            style = MaterialTheme.typography.bodySmall
        )
    }
)

@Composable
fun CatalogListItem(
    title: String,
    kind: String?,
    season: Any?,
    image: String?,
    isBig: Boolean,
    click: () -> Unit
) = ListItem(
    modifier = Modifier.clickable(onClick = click),
    headlineContent = kind?.let { { Text(getKind(it)) } } ?: {},
    supportingContent = season?.let { { Text(getSeason(it, kind)) } } ?: { if (isBig) Text(BLANK) },
    overlineContent = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    },
    leadingContent = {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .size(121.dp, 170.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        )
    }
)

@Composable
fun CatalogGridItem(
    title: String,
    image: String?,
    click: () -> Unit
) = Column(Modifier.clickable(onClick = click)) {
    AsyncImage(
        model = image,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        filterQuality = FilterQuality.High,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
    )

    Text(
        minLines = 3,
        maxLines = 3,
        text = title,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

@Composable
fun RoundedPoster(link: String?, width: Dp = 140.dp) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    contentDescription = null,
    modifier = Modifier
        .width(width)
        .fillMaxHeight()
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentScale = ContentScale.FillBounds,
    filterQuality = FilterQuality.High
)

@Composable
fun RoundedRelatedPoster(link: String?, scale: ContentScale = ContentScale.FillHeight) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .height(187.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentScale = scale,
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
fun ShortDescription(title: String, kind: String?, season: Any?) = Column {
    Text(
        text = title,
        maxLines = 3,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
    Text(text = getKind(kind), style = MaterialTheme.typography.bodyLarge)
    Text(text = getSeason(season, kind), style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun Description(text: String?) {
    val full = fromHtml(text).text
    val hasSpoiler = full.contains("спойлер")
    val main = if (hasSpoiler) full.substringBefore("спойлер") else full
    val spoiler = if (hasSpoiler) full.substringAfter("спойлер") else BLANK

    var show by remember { mutableStateOf(false) }
    var lines by remember { mutableIntStateOf(8) }

    Column(Modifier.animateContentSize()) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle("Описание", Modifier.padding(bottom = 4.dp))
            IconButton(
                onClick = { lines = if(lines == 8) Int.MAX_VALUE else 8 }
            ) {
                Icon(
                    contentDescription = null,
                    imageVector = if (lines == 8) Icons.Outlined.KeyboardArrowDown
                    else Icons.Outlined.KeyboardArrowUp,
                )
            }
        }
        Text(
            text = main,
            maxLines = lines,
            overflow = TextOverflow.Ellipsis
        )
        if (hasSpoiler && lines != 8) Row(Modifier.clickable { show = !show }) {
            Text("Спойлер")
            Icon(if (show) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown, null)
        }
        if (show && lines != 8) ElevatedCard { Text(spoiler) }
        HorizontalDivider(Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ClubAnimeImage(link: String) {
    AsyncImage(
        model = getImage(link),
        contentDescription = null,
        modifier = Modifier
            .size(75.dp, 125.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, Color.Gray, MaterialTheme.shapes.medium),
        contentScale = ContentScale.FillHeight,
        filterQuality = FilterQuality.High
    )
}

@Composable
fun Comment(comment: Comment, toUser: (Long) -> Unit) {
    ListItem(
        headlineContent = { Text(comment.user.nickname) },
        modifier = Modifier.offset(x = (-8).dp),
        leadingContent = {
            AsyncImage(
                model = comment.user.image.x160,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable { toUser(comment.userId) },
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
            )
        },
        supportingContent = { Text(convertDate(comment.createdAt)) }
    )
    HtmlCommentBody(comment.htmlBody.trimIndent())
    HorizontalDivider(Modifier.padding(top = 8.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(hide: () -> Unit, list: LazyPagingItems<Comment>, toUser: (Long) -> Unit) =
    Dialog(hide, DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_comments)) },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = PaddingValues(8.dp, values.calculateTopPadding())) {
                when (list.loadState.refresh) {
                    is Error -> item { ErrorScreen(list::retry) }
                    Loading -> item { LoadingScreen() }
                    is NotLoading -> items(list.itemCount) { Comment(list[it]!!, toUser) }
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
            model = if (link?.contains("https") == true) link else getImage(link),
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
fun HistoryItem(note: History, toAnime: (String) -> Unit, toManga: (String) -> Unit) =
    ListItem(
        trailingContent = { Text(convertDate(note.createdAt)) },
        supportingContent = { Text(fromHtml(note.description)) },
        modifier = Modifier.clickable {
            note.target?.let {
                if (it.kind == LINKED_TYPE[1].lowercase()) toManga(it.id.toString())
                else toAnime(it.id.toString())
            }
        },
        headlineContent = {
            note.target?.let {
                Text(text = it.name, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        },
        leadingContent = {
            AsyncImage(
                model = note.target?.let { getImage(it.image.original) }
                    ?: R.drawable.vector_website,
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

fun LazyListScope.comments(comments: LazyPagingItems<Comment>, toUser: (Long) -> Unit) {
    item { ParagraphTitle(stringResource(text_comments), Modifier.offset(y = 8.dp)) }
    when (comments.loadState.refresh) {
        is Error -> item { ErrorScreen() }
        Loading -> item { LoadingScreen() }
        is NotLoading -> items(comments.itemCount) { Comment(comments[it]!!, toUser) }
    }
    if (comments.loadState.append == Loading) item { LoadingScreen() }
    if (comments.loadState.hasError) item { ErrorScreen(comments::retry) }
}

fun fromHtml(text: String?) = if (text != null) {
        AnnotatedString.Companion.fromHtml(
            htmlString = text,
            linkStyles = TextLinkStyles(
                SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    platformStyle = PlatformSpanStyle.Default
                )
            )
        )
    } else AnnotatedString(BLANK)

@Composable
fun Related(showFull: () -> Unit, list: List<RelatedFragment>, toAnime: (String) -> Unit, toManga: (String) -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_related), Modifier.padding(bottom = 4.dp))
            TextButton(showFull) { Text(stringResource(text_show_all_w)) }
        }
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            items(list.take(4)) { related->
                Column(
                    modifier = Modifier
                        .width(120.dp)
                        .clickable {
                            related.anime?.let { toAnime(it.id) }
                            related.manga?.let { toManga(it.id) }
                        }
                ) {
                    RoundedRelatedPoster(related.anime?.poster?.originalUrl ?: related.manga?.poster?.originalUrl)
                    RelatedText(related.anime?.russian ?: related.anime?.name ?: related.manga?.russian ?: related.manga!!.name)
                }
            }
        }
    }

@Composable
fun Characters(show: () -> Unit, roles: List<CharacterFragment>, toCharacter: (String) -> Unit) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_characters))
            IconButton(show) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        }
        LazyRow(
            horizontalArrangement = spacedBy(12.dp),
            verticalAlignment = CenterVertically
        ) {
            items(roles.filter { it.rolesRu.contains("Main") }) { role ->
                role.character.let {
                    Column(
                        modifier = Modifier.clickable { toCharacter(it.id) },
                        verticalArrangement = spacedBy(4.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        CircleImage(it.poster?.originalUrl)
                        TextCircleImage(it.russian ?: it.name)
                    }
                }
            }
        }
    }

@Composable
fun Authors(show: () -> Unit, roles: List<PersonFragment>, toPerson: (Long) -> Unit) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_authors))
            IconButton(show) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, null) }
        }
        LazyRow(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = CenterVertically
        ) {
            items(roles.filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }) { role ->
                role.person.let {
                    Column(
                        modifier = Modifier.clickable { toPerson(it.id.toLong()) },
                        verticalArrangement = spacedBy(4.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        CircleImage(it.poster?.originalUrl)
                        TextCircleImage(it.russian ?: it.name)
                    }
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogRelated(hide: () -> Unit, list: List<RelatedFragment>, toAnime: (String) -> Unit, toManga: (String) -> Unit) =
    Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
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
                                text = related.anime?.russian ?: related.anime?.name ?: related.manga?.russian ?: related.manga!!.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier.clickable {
                            related.anime?.let { toAnime(it.id) }
                            related.manga?.let { toManga(it.id) }
                        },
                        leadingContent = {
                            AsyncImage(
                                model = related.anime?.poster?.originalUrl ?: related.manga?.poster?.originalUrl,
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
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogCharacters(
    hide: () -> Unit,
    state: LazyListState,
    roles: List<CharacterFragment>,
    toCharacter: (String) -> Unit
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_characters)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding()), state = state) {
            items(roles) { role ->
                OneLineImage(
                    name = role.character.russian ?: role.character.name,
                    link = role.character.poster?.originalUrl,
                    modifier = Modifier.clickable { toCharacter(role.character.id) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogAuthors(
    hide: () -> Unit,
    state: LazyListState,
    roles: List<PersonFragment>,
    toPerson: (Long) -> Unit
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_authors)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        LazyColumn(contentPadding = PaddingValues(vertical = values.calculateTopPadding()), state = state) {
            items(roles) { role ->
                OneLineImage(
                    name = role.person.russian ?: role.person.name,
                    link = role.person.poster?.originalUrl,
                    modifier = Modifier.clickable { toPerson(role.person.id.toLong()) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    hideSheet: () -> Unit,
    showRate: () -> Unit,
    showSimilar: () -> Unit,
    showStats: () -> Unit,
    showLinks: () -> Unit,
    changeFavourite: (Boolean) -> Unit,
    state: SheetState,
    rate: UserRateF?,
    star: Boolean
) = ModalBottomSheet(hideSheet, sheetState = state) {
    if (Preferences.isTokenExists()) {
        ListItem(
            headlineContent = {
                Text(stringResource(rate?.let { text_change_rate } ?: text_add_rate))
            },
            modifier = Modifier.clickable(onClick = showRate),
            leadingContent = { Icon(Icons.Outlined.Edit, null) }
        )
        ListItem(
            headlineContent = { Text(stringResource(if (star) text_remove_fav else text_add_fav)) },
            modifier = Modifier.clickable { changeFavourite(star) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (star) Color.Red else LocalContentColor.current
                )
            }
        )
    }
    ListItem(
        headlineContent = { Text(stringResource(text_similar)) },
        modifier = Modifier.clickable(onClick = showSimilar),
        leadingContent = { Icon(painterResource(R.drawable.vector_similar), null) }
    )
    ListItem(
        headlineContent = { Text(stringResource(text_statistics)) },
        modifier = Modifier.clickable(onClick = showStats),
        leadingContent = { Icon(Icons.Outlined.Info, null) }
    )
    ListItem(
        headlineContent = { Text(stringResource(text_external_links)) },
        modifier = Modifier.clickable(onClick = showLinks),
        leadingContent = { Icon(Icons.AutoMirrored.Outlined.List, null) }
    )
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
fun CreateRate(hide: () -> Unit, reload: () -> Unit, type: String, id: String, rateF: UserRateF?) {
    val model = viewModel<UserRateViewModel>()
    val state by model.newRate.collectAsStateWithLifecycle()
    val exists by rememberSaveable { mutableStateOf(rateF != null) }

    rateF?.let { rate ->
        model.onEvent(SetRateId(rate.id))
        model.onEvent(SetStatus(WATCH_STATUSES_A.entries.first { it.key == rate.status.rawValue }))
        model.onEvent(SetScore(SCORES.entries.first { it.key == rate.score }))
        model.onEvent(SetChapters(rate.chapters.toString()))
        model.onEvent(SetEpisodes(rate.episodes.toString()))
        model.onEvent(SetVolumes(rate.volumes.toString()))
        model.onEvent(SetRewatches(rate.rewatches.toString()))
        model.onEvent(SetText(rate.text))
    }

    AlertDialog(
        onDismissRequest = hide,
        confirmButton = {
            TextButton(
                content = { Text(stringResource(text_save)) },
                enabled = !state.status.isNullOrEmpty(),
                onClick = { if (exists) model.update(state.id) else model.create(id, type); reload() }
            )
        },
        dismissButton = {
            if (exists) TextButton({ model.delete(state.id); reload() })
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
                if (type == LINKED_TYPE[0]) RateEpisodes(model::onEvent, state.episodes)
                if (type == LINKED_TYPE[1]) {
                    RateChapters(model::onEvent, state.chapters)
                    RateVolumes(model::onEvent, state.volumes)
                }
                RateScore(model::onEvent, state.scoreName)
                RateRewatches(model::onEvent, state.rewatches, type)
                RateText(model::onEvent, state.text)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RateStatus(event: (RateEvent) -> Unit, statusName: String, type: String) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = statusName,
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
            (if (type == LINKED_TYPE[0]) WATCH_STATUSES_A else WATCH_STATUSES_M).entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.value, style = MaterialTheme.typography.bodyLarge) },
                    onClick = { event(SetStatus(it)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateScore(event: (RateEvent) -> Unit, scoreName: String?) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = scoreName ?: BLANK,
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
            SCORES.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.value, style = MaterialTheme.typography.bodyLarge) },
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
fun RateRewatches(event: (RateEvent) -> Unit, count: String?, type: String) = OutlinedTextField(
    value = count ?: BLANK,
    onValueChange = { event(SetRewatches(it)) },
    label = { Text(stringResource(if (type == LINKED_TYPE[0]) text_rewatches else text_rereadings)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateText(event: (RateEvent) -> Unit, text: String?) = OutlinedTextField(
    value = text ?: BLANK,
    onValueChange = { event(SetText(it)) },
    label = { Text(stringResource(text_comment)) }
)

@Composable
fun Scores(scores: List<ScoresF>, sum: Int = scores.sumOf { it.count }) =
    Column(Modifier.fillMaxWidth(), spacedBy(4.dp)) {
        ParagraphTitle(stringResource(text_user_rates))
        Column(verticalArrangement = spacedBy(8.dp)) {
            scores.forEach {
                Row(Modifier.fillMaxWidth(), SpaceBetween) {
                    Column(Modifier.fillMaxWidth(0.625f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(it.count.toFloat() / sum + 0.15f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = it.count.toString(),
                                modifier = Modifier.padding(end = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                    Text(
                        text = it.score.toString(),
                        modifier = Modifier.padding(start = 8.dp),
                        overflow = TextOverflow.Visible,
                        maxLines = 1
                    )
                }
            }
        }
    }

@Composable
fun Statuses(statuses: List<StatsF>, type: String) {
    val sum = statuses.sumOf { it.count }

    ParagraphTitle(stringResource(text_in_lists), Modifier.padding(bottom = 4.dp))
    Column(verticalArrangement = spacedBy(8.dp)) {
        statuses.forEach {
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(it.count.toFloat() / sum + 0.169f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = it.count.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = getWatchStatus(it.status.rawValue, type),
                    modifier = Modifier.padding(start = 8.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(hide: () -> Unit, scores: List<ScoresF>?, stats: List<StatsF>?, type: String) =
    Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
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
                scores?.let { item { Scores(it) } }
                stats?.let { item { Statuses(it, type) } }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LinksSheet(
    hide: () -> Unit,
    state: SheetState,
    list: List<ExternalLink>,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(hide, sheetState = state) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        horizontalArrangement = SpaceBetween,
        verticalArrangement = spacedBy(8.dp)
    ) {
        list.forEach { link ->
            EXTERNAL_LINK_KINDS.entries.firstOrNull { it.key == link.kind }?.let { (_, value) ->
                ElevatedCard(
                    onClick = { handler.openUri(link.url) },
                    modifier = Modifier.size(100.dp),
                    colors = CardDefaults.elevatedCardColors().copy(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    AsyncImage(
                        model = "https://www.google.com/s2/favicons?domain=${Uri.parse(link.url).host}&sz=64",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .size(64.dp),
                        alignment = Alignment.Center,
                        filterQuality = FilterQuality.High
                    )
                    Text(
                        text = value,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
fun UserStats(stats: Stats, id: Long, toAnime: (String) -> Unit, toManga: (String) -> Unit) {
    Column(Modifier.fillMaxWidth(), spacedBy(12.dp)) {
        if (stats.statuses.anime.sumOf { it.size } > 0)
            Stats(id, stats.statuses.anime, LINKED_TYPE[0], toAnime, toManga)
        if (stats.statuses.manga.sumOf { it.size } > 0) {
            HorizontalDivider(Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp))
            Stats(id, stats.statuses.manga, LINKED_TYPE[1], toAnime, toManga)
        }
    }
}

@Composable
fun Stats(
    id: Long,
    stats: List<ShortInfo>,
    type: String,
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit
) {
    val sum = stats.sumOf { it.size }.takeIf { it != 0L } ?: 1

    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(if (type == LINKED_TYPE[0]) text_anime_list else text_manga_list))
            TextButton({ if (type == LINKED_TYPE[0]) toAnime(id.toString()) else toManga(id.toString()) })
            { Text(stringResource(text_show_all_s)) }
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
                    text = getWatchStatus(name, type),
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
    setTab: (Int) -> Unit,
    tab: Int,
    favourites: Favourites,
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson:(Long) -> Unit
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_favourite)) },
                navigationIcon = { NavigationIcon(hide) }
            )
        }
    ) { values ->
        Column(Modifier.padding(top = values.calculateTopPadding()), spacedBy(8.dp)) {
            ScrollableTabRow(tab, edgePadding = 8.dp) {
                FAVOURITES_ITEMS.forEachIndexed { index, title ->
                    Tab(tab == index, { setTab(index) }) {
                        Text(stringResource(title), Modifier.padding(8.dp, 12.dp))
                    }
                }
            }
            LazyColumn {
                items(
                    when (tab) {
                        0 -> favourites.animes
                        1 -> favourites.mangas
                        2 -> favourites.ranobe
                        3 -> favourites.characters
                        4 -> favourites.people
                        5 -> favourites.mangakas
                        6 -> favourites.seyu
                        else -> favourites.producers
                    }
                ) { (id, name, russian, image) ->
                    OneLineImage(
                        name = russian.orEmpty().ifEmpty { name },
                        link = image,
                        modifier = Modifier.clickable {
                            when (tab) {
                                0 -> toAnime(id.toString())
                                1, 2 -> toManga(id.toString())
                                3 -> toCharacter(id.toString())
                                else -> toPerson(id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogHistory(hide: () -> Unit, history: LazyPagingItems<History>, toAnime: (String) -> Unit, toManga: (String) -> Unit) =
    Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_history)) },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding())) {
                items(history.itemCount) { index ->
                    history[index]?.let { HistoryItem(it, toAnime, toManga) }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    hideSheet: () -> Unit,
    showFavourite: () -> Unit,
    showHistory: () -> Unit,
    state: SheetState
) = ModalBottomSheet(hideSheet, sheetState = state) {
    ListItem(
        headlineContent = { Text(stringResource(text_favourite)) },
        modifier = Modifier.clickable(onClick = showFavourite),
        leadingContent = { Icon(Icons.Outlined.FavoriteBorder, null) }
    )
    ListItem(
        headlineContent = { Text(stringResource(text_history)) },
        modifier = Modifier.clickable(onClick = showHistory),
        leadingContent = { Icon(Icons.AutoMirrored.Outlined.List, null) }
    )
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
fun BriefInfo(setMenu: (Int) -> Unit) {
    ParagraphTitle(stringResource(text_profile), Modifier.padding(bottom = 4.dp))
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
        ProfileMenus.entries.forEach { entry ->
            ElevatedCard(
                onClick = { setMenu(entry.ordinal) },
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f),
                enabled = entry.ordinal != 2
            ) {
                Row(Modifier.fillMaxSize(), Center, CenterVertically) {
                    Text(stringResource(entry.title), style = MaterialTheme.typography.titleSmall)
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogItem(
    model: UserViewModel,
    state: UserState,
    friends: LazyPagingItems<UserBasic>,
    clubs: List<ClubBasic>,
    toUser: (Long) -> Unit,
    toClub: (Long) -> Unit
) = Dialog(model::close, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(model.getTitle())) },
                navigationIcon = { NavigationIcon(model::close) }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(top = values.calculateTopPadding()),
            state = when (state.menu) {
                0 -> state.stateF
                1 -> state.stateC
                else -> rememberLazyListState()
            }
        ) {
            when (state.menu) {
                0 -> friends(friends, toUser)
                1 -> clubs(clubs, toClub)
            }
        }
    }
}

// ========================================== Extensions ===========================================

fun LazyListScope.friends(list: LazyPagingItems<UserBasic>, toUser: (Long) -> Unit) {
    when (list.loadState.refresh) {
        is Error -> item { ErrorScreen(list::retry) }
        is Loading -> item { LoadingScreen() }
        is NotLoading -> {
            items(list.itemCount) { index ->
                list[index]?.let {
                    OneLineImage(
                        name = it.nickname,
                        link = it.image.x160,
                        modifier = Modifier.clickable { toUser(it.id) }
                    )
                }
            }
            if (list.loadState.append == Loading) item { LoadingScreen() }
            if (list.loadState.hasError) item { ErrorScreen(list::retry) }
        }
    }
}

fun LazyListScope.clubs(list: List<ClubBasic>, toClub: (Long) -> Unit) = if (list.isEmpty()) item {
    Box(Modifier.fillMaxSize(), Alignment.Center) { Text(stringResource(text_empty)) }
}
else items(list) {
    OneLineImage(
        name = it.name,
        link = getImage(it.logo.original),
        modifier = Modifier.clickable { toClub(it.id) }
    )
}
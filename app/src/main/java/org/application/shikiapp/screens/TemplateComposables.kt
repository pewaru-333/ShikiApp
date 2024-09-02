package org.application.shikiapp.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.HistoryAnime
import org.application.shikiapp.models.data.User
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getSeason

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
    headlineContent = { Text(user.lastOnline, style = MaterialTheme.typography.bodyMedium) },
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
fun RoundedAnimePoster(link: String?, width: Dp = 140.dp) = AsyncImage(
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
fun RoundedRelatedPoster(link: String?) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .height(187.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentScale = ContentScale.FillHeight,
    filterQuality = FilterQuality.High,
)

@Composable
fun Names(names: List<String?>) {
    Column(Modifier.padding(horizontal = 8.dp), Arrangement.spacedBy(8.dp)) {
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
fun AnimeShortDescription(title: String, kind: String?, season: String?) =
    Column(verticalArrangement = SpaceBetween) {
        Text(
            text = title,
            maxLines = 3,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(text = getKind(kind), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = getSeason(season),
            style = MaterialTheme.typography.bodyLarge
        )
    }

@Composable
fun Description(text: String?) {
    val full = fromHtml(text).text
    val hasSpoiler = full.contains("спойлер")
    val main = if (hasSpoiler) full.substringBefore("спойлер") else full
    val spoiler = if (hasSpoiler) full.substringAfter("спойлер") else BLANK

    var show by remember { mutableStateOf(false) }

    Column(Modifier.animateContentSize()) {
        ParagraphTitle("Описание", Modifier.padding(bottom = 4.dp))
        Text(main)
        if (hasSpoiler) Row(Modifier.clickable { show = !show }) {
            Text("Спойлер")
            Icon(if (show) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
        }
        if (show) ElevatedCard { Text(spoiler) }
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
fun Comment(comment: Comment, navigator: DestinationsNavigator) {
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
                    .clickable { navigator.navigate(UserScreenDestination(comment.userId)) },
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
fun Comments(hide: () -> Unit, list: LazyPagingItems<Comment>, navigator: DestinationsNavigator) =
    Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.text_comments)) },
                    navigationIcon = { NavigationIcon(hide) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = PaddingValues(8.dp, values.calculateTopPadding())) {
                when (list.loadState.refresh) {
                    is Error -> item { ErrorScreen(list::retry) }
                    Loading -> item { LoadingScreen() }
                    is NotLoading -> items(list.itemCount) { Comment(list[it]!!, navigator) }
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
fun HistoryItem(note: HistoryAnime, navigator: DestinationsNavigator) = ListItem(
    trailingContent = { Text(convertDate(note.createdAt)) },
    supportingContent = { Text(fromHtml(note.description)) },
    modifier = Modifier.clickable {
        navigator.navigate(AnimeScreenDestination(note.target.id.toString()))
    },
    headlineContent = {
        Text(text = note.target.name, maxLines = 3, overflow = TextOverflow.Ellipsis)
    },
    leadingContent = {
        AsyncImage(
            model = getImage(note.target.image.original),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp, 121.dp)
                .clip(MaterialTheme.shapes.medium)
                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        )
    }
)

@Composable
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }

fun LazyListScope.comments(comments: LazyPagingItems<Comment>, navigator: DestinationsNavigator) {
    item { ParagraphTitle(stringResource(R.string.text_comments), Modifier.offset(y = 8.dp)) }
    when (comments.loadState.refresh) {
        is Error -> item { ErrorScreen() }
        Loading -> item { LoadingScreen() }
        is NotLoading -> items(comments.itemCount) { Comment(comments[it]!!, navigator) }
    }
    if (comments.loadState.append == Loading) item { LoadingScreen() }
    if (comments.loadState.hasError) item { ErrorScreen(comments::retry) }
}

fun fromHtml(text: String?): AnnotatedString = if (text != null)
    AnnotatedString.Companion.fromHtml(
        htmlString = text,
        linkStyles = TextLinkStyles(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                platformStyle = PlatformSpanStyle.Default
            )
        )
    ) else AnnotatedString(BLANK)

@Composable
fun RoundedPersonImage(poster: String?) = AsyncImage(
    model = poster,
    contentDescription = null,
    modifier = Modifier
        .size(127.dp, 180.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentScale = ContentScale.FillBounds,
    filterQuality = FilterQuality.High
)
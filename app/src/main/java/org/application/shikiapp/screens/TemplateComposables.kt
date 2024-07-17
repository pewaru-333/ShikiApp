package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.generated.destinations.UserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.getImage

@Composable
fun LoadingScreen() = Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

@Composable
fun ErrorScreen(onClick: Unit = Unit) = Column(Modifier.fillMaxSize(), Center, CenterHorizontally) {
    Text("Ошибка загрузки!")
    Button({ onClick.run {} }) { Text("Повторить") }
}

@Composable
fun ParagraphTitle(text: String, modifier: Modifier = Modifier) = Text(
    text = text,
    modifier = modifier,
    color = MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
)

@Composable
fun Poster(link: String?) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    modifier = Modifier
        .width(175.dp)
        .height(300.dp)
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
    contentDescription = null,
    contentScale = ContentScale.FillHeight,
    filterQuality = FilterQuality.High
)

@Composable
fun CircleImage(link: String?, size: Dp = 96.dp) = AsyncImage(
    model = if (link?.contains("https") == true) link else getImage(link),
    contentDescription = null,
    modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .border((0.4).dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
    error = painterResource(R.drawable.vector_home),
    fallback = painterResource(R.drawable.vector_home),
    alignment = Alignment.TopCenter,
    contentScale = ContentScale.Crop,
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
fun NameCircleImage(text: String, size: Dp = 96.dp) {
    Text(
        text = text,
        modifier = Modifier.width(size),
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        minLines = 2,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun Description(text: String?) {
    val full = fromHtml(text).text
    val hasSpoiler = full.contains("спойлер")
    val main = if (hasSpoiler) full.substringBefore("спойлер") else full
    val spoiler = if (hasSpoiler) full.substringAfter("спойлер") else BLANK

    var show by remember { mutableStateOf(false) }

    Column {
        ParagraphTitle("Описание")
        Text(main, Modifier.padding(top = 8.dp))
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
                error = painterResource(R.drawable.vector_home),
                fallback = painterResource(R.drawable.vector_home),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
            )
        },
        supportingContent = { Text(convertDate(comment.createdAt)) }
    )
    HtmlCommentBody(comment.htmlBody.trimIndent())
    HorizontalDivider(Modifier.padding(top = 8.dp))
}

@Composable
fun SmallItem(name: String, link: String?, modifier: Modifier = Modifier) {
    ListItem(
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
}

@Composable
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }

fun LazyListScope.comments(comments: LazyPagingItems<Comment>, navigator: DestinationsNavigator) {
    item { ParagraphTitle(stringResource(R.string.text_comments), Modifier.offset(y = 8.dp)) }
    when (comments.loadState.refresh) {
        is LoadState.Error -> item { ErrorScreen() }
        is LoadState.Loading -> item { LoadingScreen() }
        is LoadState.NotLoading -> items(comments.itemCount) { Comment(comments[it]!!, navigator) }
    }
    if (comments.loadState.append == LoadState.Loading) item { LoadingScreen() }
    if (comments.loadState.hasError) item { ErrorScreen(comments.retry()) }
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
package org.application.shikiapp.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.application.shikiapp.utils.ResourceText
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.enums.backgroundColor
import org.application.shikiapp.utils.enums.textColor
import org.application.shikiapp.utils.navigation.Screen

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
                Row(Modifier.padding(6.dp, 4.dp), Arrangement.spacedBy(4.dp), Alignment.CenterVertically) {
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

    Column(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) {
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
                Row(Modifier.padding(6.dp, 4.dp), Arrangement.spacedBy(4.dp), Alignment.CenterVertically) {
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
        verticalArrangement = Arrangement.Center,
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
fun BasicContentItem(name: String, link: String?, modifier: Modifier = Modifier) =
    ListItem(
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
fun CalendarOngoingCard(title: String, score: String?, poster: String, onNavigate: () -> Unit) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onNavigate)
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = poster,
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
                    Row(Modifier.padding(6.dp, 4.dp), Arrangement.spacedBy(4.dp), Alignment.CenterVertically) {
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

@Composable
fun RelatedCard(title: String, poster: String, relationText: String, onClick: () -> Unit) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
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
    verticalAlignment = Alignment.CenterVertically,
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
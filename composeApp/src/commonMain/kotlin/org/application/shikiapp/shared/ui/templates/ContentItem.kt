package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.enums.backgroundColor
import org.application.shikiapp.shared.utils.enums.textColor
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_star


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
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(8.dp)
) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
    ) {
        AnimatedAsyncImage(
            model = image,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        score?.let { ScoreLabel(it) }
    }

    Spacer(Modifier.width(16.dp))

    Column(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            text = buildString {
                append(stringResource(kind.title))
                season.asComposableString().let { if (it.isNotEmpty()) append(" · $it") }
                relationText?.let { append(" · $it") }
            }
        )

        Surface(
            shape = MaterialTheme.shapes.small,
            color = status.backgroundColor,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = stringResource(status.getTitle(kind)),
                modifier = Modifier.padding(10.dp, 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = status.textColor,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
    }
}

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
    AnimatedAsyncImage(
        model = image,
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
    shape = MaterialTheme.shapes.medium,
    elevation = CardDefaults.cardElevation(2.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ),
    modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        ) {
            AnimatedAsyncImage(
                model = image,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            score?.let { ScoreLabel(it) }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            )

            Spacer(Modifier.height(4.dp))


            val kindSeason = buildList {
                kind?.let { add(stringResource(it.title)) }
                season?.let { add(it) }
            }.joinToString(" • ")

            if (kindSeason.isNotEmpty()) {
                Text(
                    text = kindSeason,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            } else {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UserGridItem(title: String, imageUrl: String?, onClick: () -> Unit) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.small)
        .clickable(onClick = onClick)
        .padding(8.dp)
) {
    AnimatedAsyncImage(
        model = imageUrl,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
            .clip(CircleShape)
    )

    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun BasicContentItem(name: String, link: String?, modifier: Modifier = Modifier, roles: String? = null) =
    ListItem(
        modifier = modifier,
        headlineContent = { Text(name) },
        supportingContent = { roles?.let { Text(it) } },
        leadingContent = {
            AnimatedAsyncImage(
                model = link,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
        }
    )

@Composable
fun CalendarOngoingCard(title: String, score: String?, poster: String, onNavigate: () -> Unit) {
    val isCompact = rememberWindowSize().isCompact
    val cardWidth = if (isCompact) 120.dp else 160.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(cardWidth)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onNavigate)
    ) {
        Box(
            modifier = Modifier
                .width(cardWidth)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
        ) {
            AnimatedAsyncImage(
                model = poster,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            score?.let { ScoreLabel(it) }
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
}

@Composable
fun RelatedCard(title: String, poster: String, relationText: String, interactionSource: MutableInteractionSource, onClick: () -> Unit) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .height(170.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AnimatedAsyncImage(
                model = poster,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .indication(interactionSource, ripple())
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

            Text(
                text = relationText.uppercase().replace(" ", "\n"),
                maxLines = relationText.split(" ").size.coerceAtMost(3),
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
    AnimatedAsyncImage(
        model = poster,
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
fun BoxScope.ScoreLabel(score: String) =
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        tonalElevation = 4.dp,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
    ) {
        Row(Modifier.padding(6.dp, 4.dp), Arrangement.spacedBy(4.dp), Alignment.CenterVertically) {
            VectorIcon(
                resId = Res.drawable.vector_star,
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
package org.application.shikiapp.shared.ui.templates


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.enums.colors
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_star

data class MediaGridItemTitleConfig(
    val style: TextStyle,
    val textAlign: TextAlign,
    val maxLines: Int,
    val minLines: Int
)

data class MediaGridItemContainerConfig(
    val color: Color,
    val elevation: Dp
)

object MediaGridItemDefaults {

    @Composable
    fun titleConfig(
        minLines: Int = 1,
        maxLines: Int = 2,
        textAlign: TextAlign = TextAlign.Start,
        style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    ) = MediaGridItemTitleConfig(
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        minLines = minLines
    )

    @Composable
    fun containerConfig(color: Color = Color.Transparent, elevation: Dp = 0.dp) =
        MediaGridItemContainerConfig(
            color = color,
            elevation = elevation
        )
}

@Composable
fun CircleContentItem(
    title: String,
    poster: String?,
    titleConfig: MediaGridItemTitleConfig = MediaGridItemDefaults.titleConfig(),
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        CircleContentImage(poster, Modifier.size(72.dp))

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = titleConfig.textAlign,
            overflow = TextOverflow.Ellipsis,
            minLines = titleConfig.minLines,
            maxLines = titleConfig.maxLines,
            style = titleConfig.style
        )
    }
}

@Composable
fun ListContentItem(name: String, image: String?, roles: String? = null, onClick: () -> Unit) =
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(name) },
        leadingContent = { CircleContentImage(image, Modifier.size(64.dp)) },
        supportingContent = { roles?.let { Text(it) } }
    )

@Composable
fun RelatedCard(title: String, poster: String, relationText: String, onClick: () -> Unit) =
    MediaGridItem(
        title = title,
        poster = poster,
        modifier = Modifier.width(120.dp),
        posterModifier = Modifier.height(170.dp),
        onClick = onClick,
        titleConfig = MediaGridItemDefaults.titleConfig(
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
        ),
        imageOverlay = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            val (text, maxLines) = remember(relationText) {
                val words = relationText.split(' ')
                words.joinToString("\n", transform = String::uppercase) to words.size.coerceAtMost(3)
            }

            Text(
                text = text,
                maxLines = maxLines,
                autoSize = TextAutoSize.StepBased(8.sp, 11.sp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    )

@Composable
fun MediaListItem(
    title: String,
    poster: String?,
    score: String? = null,
    role: String? = null,
    description: AnnotatedString? = null,
    kind: Kind? = null,
    season: String? = null,
    status: Status? = null,
    date: String? = null,
    actions: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit
) {
    @Composable
    fun LocalChip(
        text: String,
        textColor: Color,
        chipColor: Color,
        border: BorderStroke? = null
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = chipColor,
            border = border
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp, 4.dp),
                style = MaterialTheme.typography.labelMedium.copy(color = textColor)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(8.dp)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .border(Dp.Hairline, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.shapes.medium)
        ) {
            AnimatedAsyncImage(
                model = poster,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            score?.let { ScoreLabel(it) }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (description != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val kindText = kind?.let { stringResource(it.title) }
                val metaText = buildList {
                    kindText?.let { add(it) }
                    season?.takeIf(String::isNotBlank)?.let { add(it) }
                }.joinToString(" • ")

                if (metaText.isNotEmpty()) {
                    LocalChip(
                        text = metaText,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        chipColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                if (status != null && kind != null) {
                    LocalChip(
                        text = stringResource(status.getTitle(kind)),
                        textColor = status.colors.text,
                        chipColor = status.colors.background
                    )
                }

                if (role != null) {
                    LocalChip(
                        text = role,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        chipColor = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            if (date != null) {
                Spacer(Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    LocalChip(
                        text = date,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        chipColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            if (actions != null) {
                Spacer(Modifier.weight(1f))
                actions()
            }
        }
    }
}

@Composable
fun MediaGridItem(
    title: String,
    poster: String?,
    score: String? = null,
    modifier: Modifier = Modifier,
    posterModifier: Modifier = Modifier.aspectRatio(2f / 3f),
    titleConfig: MediaGridItemTitleConfig = MediaGridItemDefaults.titleConfig(),
    containerConfig: MediaGridItemContainerConfig = MediaGridItemDefaults.containerConfig(),
    imageOverlay: @Composable (BoxScope.() -> Unit)? = null,
    subtitleContent: @Composable (ColumnScope.() -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = containerConfig.color,
        shadowElevation = containerConfig.elevation
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(posterModifier)
                    .clip(MaterialTheme.shapes.medium)
                    .border(Dp.Hairline, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.shapes.medium)
            ) {
                AnimatedAsyncImage(
                    model = poster,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                score?.let { ScoreLabel(it) }

                imageOverlay?.invoke(this)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp, 8.dp)
            ) {
                Text(
                    text = title,
                    textAlign = titleConfig.textAlign,
                    maxLines = titleConfig.maxLines,
                    minLines = titleConfig.minLines,
                    overflow = TextOverflow.Ellipsis,
                    style = titleConfig.style,
                    modifier = Modifier.fillMaxWidth()
                )

                subtitleContent?.invoke(this)
            }
        }
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
package org.application.shikiapp.utils

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.application.shikiapp.R
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.extensions.clickableUrl

@Composable
fun HtmlComment(commentContent: List<CommentContent>?) {
    var image by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints {
        val containerMaxWidth = this.maxWidth

        Column {
            commentContent?.forEach { item ->
                RenderContent(
                    content = item,
                    containerMaxWidth = containerMaxWidth,
                    onImageClick = { image = it }
                )
            }
        }
    }

    if (image != null) {
        Dialog(
            onDismissRequest = { image = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ZoomableAsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun RenderContent(
    content: CommentContent,
    containerMaxWidth: Dp,
    onImageClick: (String) -> Unit
) {
    when (content) {
        is CommentContent.TextContent -> {
            val context = LocalContext.current
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Text(
                text = content.text,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = content.inlineContent,
                onTextLayout = { layoutResult = it },
                modifier = Modifier.clickableUrl(context, content.text) { layoutResult }
            )
        }

        is CommentContent.ImageContent -> {
            val density = LocalDensity.current

            val maxHeight = 180.dp

            val originalWidth = with(density) { content.width.toDp() }
            val aspectRatio = if (content.height > 0) content.width / content.height else 1f

            var finalWidth = min(originalWidth, containerMaxWidth)
            var finalHeight = finalWidth / aspectRatio

            if (finalHeight > maxHeight) {
                finalHeight = maxHeight
                finalWidth = finalHeight * aspectRatio
            }

            AsyncImage(
                model = content.previewUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(finalWidth, finalHeight)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(content.fullUrl ?: content.previewUrl) }
            )
        }

        is CommentContent.VideoContent -> {
            val context = LocalContext.current

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, content.videoUrl.toUri())
                        context.startActivity(intent)
                    }
            ) {
                AsyncImage(
                    model = content.previewUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )

                Surface(Modifier.size(56.dp), CircleShape, Color.Black.copy(alpha = 0.6f)) {
                    VectorIcon(
                        resId = R.drawable.vector_refresh,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = content.source.uppercase(),
                        modifier = Modifier.padding(6.dp, 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                        )
                    )
                }
            }
        }

        is CommentContent.SpoilerContent -> {
            var isVisible by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { isVisible = !isVisible }
                    .padding(12.dp, 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VectorIcon(
                        modifier = Modifier.size(20.dp),
                        resId = if (isVisible) R.drawable.vector_keyboard_arrow_down
                        else R.drawable.vector_keyboard_arrow_right,
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AnimatedVisibility(isVisible) {
                    Column(Modifier.padding(top = 8.dp)) {
                        content.items.forEach {
                            RenderContent(
                                content = it,
                                containerMaxWidth = containerMaxWidth,
                                onImageClick = onImageClick
                            )
                        }
                    }
                }
            }
        }

        is CommentContent.QuoteContent -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
                    .clip(RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )

                Column(Modifier.padding(16.dp, 8.dp, 8.dp, 8.dp)) {
                    Text(
                        text = content.author,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    content.items.forEach {
                        RenderContent(it, containerMaxWidth, onImageClick)
                    }
                }
            }
        }

        is CommentContent.BanContent -> {
            val context = LocalContext.current
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = content.moderatorAvatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.moderatorName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = ": ",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = content.reason,
                    modifier = Modifier.clickableUrl(context, content.reason) { layoutResult },
                    onTextLayout = { layoutResult = it },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }

        is CommentContent.LineBreakContent -> Spacer(Modifier.height(8.dp))
    }
}
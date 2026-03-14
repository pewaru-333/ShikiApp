@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import org.application.shikiapp.shared.utils.extensions.clickableUrl
import org.application.shikiapp.shared.utils.extensions.flattenImages
import org.application.shikiapp.shared.utils.ui.CommentContent
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_refresh

@Composable
fun HtmlContent(commentContent: List<CommentContent>?) {
    var galleryInfo by remember { mutableStateOf<Pair<List<CommentContent.ImageContent>, Int>?>(null) }

    BoxWithConstraints {
        val containerMaxWidth = this.maxWidth

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            commentContent?.forEach { item ->
                RenderContent(
                    content = item,
                    containerMaxWidth = containerMaxWidth,
                    onImageClick = {
                        val images = commentContent.flattenImages()
                        val index = images.indexOf(it)
                        galleryInfo = images to index
                    }
                )
            }
        }
    }

    galleryInfo?.let { (imageContents, initialIndex) ->
        val imageUrls = imageContents.map { it.fullUrl ?: it.previewUrl }

        DialogImages(
            images = imageUrls,
            initialIndex = initialIndex,
            isVisible = true,
            onClose = { galleryInfo = null }
        )
    }
}

@Composable
private fun RenderContent(
    content: CommentContent,
    containerMaxWidth: Dp,
    onImageClick: (CommentContent.ImageContent) -> Unit
) {
    when (content) {
        is CommentContent.TextContent -> {
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Text(
                text = content.text,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = content.inlineContent,
                onTextLayout = { layoutResult = it },
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickableUrl(content.text) { layoutResult }
            )
        }

        is CommentContent.ImageContent -> {
            val density = LocalDensity.current
            val maxHeight = 240.dp

            val knownSize = content.width != null && content.height != null && content.height > 0f
            val modifier = if (knownSize) {
                val originalWidth = with(density) { content.width.toDp() }
                val aspectRatio = content.width / content.height

                var finalWidth = min(originalWidth, containerMaxWidth)
                var finalHeight = finalWidth / aspectRatio

                if (finalHeight > maxHeight) {
                    finalHeight = maxHeight
                    finalWidth = finalHeight * aspectRatio
                }

                Modifier.size(finalWidth, finalHeight)
            } else {
                Modifier
                    .widthIn(max = containerMaxWidth / 2)
                    .heightIn(120.dp, maxHeight)
                    .wrapContentHeight()
            }

            AnimatedAsyncImage(
                model = content.previewUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .then(modifier)
                    .clickable { onImageClick(content) }
            )
        }

        is CommentContent.VideoContent -> {
            val uriHandler = LocalUriHandler.current

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                    .clickable { uriHandler.openUri(content.videoUrl) }
            ) {
                AnimatedAsyncImage(
                    model = content.previewUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )

                Surface(Modifier.size(56.dp), CircleShape, Color.Black.copy(alpha = 0.6f)) {
                    VectorIcon(
                        resId = Res.drawable.vector_refresh,
                        tint = Color.White,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = content.source.uppercase(),
                        modifier = Modifier.padding(8.dp, 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                    )
                }
            }
        }

        is CommentContent.SpoilerContent -> {
            var isVisible by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(targetValue = if (isVisible) 90f else 0f)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { isVisible = !isVisible }
                    .padding(12.dp, 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VectorIcon(
                        resId = Res.drawable.vector_keyboard_arrow_right,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                AnimatedVisibility(isVisible) {
                    Column(Modifier.padding(start = 8.dp, top = 12.dp)) {
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
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                Text(
                    text = content.author,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.height(4.dp))

                content.items.forEach {
                    RenderContent(it, containerMaxWidth, onImageClick)
                }
            }
        }

        is CommentContent.BanContent -> {
            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedAsyncImage(
                        model = content.moderatorAvatar,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "${content.moderatorName}:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = content.reason,
                    modifier = Modifier.clickableUrl(content.reason) { layoutResult },
                    onTextLayout = { layoutResult = it },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                )
            }
        }

        is CommentContent.LineBreakContent -> Spacer(Modifier.height(12.dp))
    }
}

fun LazyListScope.htmlContent(
    contentList: List<CommentContent>,
    onImageClick: (List<CommentContent.ImageContent>, Int) -> Unit
) {
    if (contentList.isEmpty()) return

    val images = contentList.flattenImages()

    items(contentList) { item ->
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            RenderContent(
                content = item,
                containerMaxWidth = maxWidth,
                onImageClick = { clickedImage ->
                    val index = images.indexOf(clickedImage)
                    onImageClick(images, index)
                }
            )
        }
    }
}
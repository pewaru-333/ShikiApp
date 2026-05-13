@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.fastForEach
import org.application.shikiapp.shared.utils.extensions.flattenImages
import org.application.shikiapp.shared.utils.ui.CommentContent
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_refresh

@Composable
fun HtmlContent(commentContent: List<CommentContent>?) {
    var galleryInfo by remember { mutableStateOf<Pair<List<CommentContent.ImageContent>, Int>?>(null) }

    val allImages = remember(commentContent) {
        commentContent?.flattenImages() ?: emptyList()
    }

    BoxWithConstraints {
        val containerMaxWidth = maxWidth

        Column {
            commentContent?.fastForEach { item ->
                RenderContent(
                    content = item,
                    containerMaxWidth = containerMaxWidth,
                    onImageClick = {
                        val index = allImages.indexOf(it)
                        if (index != -1) {
                            galleryInfo = allImages to index
                        }
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
            var opened by remember { mutableStateOf(setOf<String>()) }

            val visualString = remember(content.text, opened) {
                val builder = AnnotatedString.Builder()
                val structuralAnnotations = content.text.getStringAnnotations(0, content.text.length)
                    .filter { it.tag == "spoiler_label" || it.tag == "spoiler_content" }
                    .sortedBy { it.start }

                var currentIndex = 0
                for (ann in structuralAnnotations) {
                    if (ann.start < currentIndex) continue
                    if (ann.start > currentIndex) {
                        builder.append(content.text.subSequence(currentIndex, ann.start))
                    }

                    val isRevealed = ann.item in opened
                    if (ann.tag == "spoiler_label" && !isRevealed) {
                        builder.append(content.text.subSequence(ann.start, ann.end))
                    } else if (ann.tag == "spoiler_content" && isRevealed) {
                        builder.append(content.text.subSequence(ann.start, ann.end))
                    }

                    currentIndex = ann.end
                }

                if (currentIndex < content.text.length) {
                    builder.append(content.text.subSequence(currentIndex, content.text.length))
                }

                val beforeString = builder.toAnnotatedString()
                val finalBuilder = AnnotatedString.Builder(beforeString)

                val openStyle = SpanStyle(
                    background = Color.Gray.copy(alpha = 0.2f),
                    textDecoration = TextDecoration.None,
                    color = Color.Unspecified
                )

                val closedInlineStyle = SpanStyle(
                    background = Color.Gray,
                    color = Color.Transparent,
                    textDecoration = TextDecoration.None
                )

                val closedLabelStyle = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color(0xFF33BBFF),
                    background = Color.Transparent
                )

                beforeString.getStringAnnotations("spoiler_inline", 0, beforeString.length).forEach { ann ->
                    val isRevealed = ann.item in opened
                    val style = if (isRevealed) openStyle else closedInlineStyle

                    finalBuilder.addStyle(style, ann.start, ann.end)
                    finalBuilder.addLink(
                        start = ann.start,
                        end = ann.end,
                        clickable = LinkAnnotation.Clickable(
                            tag = ann.item,
                            styles = TextLinkStyles(style = style),
                            linkInteractionListener = {
                                opened = if (isRevealed) opened - ann.item else opened + ann.item
                            }
                        )
                    )
                }

                beforeString.getStringAnnotations("spoiler_label", 0, beforeString.length).forEach { ann ->
                    if (ann.item !in opened) {
                        finalBuilder.addStyle(closedLabelStyle, ann.start, ann.end)
                        finalBuilder.addLink(
                            start = ann.start,
                            end = ann.end,
                            clickable = LinkAnnotation.Clickable(
                                tag = ann.item,
                                styles = TextLinkStyles(style = closedLabelStyle),
                                linkInteractionListener = { opened = opened + ann.item }
                            )
                        )
                    }
                }

                beforeString.getStringAnnotations("spoiler_content", 0, beforeString.length).forEach { ann ->
                    if (ann.item in opened) {
                        finalBuilder.addStyle(openStyle, ann.start, ann.end)
                        finalBuilder.addLink(
                            start = ann.start,
                            end = ann.end,
                            clickable = LinkAnnotation.Clickable(
                                tag = ann.item,
                                styles = TextLinkStyles(style = openStyle),
                                linkInteractionListener = { opened = opened - ann.item }
                            )
                        )
                    }
                }

                finalBuilder.toAnnotatedString()
            }

            Text(
                text = visualString,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = content.inlineContent,
                modifier = Modifier.padding(vertical = 4.dp),
                overflow = TextOverflow.Ellipsis
            )
        }

        is CommentContent.ListContent -> {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content.items.fastForEach { item ->
                    RenderContent(item, containerMaxWidth, onImageClick)
                }
            }
        }

        is CommentContent.ListItemContent -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = content.prefix,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    content.items.fastForEach { item ->
                        RenderContent(item, containerMaxWidth, onImageClick)
                    }
                }
            }
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
                    .padding(vertical = 4.dp)
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
                    .padding(vertical = 4.dp)
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
                    .padding(vertical = 4.dp)
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

                AnimatedVisibility(
                    visible = isVisible,
                    enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                    exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                ) {
                    Column(Modifier.padding(start = 8.dp, top = 12.dp)) {
                        content.items.fastForEach {
                            RenderContent(it, containerMaxWidth, onImageClick)
                        }
                    }
                }
            }
        }

        is CommentContent.QuoteContent -> {
            val primary = MaterialTheme.colorScheme.primary

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .drawBehind {
                        drawLine(
                            color = primary,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 4.dp.toPx()
                        )
                    }
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = content.author,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                )

                Spacer(Modifier.height(4.dp))

                content.items.fastForEach {
                    RenderContent(it, containerMaxWidth, onImageClick)
                }
            }
        }

        is CommentContent.BanContent -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleContentImage(content.moderatorAvatar, Modifier.size(24.dp), ContentScale.Crop)

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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                )
            }
        }
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
                    if (index != -1) {
                        onImageClick(images, index)
                    }
                }
            )
        }
    }
}
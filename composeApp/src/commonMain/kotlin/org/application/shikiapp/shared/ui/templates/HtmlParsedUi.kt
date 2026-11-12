package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.extensions.flattenImages
import org.application.shikiapp.shared.utils.ui.CommentContent
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_moderator
import shikiapp.composeapp.generated.resources.text_spoiler
import shikiapp.composeapp.generated.resources.text_video

@Composable
fun HtmlContent(commentContent: List<CommentContent>?) {
    var imageIndex by remember(commentContent) { mutableStateOf<Int?>(null) }

    val allImages = remember(commentContent) {
        commentContent?.flattenImages() ?: emptyList()
    }
    val imageUrls = remember(allImages) {
        allImages.map { it.fullUrl?.takeIf(String::isNotBlank) ?: it.previewUrl }
    }

    Column {
        commentContent?.fastForEach { item ->
            RenderContent(
                content = item,
                onImageClick = { image ->
                    val index = allImages.indexOfFirst { it === image }
                    if (index != -1) {
                        imageIndex = index
                    }
                }
            )
        }
    }

    if (allImages.isNotEmpty()) {
        DialogImages(
            images = imageUrls,
            initialIndex = imageIndex ?: 0,
            isVisible = imageIndex != null,
            onClose = { imageIndex = null }
        )
    }
}

@Composable
private fun RenderContent(
    content: CommentContent,
    onImageClick: (CommentContent.ImageContent) -> Unit
) {
    when (content) {
        is CommentContent.TextContent -> {
            var opened by remember(content.text) { mutableStateOf(emptySet<String>()) }
            val annotations = remember(content.text) {
                content.text.getStringAnnotations("spoiler_inline", 0, content.text.length)
                    .sortedWith(compareBy(AnnotatedString.Range<String>::start)
                        .thenByDescending(AnnotatedString.Range<String>::end)
                        .thenBy { it.item.substringAfterLast('_').toIntOrNull() ?: 0 })
            }

            val closed = remember(annotations, opened) {
                annotations.filter { it.item !in opened }
            }

            val visualString = remember(content.text, annotations, opened, closed) {
                if (annotations.isEmpty()) return@remember content.text

                val source = content.text.flatMapAnnotations { annotation ->
                    if (annotation.item is LinkAnnotation && closed.any { it.start < annotation.end && it.end > annotation.start }) emptyList()
                    else listOf(annotation)
                }
                val builder = AnnotatedString.Builder(source)
                val openStyle = SpanStyle(background = Color.Gray.copy(alpha = 0.2f))
                val closedStyle = SpanStyle(
                    background = Color.Gray,
                    color = Color.Transparent,
                    textDecoration = TextDecoration.None
                )

                annotations.forEachIndexed { index, annotation ->
                    val hidden = annotations.subList(0, index).any {
                        it.start <= annotation.start && it.end >= annotation.end && it.item !in opened
                    }
                    if (hidden) return@forEachIndexed

                    val isOpen = annotation.item in opened
                    val style = if (isOpen) openStyle else closedStyle
                    builder.addStyle(style, annotation.start, annotation.end)

                    fun addToggle(start: Int, end: Int) {
                        if (start >= end) return

                        builder.addLink(
                            start = start,
                            end = end,
                            clickable = LinkAnnotation.Clickable(
                                tag = annotation.item,
                                styles = TextLinkStyles(style = style),
                                linkInteractionListener = {
                                    opened = if (annotation.item in opened) opened - annotation.item
                                    else opened + annotation.item
                                }
                            )
                        )
                    }

                    if (!isOpen) {
                        addToggle(annotation.start, annotation.end)
                    } else {
                        val excluded = buildList {
                            source.getLinkAnnotations(annotation.start, annotation.end).forEach {
                                add(it.start to it.end)
                            }
                            annotations.subList(index + 1, annotations.size).forEach {
                                if (it.start >= annotation.start && it.end <= annotation.end) add(it.start to it.end)
                            }
                        }.sortedBy { it.first }

                        var start = annotation.start
                        for ((left, right) in excluded) {
                            addToggle(start, left.coerceIn(annotation.start, annotation.end))
                            start = maxOf(start, right.coerceAtMost(annotation.end))
                        }
                        addToggle(start, annotation.end)
                    }
                }

                builder.toAnnotatedString()
            }

            val inlineContent = remember(content.text, content.inlineContent, annotations, closed) {
                if (annotations.isEmpty() || content.inlineContent.isEmpty()) return@remember content.inlineContent

                val hidden = content.text.getStringAnnotations(0, content.text.length)
                    .mapNotNullTo(HashSet()) { annotation ->
                        if (
                            annotation.item in content.inlineContent &&
                            closed.any { annotation.start < it.end && annotation.end > it.start }
                        ) annotation.item else null
                    }

                if (hidden.isEmpty()) content.inlineContent
                else content.inlineContent.mapValues { (id, inline) ->
                    if (id in hidden) InlineTextContent(inline.placeholder) {} else inline
                }
            }

            Text(
                text = visualString,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = inlineContent,
                modifier = Modifier.padding(vertical = 4.dp),
                overflow = TextOverflow.Ellipsis
            )
        }

        is CommentContent.ListContent -> {
            Column(Modifier.padding(vertical = 4.dp), Arrangement.spacedBy(4.dp)) {
                content.items.fastForEach { item ->
                    RenderContent(item, onImageClick)
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
                        RenderContent(item, onImageClick)
                    }
                }
            }
        }

        is CommentContent.ImageContent -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            val density = LocalDensity.current
            val maxHeight = 240.dp
            val width = content.width
            val height = content.height
            val knownSize = width != null && height != null && width > 0f && height > 0f && width.isFinite() && height.isFinite()
            val imageModifier = if (knownSize) {
                Modifier
                    .sizeIn(maxWidth = with(density) { width.toDp() }, maxHeight = maxHeight)
                    .aspectRatio(width / height)
            } else {
                Modifier
                    .fillMaxWidth()
                    .heightIn(120.dp, maxHeight)
            }

            AnimatedAsyncImage(
                model = content.previewUrl,
                contentScale = ContentScale.Fit,
                modifier = imageModifier
                    .clip(MaterialTheme.shapes.medium)
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
                    .clickable(enabled = content.videoUrl.isNotBlank()) {
                        try {
                            uriHandler.openUri(content.videoUrl)
                        } catch (_: IllegalArgumentException) {
                        }
                    }
            ) {
                AnimatedAsyncImage(
                    model = content.previewUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )

                Surface(Modifier.size(56.dp), CircleShape, Color.Black.copy(alpha = 0.6f)) {
                    VectorIcon(
                        imageVector = Icons.PlayCircle,
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
                        text = content.source.ifBlank { stringResource(Res.string.text_video) }.uppercase(),
                        modifier = Modifier.padding(8.dp, 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                    )
                }
            }
        }

        is CommentContent.SpoilerContent -> {
            var isVisible by remember(content) { mutableStateOf(false) }
            val rotation by animateFloatAsState(targetValue = if (isVisible) 90f else 0f)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isVisible = !isVisible }
                        .padding(12.dp, 10.dp)
                ) {
                    VectorIcon(
                        imageVector = Icons.KeyboardArrowRight,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = content.title.ifBlank { stringResource(Res.string.text_spoiler) },
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
                    Column(Modifier.padding(start = 20.dp, end = 12.dp, bottom = 10.dp)) {
                        content.items.fastForEach {
                            RenderContent(it, onImageClick)
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
                if (content.author.isNotBlank()) {
                    Text(
                        text = content.author,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    )

                    Spacer(Modifier.height(4.dp))
                }

                content.items.fastForEach {
                    RenderContent(it, onImageClick)
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
                        text = content.moderatorName.ifBlank { stringResource(Res.string.text_moderator) } + ":",
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

    items(
        items = contentList,
        contentType = { it::class }
    ) { item ->
        RenderContent(
            content = item,
            onImageClick = { clickedImage ->
                val index = images.indexOfFirst { it === clickedImage }
                if (index != -1) {
                    onImageClick(images, index)
                }
            }
        )
    }
}
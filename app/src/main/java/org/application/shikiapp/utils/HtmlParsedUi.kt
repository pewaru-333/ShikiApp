package org.application.shikiapp.utils

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.screens.LoadingScreen

@Composable
fun HtmlComment(text: String) {
    var content by remember { mutableStateOf<List<CommentContent>?>(null) }
    var image by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(text) {
        content = withContext(Dispatchers.Default) {
            HtmlParser.parseComment(text)
        }
    }

    content?.let {
        BoxWithConstraints {
            val containerMaxWidth = this.maxWidth

            Column {
                it.forEach { item ->
                    RenderContent(
                        content = item,
                        containerMaxWidth = containerMaxWidth,
                        onImageClick = { image = it }
                    )
                }
            }
        }
    } ?: LoadingScreen()

    if (image != null) {
        Dialog(
            onDismissRequest = { image = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                filterQuality = FilterQuality.High
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

            fun onLinkClick(layoutResult: TextLayoutResult,  offset: Offset) {
                layoutResult.getOffsetForPosition(offset).let { position ->
                    content.text.getStringAnnotations("URL", position, position)
                        .firstOrNull()
                        ?.let { annotation ->
                            context.startActivity(Intent(Intent.ACTION_VIEW, annotation.item.toUri()))
                        }
                }
            }

            Text(
                text = content.text,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = content.inlineContent,
                onTextLayout = { layoutResult = it },
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures { offset ->
                        layoutResult?.let { layoutResult -> onLinkClick(layoutResult, offset) }
                    }
                }
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
                    Icon(
                        imageVector = if (isVisible) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
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

        is CommentContent.LineBreakContent -> Spacer(Modifier.height(8.dp))
    }
}
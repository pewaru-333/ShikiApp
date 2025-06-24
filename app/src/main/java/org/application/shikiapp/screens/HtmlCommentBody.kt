package org.application.shikiapp.screens

import android.content.Context
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.utils.BLANK
import kotlin.math.min

@Composable
fun HtmlCommentBody(text: String) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics

    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    val linkColor = Color.Blue

    var string by remember { mutableStateOf(AnnotatedString(BLANK)) }
    var imageMap by remember { mutableStateOf<Map<String, LoadedImage>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var show by remember { mutableStateOf(false)}
    var bigImage by remember { mutableStateOf(BLANK)}

    LaunchedEffect(text, linkColor) {
        val (annotatedText, images) = withContext(Dispatchers.Default) {
            toAnnotatedString(context, text, linkColor)
        }
        string = annotatedText
        imageMap = images
        loading = false
    }

    val inlineContent = remember(imageMap) {
        imageMap.mapValues { (_, image) ->
            val isEmoji = image.source.contains("smileys")

            val imageWidth = if (isEmoji) 22.sp else pxToSp(context, image.width)
            val imageHeight = if (isEmoji) 22.sp else pxToSp(context, image.height)

            val width = min(pxToSp(context, screenWidth.times(0.85f)).value, imageWidth.value).sp
            val height = min(pxToSp(context, screenHeight.times(0.6f)).value, imageHeight.value).sp

            InlineTextContent(Placeholder(width, height, PlaceholderVerticalAlign.Top)) {
                AsyncImage(
                    model = image.source,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(!isEmoji) {
                            show = true
                            bigImage = image.source
                        }
                )
            }
        }
    }

    if (loading) LoadingScreen() else Text(text = string, inlineContent = inlineContent)
    if (show) Dialog({ show = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        AsyncImage(
            model = bigImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            filterQuality = FilterQuality.High
        )
    }
}

private suspend fun toAnnotatedString(
    context: Context,
    string: String,
    linkColor: Color
): Pair<AnnotatedString, Map<String, LoadedImage>> {
    val spanned = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)

    val images = mutableMapOf<String, LoadedImage>()
    var counter = 0

    val annotatedString = buildAnnotatedString {
        fun appendTextWithAndCopyStyling(start: Int, end: Int, offset: Int) {
            append(spanned, start, end)
            spanned.getSpans(start, end, Any::class.java)
                .forEach { span ->
                    val spanStart = spanned.getSpanStart(span) - offset
                    val spanEnd = spanned.getSpanEnd(span) - offset
                    val style = when (span) {
                        is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))
                        is StyleSpan -> when (span.style) {
                            Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                            Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                            Typeface.BOLD_ITALIC -> SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )

                            else -> null
                        }

                        is SuperscriptSpan -> SpanStyle(baselineShift = BaselineShift.Superscript)
                        is SubscriptSpan -> SpanStyle(baselineShift = BaselineShift.Subscript)
                        is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
                        is StrikethroughSpan -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                        is URLSpan -> SpanStyle(
                            color = linkColor,
                            textDecoration = TextDecoration.Underline
                        )

                        else -> null
                    }
                    style?.let { addStyle(it, spanStart, spanEnd) }
                    when (span) {
                        is URLSpan -> addLink(
                            start = spanStart,
                            end = spanEnd,
                            url = LinkAnnotation.Url(
                                url = span.url,
                                styles = TextLinkStyles(
                                    SpanStyle(
                                        color = Color.Blue,
                                        textDecoration = TextDecoration.Underline,
                                        platformStyle = PlatformSpanStyle.Default
                                    )
                                )
                            )
                        )
                    }
                }
        }

        var offset = 0
        var previousEnd = 0
        spanned.getSpans(0, spanned.length, ImageSpan::class.java)
            .sortedBy { span ->
                spanned.getSpanStart(span)
            }
            .forEach { imageSpan ->
                val spanStart = spanned.getSpanStart(imageSpan)
                val spanEnd = spanned.getSpanEnd(imageSpan)

                appendTextWithAndCopyStyling(previousEnd, spanStart, offset)

                imageSpan.source
                    ?.let { source ->
                        loadImage(context, source)
                    }
                    ?.let { image ->
                        val id = "image${counter++}"
                        images[id] = image.copy(width = image.width, height = image.height)

                        appendInlineContent(id, image.source)
                        addStyle(ParagraphStyle(lineHeight = pxToSp(context, image.height)), length, length)
                        offset -= image.source.length
                    }

                offset += spanEnd - spanStart
                previousEnd = spanEnd
            }

        appendTextWithAndCopyStyling(previousEnd, spanned.length, offset)
    }

    return Pair(annotatedString, images)
}

private suspend fun loadImage(context: Context, source: String): LoadedImage? {
    val imageResult = context.imageLoader
        .execute(ImageRequest.Builder(context).data(source).build())

    return LoadedImage(
        source = source.replace("thumbnail", "original"),
        width = imageResult.image?.width?.toFloat() ?: return null,
        height = imageResult.image?.height?.toFloat() ?: return null,
    )
}

private fun pxToSp(context: Context, px: Float) =
    (px / context.resources.displayMetrics.density).sp

data class LoadedImage(val source: String, val width: Float, val height: Float)
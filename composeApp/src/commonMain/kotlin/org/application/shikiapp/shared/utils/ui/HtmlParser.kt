package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.utils.BASE_URL
import org.application.shikiapp.shared.utils.BLANK

sealed interface CommentContent {
    val items: List<CommentContent> get() = emptyList()

    data class TextContent(val text: AnnotatedString, val inlineContent: Map<String, InlineTextContent> = emptyMap()) : CommentContent
    data class ImageContent(val previewUrl: String, val fullUrl: String?, val width: Float?, val height: Float?) : CommentContent
    data class SpoilerContent(val title: String, override val items: List<CommentContent>) : CommentContent
    data class QuoteContent(val author: String, override val items: List<CommentContent>) : CommentContent
    data class VideoContent(val previewUrl: String, val videoUrl: String, val source: String) : CommentContent
    data class BanContent(val moderatorName: String, val moderatorAvatar: String, val reason: AnnotatedString) : CommentContent
    data object LineBreakContent : CommentContent
}

object HtmlParser {
    private fun preprocessHtml(document: Document) {
        document.select(".b-spoiler_inline").forEach { spoilerElement ->
            val contentHtml = spoilerElement.selectFirst("span")?.html().orEmpty()

            spoilerElement.html(contentHtml)
        }
    }

    fun parseComment(html: String): List<CommentContent> {
        val localizedHtml = Formatter.localizeNames(html)
        val document = Ksoup.parse(localizedHtml, BASE_URL).apply {
            select("div.right-text").remove()
            select(".b-replies").remove()
        }

        preprocessHtml(document)

        return parseNodes(document.body().childNodes())
    }

    private fun parseNodes(nodes: List<Node>): List<CommentContent> {
        val contentList = mutableListOf<CommentContent>()
        val inlineNodes = mutableListOf<Node>()

        fun makeInlineGroup() {
            if (inlineNodes.isNotEmpty()) {
                val (annotatedString, inlineContentMap) = stringFromNodes(inlineNodes)

                if (annotatedString.isNotBlank()) {
                    contentList.add(CommentContent.TextContent(annotatedString, inlineContentMap))
                }

                inlineNodes.clear()
            }
        }

        nodes.forEach { node ->
            if (isBlockElement(node)) {
                makeInlineGroup()

                when {
                    (node as Element).hasClass("b-quote") || node.hasClass("b-quote-v2") || node.tagName() == "blockquote" -> {
                        val author = node.selectFirst(".quoteable")?.text() ?: "Пользователь"
                        val quoteHtml = node.selectFirst(".quote-content")?.html() ?: BLANK

                        contentList.add(CommentContent.QuoteContent(author, parseComment(quoteHtml)))
                    }

                    node.hasClass("b-spoiler_block") -> {
                        val titleSpan = node.selectFirst("span")
                        val title = titleSpan?.text()?.trim() ?: "Спойлер"

                        titleSpan?.remove()

                        val contentNodes = node.selectFirst("div")?.childNodes() ?: node.childNodes()
                        contentList.add(CommentContent.SpoilerContent(title, parseNodes(contentNodes)))
                    }

                    node.hasClass("b-image") -> {
                        val imgTag = node.selectFirst("img")

                        val fullUrl = when {
                            node.tagName() == "a" -> node.attr("abs:href")
                            node.parent()?.tagName() == "a" -> node.parent()?.attr("abs:href")
                            else -> null
                        }

                        contentList.add(
                            CommentContent.ImageContent(
                                previewUrl = imgTag?.attr("abs:src") ?: fullUrl.orEmpty(),
                                fullUrl = fullUrl,
                                width = imgTag?.attr("data-width")?.toFloatOrNull(),
                                height = imgTag?.attr("data-height")?.toFloatOrNull()
                            )
                        )
                    }

                    node.hasClass("b-video") -> {
                        val link = node.selectFirst("a.video-link")
                        val img = node.selectFirst("img")

                        val source = node.selectFirst(".marker")?.text().orEmpty()
                        val videoUrl = link?.attr("href") ?: link?.attr("data-href").orEmpty()
                        val previewUrl = img?.attr("src").orEmpty().let { url ->
                            when {
                                url.startsWith("//") -> "https:$url"
                                url.startsWith("http://") -> url.replace("http://", "https://")
                                else -> url
                            }
                        }

                        contentList.add(CommentContent.VideoContent(previewUrl, videoUrl, source))
                    }

                    node.hasClass("ban") -> {
                        val modElement = node.selectFirst(".b-user16 a")
                        val modName = modElement?.attr("title") ?: "Модератор"
                        val modAvatar = modElement?.selectFirst("img")?.attr("abs:src").orEmpty()

                        val resolutionNode = node.selectFirst(".resolution")
                        val (reasonText, _) = if (resolutionNode != null) {
                            stringFromNodes(resolutionNode.childNodes())
                        } else {
                            AnnotatedString(node.text()) to emptyMap()
                        }

                        contentList.add(CommentContent.BanContent(modName, modAvatar, reasonText))
                    }

                    node.tagName() == "br" -> contentList.add(CommentContent.LineBreakContent)
                }
            } else if (isBlockContainer(node)) {
                makeInlineGroup()
                contentList.addAll(parseNodes(node.childNodes()))
            } else {
                inlineNodes.add(node)
            }
        }
        makeInlineGroup()

        return contentList
    }

    private fun stringFromNodes(nodes: List<Node>): Pair<AnnotatedString, Map<String, InlineTextContent>> {
        val inlineContentMap = mutableMapOf<String, InlineTextContent>()
        var smileyCounter = 0

        val annotatedString = buildAnnotatedString {
            nodes.forEach { node ->
                when (node) {
                    is TextNode -> append(node.text())
                    is Element -> {
                        when {
                            node.hasClass("smiley") -> {
                                val id = "smiley_${smileyCounter++}"
                                appendInlineContent(id, "[emoji]")
                                inlineContentMap[id] = InlineTextContent(Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.Center)) {
                                    AnimatedAsyncImage(node.attr("abs:src"), Modifier.fillMaxSize())
                                }
                            }
                            else -> {
                                val style = getStyleForTag(node.tagName())
                                val startIndex = length
                                val (childText, childInlineContent) = stringFromNodes(node.childNodes())

                                append(childText)
                                inlineContentMap.putAll(childInlineContent)
                                addStyle(style, startIndex, length)

                                if (node.tagName() == "a" && node.hasAttr("href")) {
                                    addLink(
                                        start = startIndex,
                                        end = length,
                                        url = LinkAnnotation.Url(
                                            url = node.attr("abs:href"),
                                            styles = TextLinkStyles(
                                                style = SpanStyle(
                                                    color = Color(0xFF33BBFF),
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return Pair(annotatedString, inlineContentMap)
    }

    private fun isBlockElement(node: Node) = node is Element &&
            (node.hasClass("b-quote") ||
                    node.hasClass("b-quote-v2") ||
                    node.tagName() == "blockquote" ||
                    node.hasClass("b-spoiler_block") ||
                    node.hasClass("b-image") ||
                    node.hasClass("b-video") ||
                    node.hasClass("ban")
                    || node.tagName() == "br")

    private fun isBlockContainer(node: Node): Boolean {
        if (node !is Element) return false

        return when (node.tagName()) {
            "div", "p", "center" -> true
            else -> false
        }
    }

    internal fun getStyleForTag(tagName: String) = when (tagName.lowercase()) {
        "s" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
        "strong", "b" -> SpanStyle(fontWeight = FontWeight.Bold)
        "em", "i" -> SpanStyle(fontStyle = FontStyle.Italic)
        "u" -> SpanStyle(textDecoration = TextDecoration.Underline)
        "a" -> SpanStyle(color = Color(0xFF33BBFF), textDecoration = TextDecoration.Underline)
        else -> SpanStyle()
    }
}
package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.ui.templates.AnimatedAsyncImage
import org.application.shikiapp.shared.utils.BLANK

sealed interface CommentContent {
    val items: List<CommentContent> get() = emptyList()

    data class TextContent(val text: AnnotatedString, val inlineContent: Map<String, InlineTextContent> = emptyMap()) : CommentContent
    data class ImageContent(val previewUrl: String, val fullUrl: String?, val width: Float?, val height: Float?) : CommentContent
    data class ListContent(val isOrdered: Boolean, override val items: List<ListItemContent>) : CommentContent
    data class ListItemContent(val prefix: String, override val items: List<CommentContent>) : CommentContent
    data class SpoilerContent(val title: String, override val items: List<CommentContent>) : CommentContent
    data class QuoteContent(val author: String, override val items: List<CommentContent>) : CommentContent
    data class VideoContent(val previewUrl: String, val videoUrl: String, val source: String) : CommentContent
    data class BanContent(val moderatorName: String, val moderatorAvatar: String, val reason: AnnotatedString) : CommentContent
}

object HtmlParser {
    private val quoteTag = Regex("""\[quote(?:=([^]]*))?]|\[/quote]""", RegexOption.IGNORE_CASE)

    fun parseComment(html: String, baseUrl: String = ApiRoutes.workingBaseUrl): List<CommentContent> {
        if (html.isBlank()) return emptyList()

        val document = Ksoup.parse(html, baseUrl)
        document.select("div.right-text, .b-replies, script, style").remove()

        for (english in document.select("span.name-en")) {
            val russian = english.nextElementSibling()?.takeIf { it.hasClass("name-ru") } ?: continue
            if (russian.text().isNotBlank()) english.remove()
            else russian.remove()
        }

        for (element in document.select("[data-text-ru], [data-text-en]")) {
            val text = element.attr("data-text-ru").ifBlank { element.attr("data-text-en") }
            if (text.isNotBlank()) element.text(text)
        }

        return Parser().parseNodes(document.body().childNodes())
    }

    private class Parser {
        private var nextId = 0

        fun parseNodes(
            nodes: List<Node>,
            textStyle: SpanStyle = styleEmpty,
            preformatted: Boolean = false,
            link: LinkAnnotation.Url? = null
        ): List<CommentContent> {
            var contentList = ArrayList<CommentContent>()
            val inlineNodes = ArrayList<Node>()
            val quotes = ArrayDeque<Pair<String, ArrayList<CommentContent>>>()

            fun makeInlineGroup() {
                if (inlineNodes.isEmpty()) return

                val (text, inlineContent) = stringFromNodes(inlineNodes, preformatted, textStyle, link)
                if (text.isNotBlank()) {
                    contentList.add(CommentContent.TextContent(text, inlineContent))
                }

                inlineNodes.clear()
            }

            fun closeQuote() {
                makeInlineGroup()

                val (author, parent) = quotes.removeLast()
                parent.add(CommentContent.QuoteContent(author, contentList))
                contentList = parent
            }

            for (node in nodes) {
                if (node is TextNode && !preformatted) {
                    val text = node.getWholeText()
                    var position = 0

                    for (tag in quoteTag.findAll(text)) {
                        val closing = tag.value.startsWith("[/")
                        if (closing && quotes.isEmpty()) continue

                        inlineNodes.add(TextNode(text.substring(position, tag.range.first)))
                        if (closing) {
                            closeQuote()
                        } else {
                            makeInlineGroup()
                            quotes.addLast(Pair(tag.groupValues[1].substringAfterLast(';'), contentList))
                            contentList = ArrayList()
                        }

                        position = tag.range.last + 1
                    }

                    if (position > 0) {
                        inlineNodes.add(TextNode(text.substring(position)))
                        continue
                    }
                }

                if (node is Element && (node.tagName() == "script" || node.tagName() == "style")) continue

                if (node is Element && isBlockElement(node)) {
                    makeInlineGroup()

                    when {
                        node.hasClass("b-quote") || node.hasClass("b-quote-v2") || node.tagName() == "blockquote" -> {
                            val authorNode = node.children().firstOrNull { it.hasClass("quoteable") }
                            val body = node.children().firstOrNull { it.hasClass("quote-content") }
                            val content = body?.childNodes() ?: node.childNodes().filter { it != authorNode }

                            contentList.add(CommentContent.QuoteContent(authorNode?.text().orEmpty(), parseNodes(content, textStyle, preformatted, link)))
                        }

                        node.hasClass("b-spoiler") || node.hasClass("b-spoiler_block") || node.hasClass("b-spoiler_inline") -> {
                            val titleNode = if (node.hasClass("b-spoiler_inline")) null else node.children().firstOrNull {
                                it.tagName() == "label" || it.hasClass("label") ||
                                        (node.hasClass("b-spoiler_block") && it.tagName() == "span")
                            }
                            val body = node.children().firstOrNull { it.hasClass("inner") }
                            val content = body?.childNodes() ?: node.childNodes().filter { it != titleNode }

                            contentList.add(CommentContent.SpoilerContent(titleNode?.text().orEmpty(), parseNodes(content, textStyle, preformatted, link)))
                        }

                        node.hasClass("b-image") || node.tagName() == "img" -> {
                            val image = if (node.tagName() == "img") node else node.selectFirst("img")
                            val link = if (node.tagName() == "a") node else node.closest("a")
                            val fullUrl = link?.url("href").orEmpty().ifEmpty { link?.url("data-href").orEmpty() }
                            val previewUrl = image?.url("src").orEmpty()
                                .ifEmpty { image?.url("data-src").orEmpty() }
                                .ifEmpty { fullUrl }

                            if (previewUrl.isNotEmpty()) {
                                contentList.add(
                                    CommentContent.ImageContent(
                                        previewUrl = previewUrl,
                                        fullUrl = fullUrl.ifEmpty { null },
                                        width = image?.dimension("data-width") ?: image?.dimension("width") ?: node.dimension("data-width"),
                                        height = image?.dimension("data-height") ?: image?.dimension("height") ?: node.dimension("data-height")
                                    )
                                )
                            }
                        }

                        node.hasClass("b-video") -> {
                            val link = node.selectFirst("a.video-link") ?: node.selectFirst("a")
                            val videoUrl = link?.url("href").orEmpty().ifEmpty { link?.url("data-href").orEmpty() }

                            if (videoUrl.isNotEmpty()) {
                                contentList.add(
                                    CommentContent.VideoContent(
                                        previewUrl = node.selectFirst("img")?.url("src").orEmpty(),
                                        videoUrl = videoUrl,
                                        source = node.selectFirst(".marker")?.text().orEmpty()
                                    )
                                )
                            }
                        }

                        node.hasClass("ban") -> {
                            val moderator = node.selectFirst(".b-user16 a")
                            val resolution = node.selectFirst(".resolution")
                            val reason = if (resolution == null) AnnotatedString(node.text())
                            else stringFromNodes(resolution.childNodes(), preformatted).first

                            contentList.add(
                                CommentContent.BanContent(
                                    moderatorName = moderator?.attr("title").orEmpty().ifBlank { moderator?.text().orEmpty() },
                                    moderatorAvatar = moderator?.selectFirst("img")?.url("src").orEmpty(),
                                    reason = reason
                                )
                            )
                        }

                        node.tagName() == "ul" || node.tagName() == "ol" -> {
                            val ordered = node.tagName() == "ol"
                            var number = node.attr("start").toIntOrNull() ?: 1
                            val items = buildList {
                                for (item in node.children()) {
                                    if (item.tagName() != "li") continue
                                    if (ordered) number = item.attr("value").toIntOrNull() ?: number

                                    val prefix = if (ordered) "${number++}. " else "• "
                                    add(CommentContent.ListItemContent(prefix, parseNodes(item.childNodes(), textStyle, preformatted, link)))
                                }
                            }

                            contentList.add(CommentContent.ListContent(ordered, items))
                        }

                        else -> contentList.addAll(parseNodes(node.childNodes(), textStyle, preformatted, link))
                    }
                } else if (node is Element && (isBlockContainer(node) || containsBlock(node))) {
                    makeInlineGroup()
                    contentList.addAll(
                        elements = parseNodes(
                            nodes = node.childNodes(),
                            textStyle = textStyle.merge(getStyleForTag(node.tagName())),
                            preformatted = preformatted || node.tagName() == "pre",
                            link = if (node.tagName() != "a") link
                            else {
                                val url = node.url("href").ifEmpty { node.url("data-href") }
                                if (url.isEmpty()) link else LinkAnnotation.Url(url, TextLinkStyles(style = styleLink))
                            }
                        )
                    )
                } else {
                    inlineNodes.add(node)
                }
            }

            makeInlineGroup()
            while (quotes.isNotEmpty()) {
                closeQuote()
            }

            return contentList
        }

        private fun stringFromNodes(
            nodes: List<Node>,
            preformatted: Boolean,
            textStyle: SpanStyle = styleEmpty,
            link: LinkAnnotation.Url? = null
        ): Pair<AnnotatedString, Map<String, InlineTextContent>> {
            val inlineContent = HashMap<String, InlineTextContent>()
            val text = buildAnnotatedString {
                withStyle(textStyle) {
                    if (link == null) appendNodes(nodes, inlineContent, preformatted)
                    else withLink(link) {
                        appendNodes(nodes, inlineContent, preformatted)
                    }
                }
            }

            return Pair(text, inlineContent)
        }

        private fun AnnotatedString.Builder.appendNodes(
            nodes: List<Node>,
            inlineContent: MutableMap<String, InlineTextContent>,
            preformatted: Boolean
        ) {
            for (node in nodes) {
                when (node) {
                    is TextNode -> append(if (preformatted) node.getWholeText() else node.text())

                    is Element -> when {
                        node.tagName() == "script" || node.tagName() == "style" -> Unit

                        isInlineImage(node) -> {
                            val id = "smiley_${nextId++}"
                            val url = node.url("src").ifEmpty { node.url("data-src") }
                            appendInlineContent(id, node.attr("alt").ifBlank { "[emoji]" })
                            inlineContent[id] = InlineTextContent(Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.Center)) {
                                AnimatedAsyncImage(url, Modifier.fillMaxSize())
                            }
                        }

                        node.hasClass("b-spoiler_inline") -> {
                            val id = "spoiler_${nextId++}"
                            val start = length
                            appendNodes(node.childNodes(), inlineContent, preformatted)

                            if (length > start) addStringAnnotation("spoiler_inline", id, start, length)
                        }

                        node.tagName() == "br" -> append("\n")

                        else -> {
                            val style = getStyleForTag(node.tagName())
                            val styleIndex = if (style == styleEmpty) null else pushStyle(style)
                            val url = if (node.tagName() == "a") node.url("href").ifEmpty { node.url("data-href") } else BLANK

                            if (url.isEmpty()) {
                                appendNodes(node.childNodes(), inlineContent, preformatted || node.tagName() == "pre")
                            } else withLink(LinkAnnotation.Url(url, TextLinkStyles(style = styleLink))) {
                                appendNodes(node.childNodes(), inlineContent, preformatted)
                            }

                            if (styleIndex != null) pop(styleIndex)
                        }
                    }
                }
            }
        }

        private fun isBlockElement(node: Element): Boolean =
            node.hasClass("b-quote") || node.hasClass("b-quote-v2") || node.tagName() == "blockquote" ||
                    node.hasClass("b-spoiler") || node.hasClass("b-spoiler_block") ||
                    (node.hasClass("b-spoiler_inline") && containsBlock(node)) ||
                    node.hasClass("b-image") || (node.tagName() == "img" && !isInlineImage(node)) ||
                    node.hasClass("b-video") || node.hasClass("ban") || node.tagName() == "ul" || node.tagName() == "ol"

        private fun isInlineImage(node: Element) =
            node.hasClass("smiley") || (node.tagName() == "img" && node.closest(".b-user16") != null)

        private fun isBlockContainer(node: Element) = when (node.tagName()) {
            "div", "p", "center", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "section", "article", "figure" -> true
            else -> false
        }

        private fun containsBlock(node: Element): Boolean =
            node.children().any { isBlockElement(it) || isBlockContainer(it) || containsBlock(it) }

        private fun Element.dimension(attribute: String) =
            attr(attribute).toFloatOrNull()?.takeIf { it > 0f && it.isFinite() }

        private fun Element.url(attribute: String): String {
            if (attr(attribute).isBlank()) return BLANK

            val url = attr("abs:$attribute")

            return if (url.startsWith("http://img.youtube.com/") || url.startsWith("http://i.ytimg.com/")) url.replaceFirst("http://", "https://")
            else url
        }
    }

    internal fun getStyleForTag(tagName: String): SpanStyle = when (tagName.lowercase()) {
        "s", "del", "strike" -> styleStrike
        "strong", "b" -> styleBold
        "em", "i" -> styleItalic
        "u" -> styleUnderline
        "a" -> styleLink
        "code", "pre" -> styleCode
        "sup" -> styleSup
        "sub" -> styleSub
        "h1" -> styleH1
        "h2" -> styleH2
        "h3" -> styleH3
        "h4" -> styleH4
        "h5" -> styleH5
        "h6" -> styleH6
        else -> styleEmpty
    }

    private val styleEmpty = SpanStyle()
    private val styleStrike = SpanStyle(textDecoration = TextDecoration.LineThrough)
    private val styleBold = SpanStyle(fontWeight = FontWeight.Bold)
    private val styleItalic = SpanStyle(fontStyle = FontStyle.Italic)
    private val styleUnderline = SpanStyle(textDecoration = TextDecoration.Underline)
    private val styleLink = SpanStyle(color = Color(0xFF33BBFF), textDecoration = TextDecoration.Underline)
    private val styleCode = SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x20888888))
    private val styleSup = SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.8.em)
    private val styleSub = SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.8.em)

    private val styleH1 = SpanStyle(fontSize = 1.5.em, fontWeight = FontWeight.Bold)
    private val styleH2 = SpanStyle(fontSize = 1.4.em, fontWeight = FontWeight.Bold)
    private val styleH3 = SpanStyle(fontSize = 1.3.em, fontWeight = FontWeight.Bold)
    private val styleH4 = SpanStyle(fontSize = 1.2.em, fontWeight = FontWeight.Bold)
    private val styleH5 = SpanStyle(fontSize = 1.1.em, fontWeight = FontWeight.Bold)
    private val styleH6 = SpanStyle(fontSize = 1.0.em, fontWeight = FontWeight.Bold)
}
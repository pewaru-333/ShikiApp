package org.application.shikiapp.shared.utils.ui.subtitles

import android.graphics.Typeface
import android.text.Html
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.util.SparseArray
import androidx.annotation.OptIn
import androidx.core.util.size
import androidx.media3.common.text.HorizontalTextInVerticalContextSpan
import androidx.media3.common.text.RubySpan
import androidx.media3.common.text.TextAnnotation
import androidx.media3.common.text.TextEmphasisSpan
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util

object SpannedToHtmlConverter {

    private val NEWLINE_REGEX = Regex("(&#13;)?&#10;")

    @OptIn(UnstableApi::class)
    fun convert(text: CharSequence?, displayDensity: Float): HtmlAndCss {
        if (text == null) {
            return HtmlAndCss("", emptyMap())
        }
        if (text !is Spanned) {
            return HtmlAndCss(escapeHtml(text), emptyMap())
        }

        val backgroundColors = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
            .map { it.backgroundColor }
            .toSet()

        val cssRuleSets = backgroundColors.associate { backgroundColor ->
            HtmlUtils.cssAllClassDescendantsSelector("bg_$backgroundColor") to
                    Util.formatInvariant(
                        "background-color:%s;",
                        HtmlUtils.toCssRgba(backgroundColor)
                    )
        }

        val spanTransitions = findSpanTransitions(text, displayDensity)
        val html = StringBuilder(text.length)
        var previousTransition = 0

        for (i in 0 until spanTransitions.size) {
            val index = spanTransitions.keyAt(i)
            html.append(escapeHtml(text.subSequence(previousTransition, index)))

            val transition = spanTransitions.valueAt(i)
            transition.spansRemoved.sortWith(SpanInfo.FOR_CLOSING_TAGS)
            for (spanInfo in transition.spansRemoved) {
                html.append(spanInfo.closingTag)
            }

            transition.spansAdded.sortWith(SpanInfo.FOR_OPENING_TAGS)
            for (spanInfo in transition.spansAdded) {
                html.append(spanInfo.openingTag)
            }
            previousTransition = index
        }

        html.append(escapeHtml(text.subSequence(previousTransition, text.length)))

        return HtmlAndCss(html.toString(), cssRuleSets)
    }

    private fun findSpanTransitions(
        spanned: Spanned,
        displayDensity: Float
    ): SparseArray<Transition> {
        val spanTransitions = SparseArray<Transition>()

        for (span in spanned.getSpans(0, spanned.length, Any::class.java)) {
            val openingTag = getOpeningTag(span, displayDensity)
            val closingTag = getClosingTag(span)
            val spanStart = spanned.getSpanStart(span)
            val spanEnd = spanned.getSpanEnd(span)

            if (openingTag != null) {
                requireNotNull(closingTag)
                val spanInfo = SpanInfo(spanStart, spanEnd, openingTag, closingTag)
                spanTransitions.getOrCreate(spanStart).spansAdded.add(spanInfo)
                spanTransitions.getOrCreate(spanEnd).spansRemoved.add(spanInfo)
            }
        }

        return spanTransitions
    }

    @OptIn(UnstableApi::class)
    private fun getOpeningTag(span: Any, displayDensity: Float): String? {
        return when (span) {
            is StrikethroughSpan -> "<span style='text-decoration:line-through;'>"
            is ForegroundColorSpan -> Util.formatInvariant(
                "<span style='color:%s;'>",
                HtmlUtils.toCssRgba(span.foregroundColor)
            )

            is BackgroundColorSpan -> Util.formatInvariant(
                "<span class='bg_%s'>",
                span.backgroundColor
            )

            is HorizontalTextInVerticalContextSpan -> "<span style='text-combine-upright:all;'>"
            is AbsoluteSizeSpan -> {
                val sizeCssPx = if (span.dip) span.size.toFloat() else span.size / displayDensity
                Util.formatInvariant("<span style='font-size:%.2fpx;'>", sizeCssPx)
            }

            is RelativeSizeSpan -> Util.formatInvariant(
                "<span style='font-size:%.2f%%;'>",
                span.sizeChange * 100
            )

            is TypefaceSpan -> span.family?.let {
                Util.formatInvariant(
                    "<span style='font-family:\"%s\";'>",
                    it
                )
            }

            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> "<b>"
                Typeface.ITALIC -> "<i>"
                Typeface.BOLD_ITALIC -> "<b><i>"
                else -> null
            }

            is RubySpan -> when (span.position) {
                TextAnnotation.POSITION_BEFORE -> "<ruby style='ruby-position:over;'>"
                TextAnnotation.POSITION_AFTER -> "<ruby style='ruby-position:under;'>"
                TextAnnotation.POSITION_UNKNOWN -> "<ruby style='ruby-position:unset;'>"
                else -> null
            }

            is UnderlineSpan -> "<u>"
            is TextEmphasisSpan -> {
                val style = getTextEmphasisStyle(span.markShape, span.markFill)
                val position = getTextEmphasisPosition(span.position)
                Util.formatInvariant(
                    "<span style='-webkit-text-emphasis-style:%1\$s;text-emphasis-style:%1\$s;-webkit-text-emphasis-position:%2\$s;text-emphasis-position:%2\$s;display:inline-block;'>",
                    style, position
                )
            }

            else -> null
        }
    }

    @OptIn(UnstableApi::class)
    private fun getClosingTag(span: Any): String? {
        return when (span) {
            is StrikethroughSpan,
            is ForegroundColorSpan,
            is BackgroundColorSpan,
            is HorizontalTextInVerticalContextSpan,
            is AbsoluteSizeSpan,
            is RelativeSizeSpan,
            is TextEmphasisSpan -> "</span>"

            is TypefaceSpan -> if (span.family != null) "</span>" else null
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> "</b>"
                Typeface.ITALIC -> "</i>"
                Typeface.BOLD_ITALIC -> "</i></b>"
                else -> null
            }

            is RubySpan -> "<rt>${escapeHtml(span.rubyText)}</rt></ruby>"
            is UnderlineSpan -> "</u>"
            else -> null
        }
    }

    @OptIn(UnstableApi::class)
    private fun getTextEmphasisStyle(shape: Int, fill: Int): String {
        val builder = StringBuilder()
        when (fill) {
            TextEmphasisSpan.MARK_FILL_FILLED -> builder.append("filled ")
            TextEmphasisSpan.MARK_FILL_OPEN -> builder.append("open ")
        }
        when (shape) {
            TextEmphasisSpan.MARK_SHAPE_CIRCLE -> builder.append("circle")
            TextEmphasisSpan.MARK_SHAPE_DOT -> builder.append("dot")
            TextEmphasisSpan.MARK_SHAPE_SESAME -> builder.append("sesame")
            TextEmphasisSpan.MARK_SHAPE_NONE -> builder.append("none")
            else -> builder.append("unset")
        }
        return builder.toString()
    }

    @OptIn(UnstableApi::class)
    private fun getTextEmphasisPosition(position: Int): String {
        return if (position == TextAnnotation.POSITION_AFTER) "under left" else "over right"
    }

    private fun SparseArray<Transition>.getOrCreate(key: Int): Transition {
        var transition = this.get(key)
        if (transition == null) {
            transition = Transition()
            this.put(key, transition)
        }
        return transition
    }

    private fun escapeHtml(text: CharSequence) =
        NEWLINE_REGEX.replace(Html.escapeHtml(text), "<br>")

    data class HtmlAndCss(
        val html: String,
        val cssRuleSets: Map<String, String>
    )

    private data class SpanInfo(
        val start: Int,
        val end: Int,
        val openingTag: String,
        val closingTag: String
    ) {
        companion object {
            val FOR_OPENING_TAGS = compareByDescending<SpanInfo> { it.end }
                .thenBy { it.openingTag }
                .thenBy { it.closingTag }

            val FOR_CLOSING_TAGS = compareByDescending<SpanInfo> { it.start }
                .thenByDescending { it.openingTag }
                .thenByDescending { it.closingTag }
        }
    }

    private class Transition {
        val spansAdded = mutableListOf<SpanInfo>()
        val spansRemoved = mutableListOf<SpanInfo>()
    }
}
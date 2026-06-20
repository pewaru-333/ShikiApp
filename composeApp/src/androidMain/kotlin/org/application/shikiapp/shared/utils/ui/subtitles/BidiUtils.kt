package org.application.shikiapp.shared.utils.ui.subtitles

import android.text.BidiFormatter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextDirectionHeuristics

object BidiUtils {

    fun containsRtl(input: CharSequence?): Boolean {
        if (input == null) return false

        var offset = 0
        val length = input.length

        while (offset < length) {
            val codePoint = Character.codePointAt(input, offset)
            val dir = Character.getDirectionality(codePoint)

            if (dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC ||
                dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING ||
                dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
            ) {
                return true
            }
            offset += Character.charCount(codePoint)
        }
        return false
    }

    fun wrapText(input: CharSequence): CharSequence {
        val bidiFormatter = BidiFormatter.getInstance()

        val spannedInput = input as? Spanned
        val spans = spannedInput?.getSpans(0, input.length, Any::class.java)
        val spanStarts = spans?.let { IntArray(it.size) { -1 } }
        val spanEnds = spans?.let { IntArray(it.size) { -1 } }

        val isCrLf = input.contains("\r\n")
        val lines = input.split(if (isCrLf) "\r\n" else "\n")
        val eolLength = if (isCrLf) 2 else 1

        val wrappedLines = ArrayList<String>(lines.size)
        var spanUpdate = 0
        var lineStart = 0

        for (line in lines) {
            val wrappedLine = bidiFormatter.unicodeWrap(line, TextDirectionHeuristics.LTR)

            if (spannedInput != null && spans != null && spanStarts != null && spanEnds != null) {
                val diff = wrappedLine.length - line.length
                if (diff > 0) {
                    spanUpdate++
                }

                for (j in spans.indices) {
                    val spanStart = spannedInput.getSpanStart(spans[j])
                    val spanEnd = spannedInput.getSpanEnd(spans[j])

                    if (spanStarts[j] < 0 && spanStart >= lineStart && spanStart < lineStart + line.length) {
                        spanStarts[j] = spanUpdate
                    }
                    if (spanEnds[j] < 0 && (spanEnd - 1) >= lineStart && (spanEnd - 1) < lineStart + line.length) {
                        spanEnds[j] = spanUpdate
                    }
                }
                lineStart += line.length + eolLength
                if (diff > 0) {
                    spanUpdate++
                }
            }
            wrappedLines.add(wrappedLine)
        }

        val wrapped = SpannableStringBuilder(wrappedLines.joinToString("\n"))

        if (spannedInput != null && spans != null && spanStarts != null && spanEnds != null) {
            for (i in spans.indices) {
                val start = spannedInput.getSpanStart(spans[i]) + spanStarts[i]
                val end = spannedInput.getSpanEnd(spans[i]) + spanEnds[i]
                val flags = spannedInput.getSpanFlags(spans[i])

                if (start in 0..wrapped.length && end in 0..wrapped.length) {
                    wrapped.setSpan(spans[i], start, end, flags)
                }
            }
        }

        return wrapped
    }
}
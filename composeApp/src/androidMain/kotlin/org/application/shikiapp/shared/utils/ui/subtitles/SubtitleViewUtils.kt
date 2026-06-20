package org.application.shikiapp.shared.utils.ui.subtitles

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import androidx.annotation.OptIn
import androidx.media3.common.text.Cue
import androidx.media3.common.text.LanguageFeatureSpan
import androidx.media3.common.util.UnstableApi

object SubtitleViewUtils {

    fun resolveTextSize(
        @Cue.TextSizeType textSizeType: Int,
        textSize: Float,
        rawViewHeight: Int,
        viewHeightMinusPadding: Int
    ): Float {
        if (textSize == Cue.DIMEN_UNSET) {
            return Cue.DIMEN_UNSET
        }
        return when (textSizeType) {
            Cue.TEXT_SIZE_TYPE_ABSOLUTE -> textSize
            Cue.TEXT_SIZE_TYPE_FRACTIONAL -> textSize * viewHeightMinusPadding
            Cue.TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING -> textSize * rawViewHeight
            else -> Cue.DIMEN_UNSET
        }
    }

    @OptIn(UnstableApi::class)
    fun removeAllEmbeddedStyling(cue: Cue.Builder) {
        cue.clearWindowColor()
        val text = cue.text
        if (text is Spanned) {
            val spannable = text as? Spannable ?: SpannableString.valueOf(text).also { cue.setText(it) }
            spannable.removeSpansIf { it !is LanguageFeatureSpan }
        }
        removeEmbeddedFontSizes(cue)
    }

    @OptIn(UnstableApi::class)
    fun removeEmbeddedFontSizes(cue: Cue.Builder) {
        cue.setTextSize(Cue.DIMEN_UNSET, Cue.TYPE_UNSET)
        val text = cue.text
        if (text is Spanned) {
            val spannable = text as? Spannable ?: SpannableString.valueOf(text).also { cue.setText(it) }
            spannable.removeSpansIf { it is AbsoluteSizeSpan || it is RelativeSizeSpan }
        }
    }

    private inline fun Spannable.removeSpansIf(predicate: (Any) -> Boolean) {
        val spans = getSpans(0, length, Any::class.java)
        for (span in spans) {
            if (predicate(span)) {
                removeSpan(span)
            }
        }
    }
}
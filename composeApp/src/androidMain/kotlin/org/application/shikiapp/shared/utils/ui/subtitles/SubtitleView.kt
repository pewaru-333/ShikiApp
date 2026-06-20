package org.application.shikiapp.shared.utils.ui.subtitles

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.accessibility.CaptioningManager
import android.widget.FrameLayout
import androidx.annotation.Dimension
import androidx.annotation.IntDef
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi

@UnstableApi
class SubtitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    interface Output {
        fun update(
            cues: List<Cue>,
            style: CaptionStyleCompat,
            defaultTextSize: Float,
            @Cue.TextSizeType defaultTextSizeType: Int,
            bottomPaddingFraction: Float
        )
    }

    @MustBeDocumented
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @IntDef(VIEW_TYPE_CANVAS, VIEW_TYPE_WEB)
    annotation class ViewType

    private var output: Output
    private var innerSubtitleView: View

    var cues: List<Cue> = emptyList()
        set(value) {
            field = value
            updateOutput()
        }

    var style: CaptionStyleCompat = CaptionStyleCompat.DEFAULT
        set(value) {
            field = value
            updateOutput()
        }

    @Cue.TextSizeType
    var defaultTextSizeType: Int = Cue.TEXT_SIZE_TYPE_FRACTIONAL
        private set

    var defaultTextSize: Float = DEFAULT_TEXT_SIZE_FRACTION
        private set

    var bottomPaddingFraction: Float = DEFAULT_BOTTOM_PADDING_FRACTION
        set(value) {
            field = value
            updateOutput()
        }

    var applyEmbeddedStyles: Boolean = true
        set(value) {
            field = value
            updateOutput()
        }

    var applyEmbeddedFontSizes: Boolean = true
        set(value) {
            field = value
            updateOutput()
        }

    @ViewType
    var viewType: Int = VIEW_TYPE_CANVAS
        set(value) {
            if (field == value) return
            when (value) {
                VIEW_TYPE_CANVAS -> setView(CanvasSubtitleOutput(context))
                VIEW_TYPE_WEB -> setView(WebViewSubtitleOutput(context))
                else -> throw IllegalArgumentException()
            }
            field = value
        }

    init {
        val canvasSubtitleOutput = CanvasSubtitleOutput(context)
        output = canvasSubtitleOutput
        innerSubtitleView = canvasSubtitleOutput
        addView(innerSubtitleView)
    }

    private fun <T> setView(view: T) where T : View, T : Output {
        removeView(innerSubtitleView)
        (innerSubtitleView as? WebViewSubtitleOutput)?.destroy()
        innerSubtitleView = view
        output = view
        addView(view)
    }

    fun setFixedTextSize(@Dimension unit: Int, size: Float) {
        val resources = context?.resources ?: Resources.getSystem()
        setTextSize(
            Cue.TEXT_SIZE_TYPE_ABSOLUTE,
            TypedValue.applyDimension(unit, size, resources.displayMetrics)
        )
    }

    fun setUserDefaultTextSize() {
        setFractionalTextSize(DEFAULT_TEXT_SIZE_FRACTION * userCaptionFontScale)
    }

    @JvmOverloads
    fun setFractionalTextSize(fractionOfHeight: Float, ignorePadding: Boolean = false) {
        setTextSize(
            if (ignorePadding) Cue.TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING else Cue.TEXT_SIZE_TYPE_FRACTIONAL,
            fractionOfHeight
        )
    }

    private fun setTextSize(@Cue.TextSizeType textSizeType: Int, textSize: Float) {
        this.defaultTextSizeType = textSizeType
        this.defaultTextSize = textSize
        updateOutput()
    }

    fun setUserDefaultStyle() {
        style = userCaptionStyle
    }

    private val userCaptionFontScale: Float
        get() {
            if (isInEditMode) return 1f
            val captioningManager =
                context.getSystemService(Context.CAPTIONING_SERVICE) as? CaptioningManager
            return if (captioningManager?.isEnabled == true) captioningManager.fontScale else 1f
        }

    private val userCaptionStyle: CaptionStyleCompat
        get() {
            if (isInEditMode) return CaptionStyleCompat.DEFAULT
            val captioningManager =
                context.getSystemService(Context.CAPTIONING_SERVICE) as? CaptioningManager
            return if (captioningManager?.isEnabled == true) {
                CaptionStyleCompat.createFromCaptionStyle(captioningManager.userStyle)
            } else {
                CaptionStyleCompat.DEFAULT
            }
        }

    private fun updateOutput() {
        output.update(
            cuesWithStylingPreferencesApplied,
            style,
            defaultTextSize,
            defaultTextSizeType,
            bottomPaddingFraction
        )
    }

    private val cuesWithStylingPreferencesApplied: List<Cue>
        get() {
            if (applyEmbeddedStyles && applyEmbeddedFontSizes) return cues
            return cues.map { removeEmbeddedStyling(it) }
        }

    private fun removeEmbeddedStyling(cue: Cue): Cue {
        val strippedCue = cue.buildUpon()
        if (!applyEmbeddedStyles) {
            SubtitleViewUtils.removeAllEmbeddedStyling(strippedCue)
        } else if (!applyEmbeddedFontSizes) {
            SubtitleViewUtils.removeEmbeddedFontSizes(strippedCue)
        }
        return strippedCue.build()
    }

    companion object {
        const val DEFAULT_TEXT_SIZE_FRACTION = 0.0533f
        const val DEFAULT_BOTTOM_PADDING_FRACTION = 0.08f
        const val VIEW_TYPE_CANVAS = 1
        const val VIEW_TYPE_WEB = 2
    }
}
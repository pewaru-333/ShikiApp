package org.application.shikiapp.shared.utils.ui.subtitles

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView.Companion.DEFAULT_BOTTOM_PADDING_FRACTION
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView.Companion.DEFAULT_TEXT_SIZE_FRACTION

@UnstableApi
class CanvasSubtitleOutput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), SubtitleView.Output {

    private val painters = mutableListOf<SubtitlePainter>()
    private var cues: List<Cue> = emptyList()
    @Cue.TextSizeType
    private var textSizeType: Int = Cue.TEXT_SIZE_TYPE_FRACTIONAL
    private var textSize: Float = DEFAULT_TEXT_SIZE_FRACTION
    private var style: CaptionStyleCompat = CaptionStyleCompat.DEFAULT
    private var bottomPaddingFraction: Float = DEFAULT_BOTTOM_PADDING_FRACTION

    override fun update(
        cues: List<Cue>,
        style: CaptionStyleCompat,
        defaultTextSize: Float,
        @Cue.TextSizeType defaultTextSizeType: Int,
        bottomPaddingFraction: Float
    ) {
        this.cues = cues
        this.style = style
        this.textSize = defaultTextSize
        this.textSizeType = defaultTextSizeType
        this.bottomPaddingFraction = bottomPaddingFraction

        while (painters.size < cues.size) {
            painters.add(SubtitlePainter(context))
        }
        invalidate()
    }

    @OptIn(UnstableApi::class)
    override fun dispatchDraw(canvas: Canvas) {
        val currentCues = cues
        if (currentCues.isEmpty()) return

        val rawViewHeight = height
        val currentLeft = paddingLeft
        val currentTop = paddingTop
        val currentRight = width - paddingRight
        val currentBottom = rawViewHeight - paddingBottom

        if (currentBottom <= currentTop || currentRight <= currentLeft) return

        val viewHeightMinusPadding = currentBottom - currentTop
        val defaultViewTextSizePx = SubtitleViewUtils.resolveTextSize(
            textSizeType, textSize, rawViewHeight, viewHeightMinusPadding
        )

        if (defaultViewTextSizePx <= 0) return

        currentCues.forEachIndexed { index, originalCue ->
            val cue = if (originalCue.verticalType != Cue.TYPE_UNSET) {
                repositionVerticalCue(originalCue)
            } else {
                originalCue
            }

            val cueTextSizePx = SubtitleViewUtils.resolveTextSize(
                cue.textSizeType, cue.textSize, rawViewHeight, viewHeightMinusPadding
            )

            val painter = painters[index]
            painter.draw(
                cue = cue,
                style = style,
                defaultTextSizePx = defaultViewTextSizePx,
                cueTextSizePx = cueTextSizePx,
                bottomPaddingFraction = bottomPaddingFraction,
                canvas = canvas,
                cueBoxLeft = currentLeft,
                cueBoxTop = currentTop,
                cueBoxRight = currentRight,
                cueBoxBottom = currentBottom
            )
        }
    }

    companion object {
        @OptIn(UnstableApi::class)
        private fun repositionVerticalCue(cue: Cue) = cue.buildUpon()
            .setPosition(Cue.DIMEN_UNSET)
            .setPositionAnchor(Cue.TYPE_UNSET)
            .setTextAlignment(null)
            .apply {
                if (cue.lineType == Cue.LINE_TYPE_FRACTION) {
                    setLine(1.0f - cue.line, Cue.LINE_TYPE_FRACTION)
                } else {
                    setLine(-cue.line - 1f, Cue.LINE_TYPE_NUMBER)
                }

                when (cue.lineAnchor) {
                    Cue.ANCHOR_TYPE_END -> lineAnchor = Cue.ANCHOR_TYPE_START
                    Cue.ANCHOR_TYPE_START -> lineAnchor = Cue.ANCHOR_TYPE_END
                }
            }
            .build()
    }
}
package org.application.shikiapp.shared.utils.ui.subtitles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout.Alignment
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import androidx.annotation.OptIn
import androidx.core.graphics.withTranslation
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ResourceType")
class SubtitlePainter(context: Context) {

    private val outlineWidth: Float
    private val shadowRadius: Float
    private val shadowOffset: Float
    private val spacingMult: Float
    private val spacingAdd: Float

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val windowPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private var cueText: CharSequence? = null
    private var cueTextAlignment: Alignment? = null
    private var cueBitmap: Bitmap? = null
    private var cueLine: Float = 0f
    @Cue.LineType
    private var cueLineType: Int = 0
    @Cue.AnchorType
    private var cueLineAnchor: Int = 0
    private var cuePosition: Float = 0f
    @Cue.AnchorType
    private var cuePositionAnchor: Int = 0
    private var cueSize: Float = 0f
    private var cueBitmapHeight: Float = 0f
    private var foregroundColor: Int = 0
    private var backgroundColor: Int = 0
    private var windowColor: Int = 0
    private var edgeColor: Int = 0
    @CaptionStyleCompat.EdgeType
    private var edgeType: Int = 0
    private var defaultTextSizePx: Float = 0f
    private var cueTextSizePx: Float = 0f
    private var bottomPaddingFraction: Float = 0f
    private var parentLeft: Int = 0
    private var parentTop: Int = 0
    private var parentRight: Int = 0
    private var parentBottom: Int = 0

    private var textLayout: StaticLayout? = null
    private var edgeLayout: StaticLayout? = null
    private var textLeft: Int = 0
    private var textTop: Int = 0
    private var textPaddingX: Int = 0
    private var bitmapRect: Rect? = null

    init {
        val viewAttr = intArrayOf(android.R.attr.lineSpacingExtra, android.R.attr.lineSpacingMultiplier)
        val styledAttributes = context.obtainStyledAttributes(null, viewAttr, 0, 0)
        try {
            spacingAdd = styledAttributes.getDimensionPixelSize(0, 0).toFloat()
            spacingMult = styledAttributes.getFloat(1, 1f)
        } finally {
            styledAttributes.recycle()
        }

        val displayMetrics = context.resources.displayMetrics
        val twoDpInPx =
            ((2f * displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT).roundToInt()
                .toFloat()
        outlineWidth = twoDpInPx
        shadowRadius = twoDpInPx
        shadowOffset = twoDpInPx
    }

    @OptIn(UnstableApi::class)
    fun draw(
        cue: Cue,
        style: CaptionStyleCompat,
        defaultTextSizePx: Float,
        cueTextSizePx: Float,
        bottomPaddingFraction: Float,
        canvas: Canvas,
        cueBoxLeft: Int,
        cueBoxTop: Int,
        cueBoxRight: Int,
        cueBoxBottom: Int
    ) {
        val isTextCue = cue.bitmap == null
        var currentWindowColor = Color.BLACK

        if (isTextCue) {
            if (TextUtils.isEmpty(cue.text)) return
            currentWindowColor = if (cue.windowColorSet) cue.windowColor else style.windowColor
        }

        if (this.cueText == cue.text &&
            this.cueTextAlignment == cue.textAlignment &&
            this.cueBitmap == cue.bitmap &&
            this.cueLine == cue.line &&
            this.cueLineType == cue.lineType &&
            this.cueLineAnchor == cue.lineAnchor &&
            this.cuePosition == cue.position &&
            this.cuePositionAnchor == cue.positionAnchor &&
            this.cueSize == cue.size &&
            this.cueBitmapHeight == cue.bitmapHeight &&
            this.foregroundColor == style.foregroundColor &&
            this.backgroundColor == style.backgroundColor &&
            this.windowColor == currentWindowColor &&
            this.edgeType == style.edgeType &&
            this.edgeColor == style.edgeColor &&
            this.textPaint.typeface == style.typeface &&
            this.defaultTextSizePx == defaultTextSizePx &&
            this.cueTextSizePx == cueTextSizePx &&
            this.bottomPaddingFraction == bottomPaddingFraction &&
            this.parentLeft == cueBoxLeft &&
            this.parentTop == cueBoxTop &&
            this.parentRight == cueBoxRight &&
            this.parentBottom == cueBoxBottom
        ) {
            drawLayout(canvas, isTextCue)
            return
        }

        this.cueText = if (BidiUtils.containsRtl(cue.text)) BidiUtils.wrapText(cue.text!!) else cue.text
        this.cueTextAlignment = cue.textAlignment
        this.cueBitmap = cue.bitmap
        this.cueLine = cue.line
        this.cueLineType = cue.lineType
        this.cueLineAnchor = cue.lineAnchor
        this.cuePosition = cue.position
        this.cuePositionAnchor = cue.positionAnchor
        this.cueSize = cue.size
        this.cueBitmapHeight = cue.bitmapHeight
        this.foregroundColor = style.foregroundColor
        this.backgroundColor = style.backgroundColor
        this.windowColor = currentWindowColor
        this.edgeType = style.edgeType
        this.edgeColor = style.edgeColor
        this.textPaint.typeface = style.typeface
        this.defaultTextSizePx = defaultTextSizePx
        this.cueTextSizePx = cueTextSizePx
        this.bottomPaddingFraction = bottomPaddingFraction
        this.parentLeft = cueBoxLeft
        this.parentTop = cueBoxTop
        this.parentRight = cueBoxRight
        this.parentBottom = cueBoxBottom

        if (isTextCue) {
            setupTextLayout()
        } else {
            setupBitmapLayout()
        }
        drawLayout(canvas, isTextCue)
    }

    @OptIn(UnstableApi::class)
    private fun setupTextLayout() {
        val currentCueText = this.cueText ?: return
        val cueTextBuilder = currentCueText as? SpannableStringBuilder ?: SpannableStringBuilder(currentCueText)
        val parentWidth = parentRight - parentLeft
        val parentHeight = parentBottom - parentTop

        textPaint.textSize = defaultTextSizePx
        val textPaddingX = (defaultTextSizePx * INNER_PADDING_RATIO).roundToInt()

        var availableWidth = parentWidth - textPaddingX * 2
        if (cueSize != Cue.DIMEN_UNSET) {
            availableWidth = (availableWidth * cueSize).toInt()
        }
        if (availableWidth <= 0) return

        if (cueTextSizePx > 0) {
            cueTextBuilder.setSpan(
                AbsoluteSizeSpan(cueTextSizePx.toInt()),
                0,
                cueTextBuilder.length,
                Spanned.SPAN_PRIORITY
            )
        }

        val cueTextEdge = SpannableStringBuilder(cueTextBuilder)
        if (edgeType == CaptionStyleCompat.EDGE_TYPE_OUTLINE) {
            val foregroundColorSpans =
                cueTextEdge.getSpans(0, cueTextEdge.length, ForegroundColorSpan::class.java)
            for (span in foregroundColorSpans) {
                cueTextEdge.removeSpan(span)
            }
        }

        if (Color.alpha(backgroundColor) > 0) {
            if (edgeType == CaptionStyleCompat.EDGE_TYPE_NONE || edgeType == CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW) {
                cueTextBuilder.setSpan(
                    BackgroundColorSpan(backgroundColor),
                    0,
                    cueTextBuilder.length,
                    Spanned.SPAN_PRIORITY
                )
            } else {
                cueTextEdge.setSpan(
                    BackgroundColorSpan(backgroundColor),
                    0,
                    cueTextEdge.length,
                    Spanned.SPAN_PRIORITY
                )
            }
        }

        val textAlignment = cueTextAlignment ?: Alignment.ALIGN_CENTER
        val tempTextLayout = StaticLayout.Builder.obtain(cueTextBuilder, 0, cueTextBuilder.length, textPaint, availableWidth)
            .setAlignment(textAlignment)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(true)
            .build()

        val textHeight = tempTextLayout.height
        var textWidth = 0
        for (i in 0 until tempTextLayout.lineCount) {
            textWidth = max(ceil(tempTextLayout.getLineWidth(i)).toInt(), textWidth)
        }
        if (cueSize != Cue.DIMEN_UNSET && textWidth < availableWidth) {
            textWidth = availableWidth
        }
        textWidth += textPaddingX * 2

        var calcTextLeft: Int
        val calcTextRight: Int

        if (cuePosition != Cue.DIMEN_UNSET) {
            val anchorPosition = (parentWidth * cuePosition).roundToInt() + parentLeft
            calcTextLeft = when (cuePositionAnchor) {
                Cue.ANCHOR_TYPE_END -> anchorPosition - textWidth
                Cue.ANCHOR_TYPE_MIDDLE -> (anchorPosition * 2 - textWidth) / 2
                else -> anchorPosition
            }
            calcTextLeft = max(calcTextLeft, parentLeft)
            calcTextRight = min(calcTextLeft + textWidth, parentRight)
        } else {
            calcTextLeft = (parentWidth - textWidth) / 2 + parentLeft
            calcTextRight = calcTextLeft + textWidth
        }

        textWidth = calcTextRight - calcTextLeft
        if (textWidth <= 0) return

        var calcTextTop: Int
        if (cueLine != Cue.DIMEN_UNSET) {
            if (cueLineType == Cue.LINE_TYPE_FRACTION) {
                val anchorPosition = (parentHeight * cueLine).roundToInt() + parentTop
                calcTextTop = when (cueLineAnchor) {
                    Cue.ANCHOR_TYPE_END -> anchorPosition - textHeight
                    Cue.ANCHOR_TYPE_MIDDLE -> (anchorPosition * 2 - textHeight) / 2
                    else -> anchorPosition
                }
            } else {
                val firstLineHeight = tempTextLayout.getLineBottom(0) - tempTextLayout.getLineTop(0)
                calcTextTop = if (cueLine >= 0) {
                    (cueLine * firstLineHeight).roundToInt() + parentTop
                } else {
                    ((cueLine + 1) * firstLineHeight).roundToInt() + parentBottom - textHeight
                }
            }

            if (calcTextTop + textHeight > parentBottom) {
                calcTextTop = parentBottom - textHeight
            } else if (calcTextTop < parentTop) {
                calcTextTop = parentTop
            }
        } else {
            calcTextTop = parentBottom - textHeight - (parentHeight * bottomPaddingFraction).toInt()
        }

        this.textLayout = StaticLayout.Builder.obtain(cueTextBuilder, 0, cueTextBuilder.length, textPaint, textWidth)
            .setAlignment(textAlignment)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(true)
            .build()

        this.edgeLayout = StaticLayout.Builder.obtain(cueTextEdge, 0, cueTextEdge.length, textPaint, textWidth)
            .setAlignment(textAlignment)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(true)
            .build()

        this.textLeft = calcTextLeft
        this.textTop = calcTextTop
        this.textPaddingX = textPaddingX
    }

    private fun setupBitmapLayout() {
        val bitmap = this.cueBitmap ?: return
        val parentWidth = parentRight - parentLeft
        val parentHeight = parentBottom - parentTop
        val anchorX = parentLeft + (parentWidth * cuePosition)
        val anchorY = parentTop + (parentHeight * cueLine)

        val width = (parentWidth * cueSize).roundToInt()
        val height = if (cueBitmapHeight != Cue.DIMEN_UNSET) {
            (parentHeight * cueBitmapHeight).roundToInt()
        } else {
            (width * (bitmap.height.toFloat() / bitmap.width)).roundToInt()
        }

        val x = when (cuePositionAnchor) {
            Cue.ANCHOR_TYPE_END -> (anchorX - width).roundToInt()
            Cue.ANCHOR_TYPE_MIDDLE -> (anchorX - (width / 2f)).roundToInt()
            else -> anchorX.roundToInt()
        }

        val y = when (cueLineAnchor) {
            Cue.ANCHOR_TYPE_END -> (anchorY - height).roundToInt()
            Cue.ANCHOR_TYPE_MIDDLE -> (anchorY - (height / 2f)).roundToInt()
            else -> anchorY.roundToInt()
        }

        bitmapRect = Rect(x, y, x + width, y + height)
    }

    private fun drawLayout(canvas: Canvas, isTextCue: Boolean) {
        if (isTextCue) {
            drawTextLayout(canvas)
        } else {
            drawBitmapLayout(canvas)
        }
    }

    @OptIn(UnstableApi::class)
    private fun drawTextLayout(canvas: Canvas) {
        val currentTextLayout = textLayout ?: return
        val currentEdgeLayout = edgeLayout ?: return

        canvas.withTranslation(textLeft.toFloat(), textTop.toFloat()) {
            if (Color.alpha(windowColor) > 0) {
                windowPaint.color = windowColor
                canvas.drawRect(
                    -textPaddingX.toFloat(),
                    0f,
                    (currentTextLayout.width + textPaddingX).toFloat(),
                    currentTextLayout.height.toFloat(),
                    windowPaint
                )
            }

            when (edgeType) {
                CaptionStyleCompat.EDGE_TYPE_OUTLINE -> {
                    textPaint.strokeJoin = Paint.Join.ROUND
                    textPaint.strokeWidth = outlineWidth
                    textPaint.color = edgeColor
                    textPaint.style = Paint.Style.FILL_AND_STROKE
                    currentEdgeLayout.draw(canvas)
                }

                CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW -> {
                    textPaint.setShadowLayer(shadowRadius, shadowOffset, shadowOffset, edgeColor)
                }

                CaptionStyleCompat.EDGE_TYPE_RAISED, CaptionStyleCompat.EDGE_TYPE_DEPRESSED -> {
                    val raised = edgeType == CaptionStyleCompat.EDGE_TYPE_RAISED
                    val colorUp = if (raised) Color.WHITE else edgeColor
                    val colorDown = if (raised) edgeColor else Color.WHITE
                    val offset = shadowRadius / 2f

                    textPaint.color = foregroundColor
                    textPaint.style = Paint.Style.FILL
                    textPaint.setShadowLayer(shadowRadius, -offset, -offset, colorUp)
                    currentEdgeLayout.draw(canvas)
                    textPaint.setShadowLayer(shadowRadius, offset, offset, colorDown)
                }
            }

            textPaint.color = foregroundColor
            textPaint.style = Paint.Style.FILL
            currentTextLayout.draw(canvas)
            textPaint.setShadowLayer(0f, 0f, 0f, 0)

        }
    }

    private fun drawBitmapLayout(canvas: Canvas) {
        val bitmap = cueBitmap ?: return
        val rect = bitmapRect ?: return
        canvas.drawBitmap(bitmap, null, rect, bitmapPaint)
    }

    companion object {
        private const val INNER_PADDING_RATIO = 0.125f
    }
}
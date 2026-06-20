package org.application.shikiapp.shared.utils.ui.subtitles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView.Companion.DEFAULT_BOTTOM_PADDING_FRACTION
import org.application.shikiapp.shared.utils.ui.subtitles.SubtitleView.Companion.DEFAULT_TEXT_SIZE_FRACTION
import java.nio.charset.StandardCharsets

@UnstableApi
class WebViewSubtitleOutput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), SubtitleView.Output {

    private val canvasSubtitleOutput: CanvasSubtitleOutput = CanvasSubtitleOutput(context, attrs)
    private val webView = object : WebView(context, attrs) {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            super.onTouchEvent(event)
            return false
        }

        override fun performClick(): Boolean {
            super.performClick()
            return false
        }
    }.apply {
        setBackgroundColor(Color.TRANSPARENT)
        settings.allowContentAccess = false
    }

    private var textCues: List<Cue> = emptyList()
    private var style: CaptionStyleCompat = CaptionStyleCompat.DEFAULT
    private var defaultTextSize: Float = DEFAULT_TEXT_SIZE_FRACTION
    @Cue.TextSizeType
    private var defaultTextSizeType: Int = Cue.TEXT_SIZE_TYPE_FRACTIONAL
    private var bottomPaddingFraction: Float = DEFAULT_BOTTOM_PADDING_FRACTION

    init {
        addView(canvasSubtitleOutput)
        addView(webView)
    }

    override fun update(
        cues: List<Cue>,
        style: CaptionStyleCompat,
        defaultTextSize: Float,
        @Cue.TextSizeType defaultTextSizeType: Int,
        bottomPaddingFraction: Float
    ) {
        this.style = style
        this.defaultTextSize = defaultTextSize
        this.defaultTextSizeType = defaultTextSizeType
        this.bottomPaddingFraction = bottomPaddingFraction

        val (bitmapCues, newTextCues) = cues.partition { it.bitmap != null }

        if (this.textCues.isNotEmpty() || newTextCues.isNotEmpty()) {
            this.textCues = newTextCues
            updateWebView()
        }
        canvasSubtitleOutput.update(
            bitmapCues,
            style,
            defaultTextSize,
            defaultTextSizeType,
            bottomPaddingFraction
        )
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed && textCues.isNotEmpty()) {
            updateWebView()
        }
    }

    fun destroy() {
        webView.destroy()
    }

    @OptIn(UnstableApi::class)
    private fun updateWebView() {
        val html = StringBuilder()
        html.append(
            Util.formatInvariant(
                "<body><div style='-webkit-user-select:none;position:fixed;top:0;bottom:0;left:0;right:0;color:%s;font-size:%s;line-height:%.2f;text-shadow:%s;'>",
                HtmlUtils.toCssRgba(style.foregroundColor),
                convertTextSizeToCss(defaultTextSizeType, defaultTextSize),
                CSS_LINE_HEIGHT,
                convertCaptionStyleToCssTextShadow(style)
            )
        )

        val cssRuleSets = mutableMapOf<String, String>()
        cssRuleSets[HtmlUtils.cssAllClassDescendantsSelector(DEFAULT_BACKGROUND_CSS_CLASS)] =
            Util.formatInvariant("background-color:%s;", HtmlUtils.toCssRgba(style.backgroundColor))

        for (i in textCues.indices) {
            val cue = textCues[i]
            val positionPercent = if (cue.position != Cue.DIMEN_UNSET) cue.position * 100 else 50f
            val positionAnchorTranslatePercent = anchorTypeToTranslatePercent(cue.positionAnchor)

            val lineValue: String
            var lineMeasuredFromEnd = false
            var lineAnchorTranslatePercent = 0

            if (cue.line != Cue.DIMEN_UNSET) {
                when (cue.lineType) {
                    Cue.LINE_TYPE_NUMBER -> {
                        if (cue.line >= 0) {
                            lineValue = Util.formatInvariant("%.2fem", cue.line * CSS_LINE_HEIGHT)
                        } else {
                            lineValue =
                                Util.formatInvariant("%.2fem", (-cue.line - 1) * CSS_LINE_HEIGHT)
                            lineMeasuredFromEnd = true
                        }
                    }

                    Cue.LINE_TYPE_FRACTION, Cue.TYPE_UNSET -> {
                        lineValue = Util.formatInvariant("%.2f%%", cue.line * 100)
                        lineAnchorTranslatePercent = if (cue.verticalType == Cue.VERTICAL_TYPE_RL) {
                            -anchorTypeToTranslatePercent(cue.lineAnchor)
                        } else {
                            anchorTypeToTranslatePercent(cue.lineAnchor)
                        }
                    }

                    else -> {
                        lineValue = Util.formatInvariant("%.2f%%", cue.line * 100)
                        lineAnchorTranslatePercent = if (cue.verticalType == Cue.VERTICAL_TYPE_RL) {
                            -anchorTypeToTranslatePercent(cue.lineAnchor)
                        } else {
                            anchorTypeToTranslatePercent(cue.lineAnchor)
                        }
                    }
                }
            } else {
                lineValue = Util.formatInvariant("%.2f%%", (1.0f - bottomPaddingFraction) * 100)
                lineAnchorTranslatePercent = -100
            }

            val size = if (cue.size != Cue.DIMEN_UNSET) {
                Util.formatInvariant("%.2f%%", cue.size * 100)
            } else {
                "fit-content"
            }

            val textAlign = convertAlignmentToCss(cue.textAlignment)
            val writingMode = convertVerticalTypeToCss(cue.verticalType)
            val cueTextSizeCssPx = convertTextSizeToCss(cue.textSizeType, cue.textSize)
            val windowCssColor =
                HtmlUtils.toCssRgba(if (cue.windowColorSet) cue.windowColor else style.windowColor)

            val positionProperty: String
            val lineProperty: String
            when (cue.verticalType) {
                Cue.VERTICAL_TYPE_LR -> {
                    lineProperty = if (lineMeasuredFromEnd) "right" else "left"
                    positionProperty = "top"
                }

                Cue.VERTICAL_TYPE_RL -> {
                    lineProperty = if (lineMeasuredFromEnd) "left" else "right"
                    positionProperty = "top"
                }

                else -> {
                    lineProperty = if (lineMeasuredFromEnd) "bottom" else "top"
                    positionProperty = "left"
                }
            }

            val sizeProperty: String
            val horizontalTranslatePercent: Int
            val verticalTranslatePercent: Int
            if (cue.verticalType == Cue.VERTICAL_TYPE_LR || cue.verticalType == Cue.VERTICAL_TYPE_RL) {
                sizeProperty = "height"
                horizontalTranslatePercent = lineAnchorTranslatePercent
                verticalTranslatePercent = positionAnchorTranslatePercent
            } else {
                sizeProperty = "width"
                horizontalTranslatePercent = positionAnchorTranslatePercent
                verticalTranslatePercent = lineAnchorTranslatePercent
            }

            val htmlAndCss =
                SpannedToHtmlConverter.convert(cue.text, context.resources.displayMetrics.density)

            for ((cssSelector, cssDeclaration) in htmlAndCss.cssRuleSets) {
                val previousCssDeclarationBlock = cssRuleSets.put(cssSelector, cssDeclaration)
                check(previousCssDeclarationBlock == null || previousCssDeclarationBlock == cssDeclaration)
            }

            html.append(
                Util.formatInvariant(
                    "<div style='position:absolute;z-index:%s;%s:%.2f%%;%s:%s;%s:%s;text-align:%s;writing-mode:%s;font-size:%s;background-color:%s;transform:translate(%s%%,%s%%)%s;'>",
                    i,
                    positionProperty,
                    positionPercent,
                    lineProperty,
                    lineValue,
                    sizeProperty,
                    size,
                    textAlign,
                    writingMode,
                    cueTextSizeCssPx,
                    windowCssColor,
                    horizontalTranslatePercent,
                    verticalTranslatePercent,
                    getBlockShearTransformFunction(cue)
                )
            ).append(Util.formatInvariant("<span class='%s'>", DEFAULT_BACKGROUND_CSS_CLASS))

            if (cue.multiRowAlignment != null) {
                html.append(
                    Util.formatInvariant(
                        "<span style='display:inline-block; text-align:%s;'>",
                        convertAlignmentToCss(cue.multiRowAlignment)
                    )
                ).append(htmlAndCss.html).append("</span>")
            } else {
                html.append(htmlAndCss.html)
            }

            html.append("</span></div>")
        }

        html.append("</div></body></html>")

        val htmlHead = StringBuilder()
        htmlHead.append("<html><head><style>")
        for ((cssSelector, cssDeclaration) in cssRuleSets) {
            htmlHead.append(cssSelector).append("{").append(cssDeclaration).append("}")
        }
        htmlHead.append("</style></head>")
        html.insert(0, htmlHead)

        webView.loadData(
            Base64.encodeToString(
                html.toString().toByteArray(StandardCharsets.UTF_8),
                Base64.NO_PADDING
            ),
            "text/html",
            "base64"
        )
    }

    @OptIn(UnstableApi::class)
    private fun convertTextSizeToCss(@Cue.TextSizeType type: Int, size: Float): String {
        val sizePx = SubtitleViewUtils.resolveTextSize(type, size, height, height - paddingTop - paddingBottom)
        if (sizePx == Cue.DIMEN_UNSET) {
            return "unset"
        }
        val sizeDp = sizePx / context.resources.displayMetrics.density
        return Util.formatInvariant("%.2fpx", sizeDp)
    }

    companion object {
        private const val CSS_LINE_HEIGHT = 1.2f
        private const val DEFAULT_BACKGROUND_CSS_CLASS = "default_bg"

        @OptIn(UnstableApi::class)
        private fun getBlockShearTransformFunction(cue: Cue): String {
            if (cue.shearDegrees != 0.0f) {
                val direction =
                    if (cue.verticalType == Cue.VERTICAL_TYPE_LR || cue.verticalType == Cue.VERTICAL_TYPE_RL) {
                        "skewY"
                    } else {
                        "skewX"
                    }
                return Util.formatInvariant("%s(%.2fdeg)", direction, cue.shearDegrees)
            }
            return ""
        }

        private fun convertCaptionStyleToCssTextShadow(style: CaptionStyleCompat) =
            when (style.edgeType) {
                CaptionStyleCompat.EDGE_TYPE_DEPRESSED -> Util.formatInvariant(
                    "-0.05em -0.05em 0.15em %s", HtmlUtils.toCssRgba(style.edgeColor)
                )

                CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW -> Util.formatInvariant(
                    "0.1em 0.12em 0.15em %s", HtmlUtils.toCssRgba(style.edgeColor)
                )

                CaptionStyleCompat.EDGE_TYPE_OUTLINE -> Util.formatInvariant(
                    "1px 1px 0 %1\$s, 1px -1px 0 %1\$s, -1px 1px 0 %1\$s, -1px -1px 0 %1\$s",
                    HtmlUtils.toCssRgba(style.edgeColor)
                )

                CaptionStyleCompat.EDGE_TYPE_RAISED -> Util.formatInvariant(
                    "0.06em 0.08em 0.15em %s", HtmlUtils.toCssRgba(style.edgeColor)
                )

                else -> "unset"
            }

        @OptIn(UnstableApi::class)
        private fun convertVerticalTypeToCss(@Cue.VerticalType verticalType: Int) =
            when (verticalType) {
                Cue.VERTICAL_TYPE_LR -> "vertical-lr"
                Cue.VERTICAL_TYPE_RL -> "vertical-rl"
                else -> "horizontal-tb"
            }

        private fun convertAlignmentToCss(alignment: Layout.Alignment?) = when (alignment) {
            Layout.Alignment.ALIGN_NORMAL -> "start"
            Layout.Alignment.ALIGN_OPPOSITE -> "end"
            else -> "center"
        }

        @OptIn(UnstableApi::class)
        private fun anchorTypeToTranslatePercent(@Cue.AnchorType anchorType: Int) =
            when (anchorType) {
                Cue.ANCHOR_TYPE_END -> -100
                Cue.ANCHOR_TYPE_MIDDLE -> -50
                else -> 0
            }
    }
}
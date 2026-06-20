package org.application.shikiapp.shared.utils.ui.subtitles

import android.graphics.Color
import android.graphics.Typeface
import android.view.accessibility.CaptioningManager
import androidx.annotation.IntDef
import androidx.media3.common.util.UnstableApi

@UnstableApi
data class CaptionStyleCompat(
    val foregroundColor: Int,
    val backgroundColor: Int,
    val windowColor: Int,
    @EdgeType val edgeType: Int,
    val edgeColor: Int,
    val typeface: Typeface?
) {

    @MustBeDocumented
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @IntDef(
        EDGE_TYPE_NONE,
        EDGE_TYPE_OUTLINE,
        EDGE_TYPE_DROP_SHADOW,
        EDGE_TYPE_RAISED,
        EDGE_TYPE_DEPRESSED
    )
    annotation class EdgeType

    companion object {
        const val EDGE_TYPE_NONE = 0
        const val EDGE_TYPE_OUTLINE = 1
        const val EDGE_TYPE_DROP_SHADOW = 2
        const val EDGE_TYPE_RAISED = 3
        const val EDGE_TYPE_DEPRESSED = 4

        const val USE_TRACK_COLOR_SETTINGS = 1

        val DEFAULT = CaptionStyleCompat(
            foregroundColor = Color.WHITE,
            backgroundColor = Color.BLACK,
            windowColor = Color.TRANSPARENT,
            edgeType = EDGE_TYPE_NONE,
            edgeColor = Color.WHITE,
            typeface = null
        )

        fun createFromCaptionStyle(captionStyle: CaptioningManager.CaptionStyle) =
            CaptionStyleCompat(
                foregroundColor = if (captionStyle.hasForegroundColor()) captionStyle.foregroundColor else DEFAULT.foregroundColor,
                backgroundColor = if (captionStyle.hasBackgroundColor()) captionStyle.backgroundColor else DEFAULT.backgroundColor,
                windowColor = if (captionStyle.hasWindowColor()) captionStyle.windowColor else DEFAULT.windowColor,
                edgeType = if (captionStyle.hasEdgeType()) captionStyle.edgeType else DEFAULT.edgeType,
                edgeColor = if (captionStyle.hasEdgeColor()) captionStyle.edgeColor else DEFAULT.edgeColor,
                typeface = captionStyle.typeface
            )
    }
}
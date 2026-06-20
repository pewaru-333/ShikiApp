package org.application.shikiapp.shared.utils.ui.subtitles

import android.graphics.Color
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util

object HtmlUtils {
    @UnstableApi
    fun toCssRgba(color: Int) = Util.formatInvariant(
        "rgba(%d,%d,%d,%.3f)",
        Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color) / 255.0
    )

    fun cssAllClassDescendantsSelector(className: String) = ".$className,.$className *"
}
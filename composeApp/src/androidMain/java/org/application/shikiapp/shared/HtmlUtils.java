package org.application.shikiapp.shared;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;

final class HtmlUtils {

    private HtmlUtils() {
    }

    @OptIn(markerClass = UnstableApi.class)
    public static String toCssRgba(@ColorInt int color) {
        return Util.formatInvariant(
                "rgba(%d,%d,%d,%.3f)",
                Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color) / 255.0);
    }

    public static String cssAllClassDescendantsSelector(String className) {
        return "." + className + ",." + className + " *";
    }
}
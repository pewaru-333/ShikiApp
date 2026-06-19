package org.application.shikiapp.shared;

import static java.lang.annotation.ElementType.TYPE_USE;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.accessibility.CaptioningManager;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UnstableApi
public record CaptionStyleCompat(
        int foregroundColor,
        int backgroundColor,
        int windowColor,
        @EdgeType int edgeType, int edgeColor,
        @Nullable Typeface typeface
) {

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(TYPE_USE)
    @IntDef({EDGE_TYPE_NONE, EDGE_TYPE_OUTLINE, EDGE_TYPE_DROP_SHADOW, EDGE_TYPE_RAISED, EDGE_TYPE_DEPRESSED})
    public @interface EdgeType {
    }

    public static final int EDGE_TYPE_NONE = 0;

    public static final int EDGE_TYPE_OUTLINE = 1;

    public static final int EDGE_TYPE_DROP_SHADOW = 2;

    public static final int EDGE_TYPE_RAISED = 3;

    public static final int EDGE_TYPE_DEPRESSED = 4;

    public static final int USE_TRACK_COLOR_SETTINGS = 1;

    public static final CaptionStyleCompat DEFAULT = new CaptionStyleCompat(
            Color.WHITE,
            Color.BLACK,
            Color.TRANSPARENT,
            EDGE_TYPE_NONE,
            Color.WHITE,
            null
    );

    public static CaptionStyleCompat createFromCaptionStyle(
            CaptioningManager.CaptionStyle captionStyle) {
        return new CaptionStyleCompat(
                captionStyle.hasForegroundColor() ? captionStyle.foregroundColor : DEFAULT.foregroundColor,
                captionStyle.hasBackgroundColor() ? captionStyle.backgroundColor : DEFAULT.backgroundColor,
                captionStyle.hasWindowColor() ? captionStyle.windowColor : DEFAULT.windowColor,
                captionStyle.hasEdgeType() ? captionStyle.edgeType : DEFAULT.edgeType,
                captionStyle.hasEdgeColor() ? captionStyle.edgeColor : DEFAULT.edgeColor,
                captionStyle.getTypeface());
    }
}
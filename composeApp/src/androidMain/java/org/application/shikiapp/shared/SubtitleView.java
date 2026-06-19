package org.application.shikiapp.shared;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.FrameLayout;

import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.media3.common.text.Cue;
import androidx.media3.common.util.UnstableApi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UnstableApi
public final class SubtitleView extends FrameLayout {

  public interface Output {

    void update(
            List<Cue> cues,
            CaptionStyleCompat style,
            float defaultTextSize,
            @Cue.TextSizeType int defaultTextSizeType,
            float bottomPaddingFraction);
  }

  public static final float DEFAULT_TEXT_SIZE_FRACTION = 0.0533f;

  public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08f;

  public static final int VIEW_TYPE_CANVAS = 1;

  public static final int VIEW_TYPE_WEB = 2;

  @Documented
  @Retention(SOURCE)
  @Target(TYPE_USE)
  @IntDef({VIEW_TYPE_CANVAS, VIEW_TYPE_WEB})
  public @interface ViewType {}

  private List<Cue> cues;
  private CaptionStyleCompat style;
  private @Cue.TextSizeType int defaultTextSizeType;
  private float defaultTextSize;
  private float bottomPaddingFraction;
  private boolean applyEmbeddedStyles;
  private boolean applyEmbeddedFontSizes;

  private @ViewType int viewType;
  private Output output;
  private View innerSubtitleView;

  public SubtitleView(Context context) {
    this(context, null);
  }

  public SubtitleView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    cues = Collections.emptyList();
    style = CaptionStyleCompat.DEFAULT;
    defaultTextSizeType = Cue.TEXT_SIZE_TYPE_FRACTIONAL;
    defaultTextSize = DEFAULT_TEXT_SIZE_FRACTION;
    bottomPaddingFraction = DEFAULT_BOTTOM_PADDING_FRACTION;
    applyEmbeddedStyles = true;
    applyEmbeddedFontSizes = true;

    CanvasSubtitleOutput canvasSubtitleOutput = new CanvasSubtitleOutput(context);
    output = canvasSubtitleOutput;
    innerSubtitleView = canvasSubtitleOutput;
    addView(innerSubtitleView);
    viewType = VIEW_TYPE_CANVAS;
  }

  public void setCues(@Nullable List<Cue> cues) {
    this.cues = (cues != null ? cues : Collections.emptyList());
    updateOutput();
  }

  public void setViewType(@ViewType int viewType) {
    if (this.viewType == viewType) {
      return;
    }
    switch (viewType) {
      case VIEW_TYPE_CANVAS:
        setView(new CanvasSubtitleOutput(getContext()));
        break;
      case VIEW_TYPE_WEB:
        setView(new WebViewSubtitleOutput(getContext()));
        break;
      default:
        throw new IllegalArgumentException();
    }
    this.viewType = viewType;
  }

  private <T extends View & Output> void setView(T view) {
    removeView(innerSubtitleView);
    if (innerSubtitleView instanceof WebViewSubtitleOutput) {
      ((WebViewSubtitleOutput) innerSubtitleView).destroy();
    }
    innerSubtitleView = view;
    output = view;
    addView(view);
  }

  public void setFixedTextSize(@Dimension int unit, float size) {
    Context context = getContext();
    Resources resources;
    if (context == null) {
      resources = Resources.getSystem();
    } else {
      resources = context.getResources();
    }
    setTextSize(
            Cue.TEXT_SIZE_TYPE_ABSOLUTE,
            TypedValue.applyDimension(unit, size, resources.getDisplayMetrics()));
  }

  public void setUserDefaultTextSize() {
    setFractionalTextSize(DEFAULT_TEXT_SIZE_FRACTION * getUserCaptionFontScale());
  }

  public void setFractionalTextSize(float fractionOfHeight) {
    setFractionalTextSize(fractionOfHeight, false);
  }

  public void setFractionalTextSize(float fractionOfHeight, boolean ignorePadding) {
    setTextSize(
            ignorePadding
                    ? Cue.TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING
                    : Cue.TEXT_SIZE_TYPE_FRACTIONAL,
            fractionOfHeight);
  }

  private void setTextSize(@Cue.TextSizeType int textSizeType, float textSize) {
    this.defaultTextSizeType = textSizeType;
    this.defaultTextSize = textSize;
    updateOutput();
  }

  public void setApplyEmbeddedStyles(boolean applyEmbeddedStyles) {
    this.applyEmbeddedStyles = applyEmbeddedStyles;
    updateOutput();
  }

  public void setApplyEmbeddedFontSizes(boolean applyEmbeddedFontSizes) {
    this.applyEmbeddedFontSizes = applyEmbeddedFontSizes;
    updateOutput();
  }

  public void setUserDefaultStyle() {
    setStyle(getUserCaptionStyle());
  }

  public void setStyle(CaptionStyleCompat style) {
    this.style = style;
    updateOutput();
  }

  public void setBottomPaddingFraction(float bottomPaddingFraction) {
    this.bottomPaddingFraction = bottomPaddingFraction;
    updateOutput();
  }

  private float getUserCaptionFontScale() {
    if (isInEditMode()) {
      return 1f;
    }
    @Nullable
    CaptioningManager captioningManager =
            (CaptioningManager) getContext().getSystemService(Context.CAPTIONING_SERVICE);
    return captioningManager != null && captioningManager.isEnabled()
            ? captioningManager.getFontScale()
            : 1f;
  }

  private CaptionStyleCompat getUserCaptionStyle() {
    if (isInEditMode()) {
      return CaptionStyleCompat.DEFAULT;
    }
    @Nullable
    CaptioningManager captioningManager =
            (CaptioningManager) getContext().getSystemService(Context.CAPTIONING_SERVICE);
    return captioningManager != null && captioningManager.isEnabled()
            ? CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle())
            : CaptionStyleCompat.DEFAULT;
  }

  private void updateOutput() {
    output.update(
            getCuesWithStylingPreferencesApplied(),
            style,
            defaultTextSize,
            defaultTextSizeType,
            bottomPaddingFraction);
  }

  private List<Cue> getCuesWithStylingPreferencesApplied() {
    if (applyEmbeddedStyles && applyEmbeddedFontSizes) {
      return cues;
    }
    List<Cue> strippedCues = new ArrayList<>(cues.size());
    for (int i = 0; i < cues.size(); i++) {
      strippedCues.add(removeEmbeddedStyling(cues.get(i)));
    }
    return strippedCues;
  }

  private Cue removeEmbeddedStyling(Cue cue) {
    Cue.Builder strippedCue = cue.buildUpon();
    if (!applyEmbeddedStyles) {
      SubtitleViewUtils.removeAllEmbeddedStyling(strippedCue);
    } else if (!applyEmbeddedFontSizes) {
      SubtitleViewUtils.removeEmbeddedFontSizes(strippedCue);
    }
    return strippedCue.build();
  }
}
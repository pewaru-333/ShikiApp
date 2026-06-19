package org.application.shikiapp.shared;

import static org.application.shikiapp.shared.SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION;
import static org.application.shikiapp.shared.SubtitleView.DEFAULT_TEXT_SIZE_FRACTION;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.text.Cue;
import androidx.media3.common.util.UnstableApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@UnstableApi
final class CanvasSubtitleOutput extends View implements SubtitleView.Output {

  private final List<SubtitlePainter> painters;

  private List<Cue> cues;
  private @Cue.TextSizeType int textSizeType;
  private float textSize;
  private CaptionStyleCompat style;
  private float bottomPaddingFraction;

  public CanvasSubtitleOutput(Context context) {
    this(context, null);
  }

  public CanvasSubtitleOutput(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    painters = new ArrayList<>();
    cues = Collections.emptyList();
    textSizeType = Cue.TEXT_SIZE_TYPE_FRACTIONAL;
    textSize = DEFAULT_TEXT_SIZE_FRACTION;
    style = CaptionStyleCompat.DEFAULT;
    bottomPaddingFraction = DEFAULT_BOTTOM_PADDING_FRACTION;
  }

  @Override
  public void update(
          List<Cue> cues,
          CaptionStyleCompat style,
          float textSize,
          @Cue.TextSizeType int textSizeType,
          float bottomPaddingFraction) {
    this.cues = cues;
    this.style = style;
    this.textSize = textSize;
    this.textSizeType = textSizeType;
    this.bottomPaddingFraction = bottomPaddingFraction;
    while (painters.size() < cues.size()) {
      painters.add(new SubtitlePainter(getContext()));
    }
    invalidate();
  }

  @Override
  public void dispatchDraw(@NonNull Canvas canvas) {
    @Nullable List<Cue> cues = this.cues;
    if (Objects.requireNonNull(cues).isEmpty()) {
      return;
    }

    int rawViewHeight = getHeight();

    int left = getPaddingLeft();
    int top = getPaddingTop();
    int right = getWidth() - getPaddingRight();
    int bottom = rawViewHeight - getPaddingBottom();
    if (bottom <= top || right <= left) {
      return;
    }
    int viewHeightMinusPadding = bottom - top;

    float defaultViewTextSizePx =
            SubtitleViewUtils.resolveTextSize(
                    textSizeType, textSize, rawViewHeight, viewHeightMinusPadding);
    if (defaultViewTextSizePx <= 0) {
      return;
    }

    int cueCount = cues.size();
    for (int i = 0; i < cueCount; i++) {
      Cue cue = cues.get(i);
      if (cue.verticalType != Cue.TYPE_UNSET) {
        cue = repositionVerticalCue(cue);
      }
      float cueTextSizePx =
              SubtitleViewUtils.resolveTextSize(
                      cue.textSizeType, cue.textSize, rawViewHeight, viewHeightMinusPadding);
      SubtitlePainter painter = painters.get(i);
      painter.draw(
              cue,
              style,
              defaultViewTextSizePx,
              cueTextSizePx,
              bottomPaddingFraction,
              canvas,
              left,
              top,
              right,
              bottom);
    }
  }

  private static Cue repositionVerticalCue(Cue cue) {
    Cue.Builder cueBuilder =
            cue.buildUpon()
                    .setPosition(Cue.DIMEN_UNSET)
                    .setPositionAnchor(Cue.TYPE_UNSET)
                    .setTextAlignment(null);

    if (cue.lineType == Cue.LINE_TYPE_FRACTION) {
      cueBuilder.setLine(1.0f - cue.line, Cue.LINE_TYPE_FRACTION);
    } else {
      cueBuilder.setLine(-cue.line - 1f, Cue.LINE_TYPE_NUMBER);
    }
    switch (cue.lineAnchor) {
      case Cue.ANCHOR_TYPE_END:
        cueBuilder.setLineAnchor(Cue.ANCHOR_TYPE_START);
        break;
      case Cue.ANCHOR_TYPE_START:
        cueBuilder.setLineAnchor(Cue.ANCHOR_TYPE_END);
        break;
      case Cue.ANCHOR_TYPE_MIDDLE:
      case Cue.TYPE_UNSET:
      default:
    }
    return cueBuilder.build();
  }
}
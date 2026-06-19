package org.application.shikiapp.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.text.Cue;
import androidx.media3.common.util.UnstableApi;

import java.util.Objects;

public final class SubtitlePainter {

  private static final float INNER_PADDING_RATIO = 0.125f;

  private final float outlineWidth;
  private final float shadowRadius;
  private final float shadowOffset;
  private final float spacingMult;
  private final float spacingAdd;

  private final TextPaint textPaint;
  private final Paint windowPaint;
  private final Paint bitmapPaint;

  @Nullable private CharSequence cueText;
  @Nullable private Alignment cueTextAlignment;
  @Nullable private Bitmap cueBitmap;
  private float cueLine;
  private @Cue.LineType int cueLineType;
  private @Cue.AnchorType int cueLineAnchor;
  private float cuePosition;
  private @Cue.AnchorType int cuePositionAnchor;
  private float cueSize;
  private float cueBitmapHeight;
  private int foregroundColor;
  private int backgroundColor;
  private int windowColor;
  private int edgeColor;
  private @CaptionStyleCompat.EdgeType int edgeType;
  private float defaultTextSizePx;
  private float cueTextSizePx;
  private float bottomPaddingFraction;
  private int parentLeft;
  private int parentTop;
  private int parentRight;
  private int parentBottom;

  private StaticLayout textLayout;
  private StaticLayout edgeLayout;
  private int textLeft;
  private int textTop;
  private int textPaddingX;
  private Rect bitmapRect;

  @SuppressWarnings("ResourceType")
  public SubtitlePainter(Context context) {
    int[] viewAttr = {android.R.attr.lineSpacingExtra, android.R.attr.lineSpacingMultiplier};
    try (TypedArray styledAttributes = context.obtainStyledAttributes(null, viewAttr, 0, 0)) {
      spacingAdd = styledAttributes.getDimensionPixelSize(0, 0);
      spacingMult = styledAttributes.getFloat(1, 1);
      styledAttributes.recycle();
    }

    Resources resources = context.getResources();
    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
    int twoDpInPx = Math.round((2f * displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT);
    outlineWidth = twoDpInPx;
    shadowRadius = twoDpInPx;
    shadowOffset = twoDpInPx;

    textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setSubpixelText(true);

    windowPaint = new Paint();
    windowPaint.setAntiAlias(true);
    windowPaint.setStyle(Style.FILL);

    bitmapPaint = new Paint();
    bitmapPaint.setAntiAlias(true);
    bitmapPaint.setFilterBitmap(true);
  }

  @OptIn(markerClass = UnstableApi.class)
  public void draw(
          Cue cue,
          CaptionStyleCompat style,
          float defaultTextSizePx,
          float cueTextSizePx,
          float bottomPaddingFraction,
          Canvas canvas,
          int cueBoxLeft,
          int cueBoxTop,
          int cueBoxRight,
          int cueBoxBottom) {
    boolean isTextCue = cue.bitmap == null;
    int windowColor = Color.BLACK;
    if (isTextCue) {
      if (TextUtils.isEmpty(cue.text)) {
        return;
      }
      windowColor = cue.windowColorSet ? cue.windowColor : style.windowColor();
    }
    if (areCharSequencesEqual(this.cueText, cue.text)
            && Objects.equals(this.cueTextAlignment, cue.textAlignment)
            && this.cueBitmap == cue.bitmap
            && this.cueLine == cue.line
            && this.cueLineType == cue.lineType
            && Objects.equals(this.cueLineAnchor, cue.lineAnchor)
            && this.cuePosition == cue.position
            && Objects.equals(this.cuePositionAnchor, cue.positionAnchor)
            && this.cueSize == cue.size
            && this.cueBitmapHeight == cue.bitmapHeight
            && this.foregroundColor == style.foregroundColor()
            && this.backgroundColor == style.backgroundColor()
            && this.windowColor == windowColor
            && this.edgeType == style.edgeType()
            && this.edgeColor == style.edgeColor()
            && Objects.equals(this.textPaint.getTypeface(), style.typeface())
            && this.defaultTextSizePx == defaultTextSizePx
            && this.cueTextSizePx == cueTextSizePx
            && this.bottomPaddingFraction == bottomPaddingFraction
            && this.parentLeft == cueBoxLeft
            && this.parentTop == cueBoxTop
            && this.parentRight == cueBoxRight
            && this.parentBottom == cueBoxBottom) {
      drawLayout(canvas, isTextCue);
      return;
    }

    this.cueText = BidiUtils.containsRtl(cue.text) ? BidiUtils.wrapText(cue.text) : cue.text;
    this.cueTextAlignment = cue.textAlignment;
    this.cueBitmap = cue.bitmap;
    this.cueLine = cue.line;
    this.cueLineType = cue.lineType;
    this.cueLineAnchor = cue.lineAnchor;
    this.cuePosition = cue.position;
    this.cuePositionAnchor = cue.positionAnchor;
    this.cueSize = cue.size;
    this.cueBitmapHeight = cue.bitmapHeight;
    this.foregroundColor = style.foregroundColor();
    this.backgroundColor = style.backgroundColor();
    this.windowColor = windowColor;
    this.edgeType = style.edgeType();
    this.edgeColor = style.edgeColor();
    this.textPaint.setTypeface(style.typeface());
    this.defaultTextSizePx = defaultTextSizePx;
    this.cueTextSizePx = cueTextSizePx;
    this.bottomPaddingFraction = bottomPaddingFraction;
    this.parentLeft = cueBoxLeft;
    this.parentTop = cueBoxTop;
    this.parentRight = cueBoxRight;
    this.parentBottom = cueBoxBottom;

    if (isTextCue) {
      checkNotNull(cueText);
      setupTextLayout();
    } else {
      checkNotNull(cueBitmap);
      setupBitmapLayout();
    }
    drawLayout(canvas, isTextCue);
  }

  @OptIn(markerClass = UnstableApi.class)
  private void setupTextLayout() {
    SpannableStringBuilder cueText =
            this.cueText instanceof SpannableStringBuilder
                    ? (SpannableStringBuilder) this.cueText
                    : new SpannableStringBuilder(this.cueText);
    int parentWidth = parentRight - parentLeft;
    int parentHeight = parentBottom - parentTop;

    textPaint.setTextSize(defaultTextSizePx);
    int textPaddingX = (int) (defaultTextSizePx * INNER_PADDING_RATIO + 0.5f);

    int availableWidth = parentWidth - textPaddingX * 2;
    if (cueSize != Cue.DIMEN_UNSET) {
      availableWidth = (int) (availableWidth * cueSize);
    }
    if (availableWidth <= 0) {
      return;
    }

    if (cueTextSizePx > 0) {
      cueText.setSpan(
              new AbsoluteSizeSpan((int) cueTextSizePx),
              0,
              cueText.length(),
              Spanned.SPAN_PRIORITY);
    }

    SpannableStringBuilder cueTextEdge = new SpannableStringBuilder(cueText);
    if (edgeType == CaptionStyleCompat.EDGE_TYPE_OUTLINE) {
      ForegroundColorSpan[] foregroundColorSpans =
              cueTextEdge.getSpans(0, cueTextEdge.length(), ForegroundColorSpan.class);
      for (ForegroundColorSpan foregroundColorSpan : foregroundColorSpans) {
        cueTextEdge.removeSpan(foregroundColorSpan);
      }
    }

    if (Color.alpha(backgroundColor) > 0) {
      if (edgeType == CaptionStyleCompat.EDGE_TYPE_NONE
              || edgeType == CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW) {
        cueText.setSpan(
                new BackgroundColorSpan(backgroundColor), 0, cueText.length(), Spanned.SPAN_PRIORITY);
      } else {
        cueTextEdge.setSpan(
                new BackgroundColorSpan(backgroundColor),
                0,
                cueTextEdge.length(),
                Spanned.SPAN_PRIORITY);
      }
    }

    Alignment textAlignment = cueTextAlignment == null ? Alignment.ALIGN_CENTER : cueTextAlignment;
    textLayout =
            new StaticLayout(
                    cueText, textPaint, availableWidth, textAlignment, spacingMult, spacingAdd, true);
    int textHeight = textLayout.getHeight();
    int textWidth = 0;
    int lineCount = textLayout.getLineCount();
    for (int i = 0; i < lineCount; i++) {
      textWidth = Math.max((int) Math.ceil(textLayout.getLineWidth(i)), textWidth);
    }
    if (cueSize != Cue.DIMEN_UNSET && textWidth < availableWidth) {
      textWidth = availableWidth;
    }
    textWidth += textPaddingX * 2;

    int textLeft;
    int textRight;
    if (cuePosition != Cue.DIMEN_UNSET) {
      int anchorPosition = Math.round(parentWidth * cuePosition) + parentLeft;
        textLeft = switch (cuePositionAnchor) {
            case Cue.ANCHOR_TYPE_END -> anchorPosition - textWidth;
            case Cue.ANCHOR_TYPE_MIDDLE -> (anchorPosition * 2 - textWidth) / 2;
            default -> anchorPosition;
        };

      textLeft = Math.max(textLeft, parentLeft);
      textRight = Math.min(textLeft + textWidth, parentRight);
    } else {
      textLeft = (parentWidth - textWidth) / 2 + parentLeft;
      textRight = textLeft + textWidth;
    }

    textWidth = textRight - textLeft;
    if (textWidth <= 0) {
      return;
    }

    int textTop;
    if (cueLine != Cue.DIMEN_UNSET) {
      if (cueLineType == Cue.LINE_TYPE_FRACTION) {
        int anchorPosition = Math.round(parentHeight * cueLine) + parentTop;
        textTop =
                cueLineAnchor == Cue.ANCHOR_TYPE_END
                        ? anchorPosition - textHeight
                        : cueLineAnchor == Cue.ANCHOR_TYPE_MIDDLE
                          ? (anchorPosition * 2 - textHeight) / 2
                          : anchorPosition;
      } else {
        int firstLineHeight = textLayout.getLineBottom(0) - textLayout.getLineTop(0);
        if (cueLine >= 0) {
          textTop = Math.round(cueLine * firstLineHeight) + parentTop;
        } else {
          textTop = Math.round((cueLine + 1) * firstLineHeight) + parentBottom - textHeight;
        }
      }

      if (textTop + textHeight > parentBottom) {
        textTop = parentBottom - textHeight;
      } else if (textTop < parentTop) {
        textTop = parentTop;
      }
    } else {
      textTop = parentBottom - textHeight - (int) (parentHeight * bottomPaddingFraction);
    }

    this.textLayout =
            new StaticLayout(
                    cueText, textPaint, textWidth, textAlignment, spacingMult, spacingAdd, true);
    this.edgeLayout =
            new StaticLayout(
                    cueTextEdge, textPaint, textWidth, textAlignment, spacingMult, spacingAdd, true);
    this.textLeft = textLeft;
    this.textTop = textTop;
    this.textPaddingX = textPaddingX;
  }

  private void setupBitmapLayout() {
    Bitmap cueBitmap = this.cueBitmap;
    int parentWidth = parentRight - parentLeft;
    int parentHeight = parentBottom - parentTop;
    float anchorX = parentLeft + (parentWidth * cuePosition);
    float anchorY = parentTop + (parentHeight * cueLine);
    int width = Math.round(parentWidth * cueSize);
    int height =
            cueBitmapHeight != Cue.DIMEN_UNSET
                    ? Math.round(parentHeight * cueBitmapHeight)
                    : Math.round(width * ((float) Objects.requireNonNull(cueBitmap).getHeight() / cueBitmap.getWidth()));
    int x =
            Math.round(
                    cuePositionAnchor == Cue.ANCHOR_TYPE_END
                            ? (anchorX - width)
                            : cuePositionAnchor == Cue.ANCHOR_TYPE_MIDDLE ? (anchorX - (width / 2f)) : anchorX);
    int y =
            Math.round(
                    cueLineAnchor == Cue.ANCHOR_TYPE_END
                            ? (anchorY - height)
                            : cueLineAnchor == Cue.ANCHOR_TYPE_MIDDLE ? (anchorY - (height / 2f)) : anchorY);
    bitmapRect = new Rect(x, y, x + width, y + height);
  }

  private void drawLayout(Canvas canvas, boolean isTextCue) {
    if (isTextCue) {
      drawTextLayout(canvas);
    } else {
      checkNotNull(bitmapRect);
      checkNotNull(cueBitmap);
      drawBitmapLayout(canvas);
    }
  }

  @OptIn(markerClass = UnstableApi.class)
  private void drawTextLayout(Canvas canvas) {
    StaticLayout textLayout = this.textLayout;
    StaticLayout edgeLayout = this.edgeLayout;
    if (textLayout == null || edgeLayout == null) {
      return;
    }

    int saveCount = canvas.save();
    canvas.translate(textLeft, textTop);

    if (Color.alpha(windowColor) > 0) {
      windowPaint.setColor(windowColor);
      canvas.drawRect(
              -textPaddingX,
              0,
              textLayout.getWidth() + textPaddingX,
              textLayout.getHeight(),
              windowPaint);
    }

    if (edgeType == CaptionStyleCompat.EDGE_TYPE_OUTLINE) {
      textPaint.setStrokeJoin(Join.ROUND);
      textPaint.setStrokeWidth(outlineWidth);
      textPaint.setColor(edgeColor);
      textPaint.setStyle(Style.FILL_AND_STROKE);
      edgeLayout.draw(canvas);
    } else if (edgeType == CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW) {
      textPaint.setShadowLayer(shadowRadius, shadowOffset, shadowOffset, edgeColor);
    } else if (edgeType == CaptionStyleCompat.EDGE_TYPE_RAISED
            || edgeType == CaptionStyleCompat.EDGE_TYPE_DEPRESSED) {
      boolean raised = edgeType == CaptionStyleCompat.EDGE_TYPE_RAISED;
      int colorUp = raised ? Color.WHITE : edgeColor;
      int colorDown = raised ? edgeColor : Color.WHITE;
      float offset = shadowRadius / 2f;
      textPaint.setColor(foregroundColor);
      textPaint.setStyle(Style.FILL);
      textPaint.setShadowLayer(shadowRadius, -offset, -offset, colorUp);
      edgeLayout.draw(canvas);
      textPaint.setShadowLayer(shadowRadius, offset, offset, colorDown);
    }

    textPaint.setColor(foregroundColor);
    textPaint.setStyle(Style.FILL);
    textLayout.draw(canvas);
    textPaint.setShadowLayer(0, 0, 0, 0);

    canvas.restoreToCount(saveCount);
  }

  private void drawBitmapLayout(Canvas canvas) {
      if (cueBitmap != null) {
          canvas.drawBitmap(cueBitmap, null, bitmapRect, bitmapPaint);
      }
  }

  @SuppressWarnings("UndefinedEquals")
  private static boolean areCharSequencesEqual(
          @Nullable CharSequence first, @Nullable CharSequence second) {
    return Objects.equals(first, second);
  }
}
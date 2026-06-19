package org.application.shikiapp.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;

import androidx.annotation.OptIn;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.LanguageFeatureSpan;
import androidx.media3.common.util.UnstableApi;

import com.google.common.base.Predicate;

public final class SubtitleViewUtils {

  public static float resolveTextSize(
          @Cue.TextSizeType int textSizeType,
          float textSize,
          int rawViewHeight,
          int viewHeightMinusPadding) {
    if (textSize == Cue.DIMEN_UNSET) {
      return Cue.DIMEN_UNSET;
    }
      return switch (textSizeType) {
          case Cue.TEXT_SIZE_TYPE_ABSOLUTE -> textSize;
          case Cue.TEXT_SIZE_TYPE_FRACTIONAL -> textSize * viewHeightMinusPadding;
          case Cue.TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING -> textSize * rawViewHeight;
          default -> Cue.DIMEN_UNSET;
      };
  }

  @OptIn(markerClass = UnstableApi.class)
  public static void removeAllEmbeddedStyling(Cue.Builder cue) {
    cue.clearWindowColor();
    if (cue.getText() instanceof Spanned) {
      if (!(cue.getText() instanceof Spannable)) {
        cue.setText(SpannableString.valueOf(cue.getText()));
      }
      removeSpansIf(
              (Spannable) checkNotNull(cue.getText()), span -> !(span instanceof LanguageFeatureSpan));
    }
    removeEmbeddedFontSizes(cue);
  }

  @OptIn(markerClass = UnstableApi.class)
  public static void removeEmbeddedFontSizes(Cue.Builder cue) {
    cue.setTextSize(Cue.DIMEN_UNSET, Cue.TYPE_UNSET);
    if (cue.getText() instanceof Spanned) {
      if (!(cue.getText() instanceof Spannable)) {
        cue.setText(SpannableString.valueOf(cue.getText()));
      }
      removeSpansIf(
              (Spannable) checkNotNull(cue.getText()),
              span -> span instanceof AbsoluteSizeSpan || span instanceof RelativeSizeSpan);
    }
  }

  private static void removeSpansIf(Spannable spannable, Predicate<Object> removeFilter) {
    Object[] spans = spannable.getSpans(0, spannable.length(), Object.class);
    for (Object span : spans) {
      if (removeFilter.apply(span)) {
        spannable.removeSpan(span);
      }
    }
  }

  private SubtitleViewUtils() {}
}
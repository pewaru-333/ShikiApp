package org.application.shikiapp.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextDirectionHeuristics;

import androidx.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class BidiUtils {

  private static final Splitter LF_SPLITTER = Splitter.on("\n");
  private static final Splitter CRLF_SPLITTER = Splitter.on("\r\n");
  private static final Joiner LF_JOINER = Joiner.on("\n");

  static boolean containsRtl(@Nullable CharSequence input) {
    if (input == null) {
      return false;
    }
    int length = input.length();
    for (int offset = 0; offset < length; ) {
      int codePoint = Character.codePointAt(input, offset);
      byte dir = Character.getDirectionality(codePoint);
      if (dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT
              || dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
              || dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING
              || dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE) {
        return true;
      }
      offset += Character.charCount(codePoint);
    }
    return false;
  }

  public static CharSequence wrapText(CharSequence input) {
    BidiFormatter bidiFormatter = BidiFormatter.getInstance();
    Spanned spannedInput = null;
    Object[] spans = null;
    int[] spanStarts = null;
    int[] spanEnds = null;

    if (input instanceof Spanned) {
      spannedInput = (Spanned) input;
      spans = spannedInput.getSpans(0, input.length(), Object.class);
      spanStarts = new int[spans.length];
      spanEnds = new int[spans.length];
      Arrays.fill(spanStarts, -1);
      Arrays.fill(spanEnds, -1);
    }

    List<String> lines;
    int eolLength;
    if (input.toString().contains("\r\n")) {
      lines = CRLF_SPLITTER.splitToList(input);
      eolLength = 2;
    } else {
      lines = LF_SPLITTER.splitToList(input);
      eolLength = 1;
    }

    List<String> wrappedLines = new ArrayList<>(lines.size());

    int spanUpdate = 0;
    int lineStart = 0;
    for (String line : lines) {
      String wrappedLine = bidiFormatter.unicodeWrap(line, TextDirectionHeuristics.LTR);
      if (spans != null) {
        checkNotNull(spannedInput);
        checkNotNull(spanStarts);
        checkNotNull(spanEnds);
        int diff = wrappedLine.length() - line.length();
        if (diff > 0) {
          spanUpdate++;
        }
        for (int j = 0; j < spans.length; j++) {
          if ((spanStarts[j] < 0)
                  && (spannedInput.getSpanStart(spans[j]) >= lineStart)
                  && (spannedInput.getSpanStart(spans[j]) < lineStart + line.length())) {
            spanStarts[j] = spanUpdate;
          }
          if ((spanEnds[j] < 0)
                  && ((spannedInput.getSpanEnd(spans[j]) - 1) >= lineStart)
                  && ((spannedInput.getSpanEnd(spans[j]) - 1) < lineStart + line.length())) {
            spanEnds[j] = spanUpdate;
          }
        }
        lineStart += line.length() + eolLength;
        if (diff > 0) {
          spanUpdate++;
        }
      }
      wrappedLines.add(wrappedLine);
    }

    SpannableStringBuilder wrapped = new SpannableStringBuilder(LF_JOINER.join(wrappedLines));

    if (spans != null) {
      checkNotNull(spannedInput);
      checkNotNull(spanStarts);
      checkNotNull(spanEnds);
      for (int i = 0; i < spans.length; i++) {
        int start = spannedInput.getSpanStart(spans[i]) + spanStarts[i];
        int end = spannedInput.getSpanEnd(spans[i]) + spanEnds[i];
        int flags = spannedInput.getSpanFlags(spans[i]);
        if ((start >= 0) && (start < wrapped.length()) && (end >= 0) && (end <= wrapped.length())) {
          wrapped.setSpan(spans[i], start, end, flags);
        }
      }
    }

    return wrapped;
  }
}
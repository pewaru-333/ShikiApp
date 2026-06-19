package org.application.shikiapp.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.text.HorizontalTextInVerticalContextSpan;
import androidx.media3.common.text.RubySpan;
import androidx.media3.common.text.TextAnnotation;
import androidx.media3.common.text.TextEmphasisSpan;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

final class SpannedToHtmlConverter {

  private static final Pattern NEWLINE_PATTERN = Pattern.compile("(&#13;)?&#10;");

  private SpannedToHtmlConverter() {}

  @OptIn(markerClass = UnstableApi.class)
  public static HtmlAndCss convert(@Nullable CharSequence text, float displayDensity) {
    if (text == null) {
      return new HtmlAndCss("", ImmutableMap.of());
    }
    if (!(text instanceof Spanned spanned)) {
      return new HtmlAndCss(escapeHtml(text), ImmutableMap.of());
    }

      Set<Integer> backgroundColors = new HashSet<>();
    for (BackgroundColorSpan backgroundColorSpan :
            spanned.getSpans(0, spanned.length(), BackgroundColorSpan.class)) {
      backgroundColors.add(backgroundColorSpan.getBackgroundColor());
    }
    HashMap<String, String> cssRuleSets = new HashMap<>();
    for (int backgroundColor : backgroundColors) {
      cssRuleSets.put(
              HtmlUtils.cssAllClassDescendantsSelector("bg_" + backgroundColor),
              Util.formatInvariant("background-color:%s;", HtmlUtils.toCssRgba(backgroundColor)));
    }

    SparseArray<Transition> spanTransitions = findSpanTransitions(spanned, displayDensity);
    StringBuilder html = new StringBuilder(spanned.length());
    int previousTransition = 0;
    for (int i = 0; i < spanTransitions.size(); i++) {
      int index = spanTransitions.keyAt(i);
      html.append(escapeHtml(spanned.subSequence(previousTransition, index)));

      Transition transition = spanTransitions.get(index);
      transition.spansRemoved.sort(SpanInfo.FOR_CLOSING_TAGS);
      for (SpanInfo spanInfo : transition.spansRemoved) {
        html.append(spanInfo.closingTag);
      }
      transition.spansAdded.sort(SpanInfo.FOR_OPENING_TAGS);
      for (SpanInfo spanInfo : transition.spansAdded) {
        html.append(spanInfo.openingTag);
      }
      previousTransition = index;
    }

    html.append(escapeHtml(spanned.subSequence(previousTransition, spanned.length())));

    return new HtmlAndCss(html.toString(), cssRuleSets);
  }

  private static SparseArray<Transition> findSpanTransitions(
          Spanned spanned, float displayDensity) {
    SparseArray<Transition> spanTransitions = new SparseArray<>();

    for (Object span : spanned.getSpans(0, spanned.length(), Object.class)) {
      @Nullable String openingTag = getOpeningTag(span, displayDensity);
      @Nullable String closingTag = getClosingTag(span);
      int spanStart = spanned.getSpanStart(span);
      int spanEnd = spanned.getSpanEnd(span);
      if (openingTag != null) {
        checkNotNull(closingTag);
        SpanInfo spanInfo = new SpanInfo(spanStart, spanEnd, openingTag, closingTag);
        getOrCreate(spanTransitions, spanStart).spansAdded.add(spanInfo);
        getOrCreate(spanTransitions, spanEnd).spansRemoved.add(spanInfo);
      }
    }

    return spanTransitions;
  }

  @OptIn(markerClass = UnstableApi.class)
  @Nullable
  private static String getOpeningTag(Object span, float displayDensity) {
      switch (span) {
          case StrikethroughSpan ignored -> {
              return "<span style='text-decoration:line-through;'>";
          }
          case ForegroundColorSpan colorSpan -> {
              return Util.formatInvariant(
                      "<span style='color:%s;'>", HtmlUtils.toCssRgba(colorSpan.getForegroundColor()));
          }
          case BackgroundColorSpan colorSpan -> {
              return Util.formatInvariant("<span class='bg_%s'>", colorSpan.getBackgroundColor());
          }
          case HorizontalTextInVerticalContextSpan ignored -> {
              return "<span style='text-combine-upright:all;'>";
          }
          case AbsoluteSizeSpan absoluteSizeSpan -> {
              float sizeCssPx =
                      absoluteSizeSpan.getDip()
                              ? absoluteSizeSpan.getSize()
                              : absoluteSizeSpan.getSize() / displayDensity;
              return Util.formatInvariant("<span style='font-size:%.2fpx;'>", sizeCssPx);
          }
          case RelativeSizeSpan relativeSizeSpan -> {
              return Util.formatInvariant(
                      "<span style='font-size:%.2f%%;'>", relativeSizeSpan.getSizeChange() * 100);
          }
          case TypefaceSpan typefaceSpan -> {
              @Nullable String fontFamily = typefaceSpan.getFamily();
              return fontFamily != null
                      ? Util.formatInvariant("<span style='font-family:\"%s\";'>", fontFamily)
                      : null;
          }
          case StyleSpan styleSpan -> {
              return switch (styleSpan.getStyle()) {
                  case Typeface.BOLD -> "<b>";
                  case Typeface.ITALIC -> "<i>";
                  case Typeface.BOLD_ITALIC -> "<b><i>";
                  default -> null;
              };
          }
          case RubySpan rubySpan -> {
              return switch (rubySpan.position) {
                  case TextAnnotation.POSITION_BEFORE -> "<ruby style='ruby-position:over;'>";
                  case TextAnnotation.POSITION_AFTER -> "<ruby style='ruby-position:under;'>";
                  case TextAnnotation.POSITION_UNKNOWN -> "<ruby style='ruby-position:unset;'>";
                  default -> null;
              };
          }
          case UnderlineSpan ignored -> {
              return "<u>";
          }
          case TextEmphasisSpan textEmphasisSpan -> {
              String style = getTextEmphasisStyle(textEmphasisSpan.markShape, textEmphasisSpan.markFill);
              String position = getTextEmphasisPosition(textEmphasisSpan.position);
              return Util.formatInvariant(
                      "<span style='-webkit-text-emphasis-style:%1$s;text-emphasis-style:%1$s;"
                              + "-webkit-text-emphasis-position:%2$s;text-emphasis-position:%2$s;"
                              + "display:inline-block;'>",
                      style, position);
          }
          case null, default -> {
              return null;
          }
      }
  }

  @OptIn(markerClass = UnstableApi.class)
  @Nullable
  private static String getClosingTag(Object span) {
    if (span instanceof StrikethroughSpan
            || span instanceof ForegroundColorSpan
            || span instanceof BackgroundColorSpan
            || span instanceof HorizontalTextInVerticalContextSpan
            || span instanceof AbsoluteSizeSpan
            || span instanceof RelativeSizeSpan
            || span instanceof TextEmphasisSpan) {
      return "</span>";
    } else if (span instanceof TypefaceSpan) {
      @Nullable String fontFamily = ((TypefaceSpan) span).getFamily();
      return fontFamily != null ? "</span>" : null;
    } else if (span instanceof StyleSpan) {
      switch (((StyleSpan) span).getStyle()) {
        case Typeface.BOLD:
          return "</b>";
        case Typeface.ITALIC:
          return "</i>";
        case Typeface.BOLD_ITALIC:
          return "</i></b>";
      }
    } else if (span instanceof RubySpan rubySpan) {
        return "<rt>" + escapeHtml(rubySpan.rubyText) + "</rt></ruby>";
    } else if (span instanceof UnderlineSpan) {
      return "</u>";
    }
    return null;
  }

  @OptIn(markerClass = UnstableApi.class)
  private static String getTextEmphasisStyle(
          @TextEmphasisSpan.MarkShape int shape, @TextEmphasisSpan.MarkFill int fill) {
    StringBuilder builder = new StringBuilder();
    switch (fill) {
      case TextEmphasisSpan.MARK_FILL_FILLED:
        builder.append("filled ");
        break;
      case TextEmphasisSpan.MARK_FILL_OPEN:
        builder.append("open ");
        break;
      case TextEmphasisSpan.MARK_FILL_UNKNOWN:
      default:
        break;
    }

    switch (shape) {
      case TextEmphasisSpan.MARK_SHAPE_CIRCLE:
        builder.append("circle");
        break;
      case TextEmphasisSpan.MARK_SHAPE_DOT:
        builder.append("dot");
        break;
      case TextEmphasisSpan.MARK_SHAPE_SESAME:
        builder.append("sesame");
        break;
      case TextEmphasisSpan.MARK_SHAPE_NONE:
        builder.append("none");
        break;
      default:
        builder.append("unset");
        break;
    }
    return builder.toString();
  }

  @OptIn(markerClass = UnstableApi.class)
  private static String getTextEmphasisPosition(@TextAnnotation.Position int position) {
      if (position == TextAnnotation.POSITION_AFTER) {
          return "under left";
      }
      return "over right";
  }

  private static Transition getOrCreate(SparseArray<Transition> transitions, int key) {
    @Nullable Transition transition = transitions.get(key);
    if (transition == null) {
      transition = new Transition();
      transitions.put(key, transition);
    }
    return transition;
  }

  private static String escapeHtml(CharSequence text) {
    String escaped = Html.escapeHtml(text);
    return NEWLINE_PATTERN.matcher(escaped).replaceAll("<br>");
  }

  public static class HtmlAndCss {

    public final String html;

    public final Map<String, String> cssRuleSets;

    private HtmlAndCss(String html, Map<String, String> cssRuleSets) {
      this.html = html;
      this.cssRuleSets = cssRuleSets;
    }
  }

  private record SpanInfo(int start, int end, String openingTag, String closingTag) {
      private static final Comparator<SpanInfo> FOR_OPENING_TAGS =
              (info1, info2) -> {
                int result = Integer.compare(info2.end, info1.end);
                if (result != 0) {
                  return result;
                }
                result = info1.openingTag.compareTo(info2.openingTag);
                if (result != 0) {
                  return result;
                }
                return info1.closingTag.compareTo(info2.closingTag);
              };

      private static final Comparator<SpanInfo> FOR_CLOSING_TAGS =
              (info1, info2) -> {
                int result = Integer.compare(info2.start, info1.start);
                if (result != 0) {
                  return result;
                }
                result = info2.openingTag.compareTo(info1.openingTag);
                if (result != 0) {
                  return result;
                }
                return info2.closingTag.compareTo(info1.closingTag);
              };

  }

  private static final class Transition {
    private final List<SpanInfo> spansAdded;
    private final List<SpanInfo> spansRemoved;

    public Transition() {
      this.spansAdded = new ArrayList<>();
      this.spansRemoved = new ArrayList<>();
    }
  }
}
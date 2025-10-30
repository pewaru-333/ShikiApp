package org.application.shikiapp.utils

import android.icu.text.NumberFormat
import android.text.format.DateUtils
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Date
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Season
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.safeValueOf
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle


fun getNextEpisode(date: Any?) = if (date !is String) BLANK
else OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

fun convertDate() = DateUtils.getRelativeTimeSpanString(
     OffsetDateTime.now().toInstant().toEpochMilli(),
     System.currentTimeMillis(),
     DateUtils.DAY_IN_MILLIS
 ).toString()

fun convertDate(date: Any?, offset: Boolean = true) = when {
     date !is String -> BLANK

     offset -> {
         val millis = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
             .toInstant()
             .toEpochMilli()

          DateUtils.getRelativeTimeSpanString(
             millis,
             System.currentTimeMillis(),
             DateUtils.DAY_IN_MILLIS
         ).toString()
     }

     else -> {
          LocalDate.parse(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
     }
 }

fun convertScore(score: Any?) = when (val result = score) {
    is String -> result.replace(".", ",")
    is Double -> NumberFormat.getNumberInstance(Locale.current.platformLocale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(score)

    else -> BLANK
}

fun getLinks(text: String): List<String> {
    val links = mutableListOf<String>()
    val matcher = Patterns.WEB_URL.matcher(text)

    while (matcher.find()) {
        val url = matcher.group()

        if (url.contains("https://") || url.contains(".jpg")) {
            links.add(
                if (!url.startsWith("https://") && url.contains(".jpg")) "https://$url" else url
            )
        }
    }

    return links
}

fun getBirthday(birthday: Date?) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${birthday?.day}.${birthday?.month}.${birthday?.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (_: DateTimeParseException) {
        null
    }
}

fun getDeathday(deceasedOn: Date?) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${deceasedOn?.day}.${deceasedOn?.month}.${deceasedOn?.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (_: DateTimeParseException) {
        null
    }
}

fun getNewsPoster(text: String?): String? {
    val embed = getLinks(text.orEmpty()).find { it.contains("img.youtube.com") }
    val poster = getLinks(text.orEmpty()).find { it.contains(".jpg") }

    return embed ?: poster
}

fun getWatchStatus(status: String?, type: LinkedType) = if (status == null) R.string.text_unknown
else when (type) {
    LinkedType.ANIME -> Enum.safeValueOf<WatchStatus>(status).titleAnime
    LinkedType.MANGA -> Enum.safeValueOf<WatchStatus>(status).titleManga
    else -> R.string.text_unknown
}

fun getFull(full: Int? = 0, status: String? = Status.ONGOING.name.lowercase()) =
    if (Status.ONGOING.safeEquals(status) && full == 0) "?" else full.toString()

fun getOngoingSeason(): String {
    val currentDate = LocalDate.now()

    val currentSeason = Season.entries.first { currentDate.month in it.months }
    val previousSeason = Season.entries.getOrNull(currentSeason.ordinal - 1) ?: Season.AUTUMN

    val currentYear = currentDate.year
    val previousYear = if (currentSeason == Season.WINTER) currentYear - 1 else currentYear

    val (currentSeasonName, previousSeasonName) = listOf(currentSeason, previousSeason).map {
        if (it.safeEquals("autumn")) "fall" else it.name.lowercase()
    }

    return "${currentSeasonName}_$currentYear,${previousSeasonName}_$previousYear"
}

fun getSeason(text: Any?, kind: String?) = when (text) {
    is Int -> ResourceText.StaticString(text.toString())
    is String -> when (text) {
        "?" -> ResourceText.StringResource(R.string.text_unknown)
        else -> when {
            Enum.safeValueOf<Kind>(kind).linkedType
                    in listOf(LinkedType.MANGA, LinkedType.RANOBE) -> ResourceText.StaticString(LocalDate.parse(text).year.toString())

            text.contains("_") -> {
                val season = text.substringBefore("_").let { if (it == "fall") "autumn" else it }
                val year = text.substringAfter("_")

                ResourceText.MultiString(
                    listOf(
                        ResourceText.StringResource(Enum.safeValueOf<Season>(season).title),
                        year
                    )
                )
            }

            text.length == 10 -> {
                val date = LocalDate.parse(text)
                val season = Season.entries.first { date.month in it.months }

                ResourceText.MultiString(
                    listOf(
                        ResourceText.StringResource(season.title),
                        date.year
                    )
                )
            }

            else -> ResourceText.StringResource(R.string.blank)
        }
    }

    else -> ResourceText.StringResource(R.string.blank)
}

fun setScore(status: Set<String>, score: Float) = if (Status.ANONS.name.lowercase() in status) null
else score.toInt()

fun localizeNames(text: String) : String {
    val fullPattern = Regex("""<span class="name-en">(.*?)</span><span class="name-ru">(.*?)</span>""")
    val englishPattern = Regex("""<span class="name-en">(.*?)</span>""")
    val russianPattern = Regex("""<span class="name-ru">(.*?)</span>""")

    var modifiedHtml = text.replace(fullPattern, """<span class="name-ru">$2</span>""")

    if (!modifiedHtml.contains("<span class=\"name-ru\">")) {
        modifiedHtml = modifiedHtml.replace(englishPattern, """<span class="name-en">$1</span>""")
    }

    return modifiedHtml
        .replace(russianPattern, "$1")
        .replace(englishPattern, "$1")
}

fun fromHtml(text: String?) = if (text == null) AnnotatedString(BLANK)
else AnnotatedString.Companion.fromHtml(
    htmlString = localizeNames(text),
    linkStyles = TextLinkStyles(
        SpanStyle(
            color = Color(0xFF33BBFF),
            textDecoration = TextDecoration.Companion.Underline,
            platformStyle = PlatformSpanStyle.Companion.Default
        )
    )
)
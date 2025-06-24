package org.application.shikiapp.utils

import android.text.format.DateUtils
import android.util.Patterns
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


fun convertDate(date: String): String {
    val formatted = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val millis = formatted.toInstant().toEpochMilli()

    return DateUtils.getRelativeTimeSpanString(
        millis,
        System.currentTimeMillis(),
        DateUtils.DAY_IN_MILLIS
    ).toString()
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
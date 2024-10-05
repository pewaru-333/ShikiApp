package org.application.shikiapp.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.util.Patterns
import org.application.AnimeQuery.Data.Anime.Studio
import org.application.MangaQuery.Data.Manga.Publisher
import org.application.shikiapp.models.data.Date
import org.application.shikiapp.models.data.Person
import java.time.LocalDate
import java.time.Month.APRIL
import java.time.Month.AUGUST
import java.time.Month.DECEMBER
import java.time.Month.FEBRUARY
import java.time.Month.JANUARY
import java.time.Month.JULY
import java.time.Month.JUNE
import java.time.Month.MARCH
import java.time.Month.MAY
import java.time.Month.NOVEMBER
import java.time.Month.OCTOBER
import java.time.Month.SEPTEMBER
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale


fun convertDate(text: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.forLanguageTag("ru"))
    val parsed = format.parse(text).time

    return DateUtils.getRelativeTimeSpanString(
        parsed,
        Calendar.getInstance().timeInMillis,
        DateUtils.DAY_IN_MILLIS
    ).toString()
}

fun getLinks(text: String): List<String> {
    val links = ArrayList<String>()
    val words = text.split("\"")
    val pattern = Patterns.WEB_URL

    words.forEach {
        if (pattern.matcher(it).find() && (it.contains("https://") || it.contains(".jpg")))
            links.add(it)
    }

    links.indices.forEach {
        if (!links[it].contains("https://"))
            links[it] = "https:".plus(links[it])
    }

    return links
}

fun fromISODate(date: String) = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME)
fun toCalendarDate(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("d MMMM, E"))

fun getBirthday(birthday: Date) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${birthday.day}.${birthday.month}.${birthday.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun getDeathday(deceasedOn: Date) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${deceasedOn.day}.${deceasedOn.month}.${deceasedOn.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun isPersonFavoured(person: Person) = person.personFavoured || person.producerFavoured
        || person.mangakaFavoured || person.seyuFavoured

fun getPoster(text: String?): String? {
    val embed = getLinks(text.orEmpty()).find { it.contains("img.youtube.com") }
    val poster = getLinks(text.orEmpty()).find { it.contains(".jpg") }

    return (embed ?: poster)
}

fun getImage(link: String?) = "https://shikimori.one${link.orEmpty()}"
fun getStatusA(status: String?) = STATUSES_A[status] ?: "Неизвестно"
fun getStatusM(status: String?) = STATUSES_M[status] ?: "Неизвестно"
fun getWatchStatus(status: String?, type: String) = when (type) {
    LINKED_TYPE[0] -> WATCH_STATUSES_A[status] ?: "Неизвестно"
    else -> WATCH_STATUSES_M[status] ?: "Неизвестно"
}

fun getKind(kind: String?) = KINDS_A[kind] ?: KINDS_M[kind] ?: KINDS_R[kind] ?: "Неизвестно"
fun getRating(rating: String?) = RATINGS[rating] ?: "Неизвестно"
fun getFull(full: Int? = 0, status: String? = STATUSES_A.keys.elementAt(1)) =
    if (status == STATUSES_A.keys.elementAt(1) && full == 0) "?" else full.toString()

fun getSeason(text: Any?, kind: String?) = when (text) {
    is String -> when (text) {
        "?" -> "Неизвестно"
        else -> when {
            kind in KINDS_M || kind in KINDS_R -> LocalDate.parse(text).year.toString()
            text.contains("_") -> {
                val season = text.substringBefore("_").let { if (it == "fall") "autumn" else it }
                val year = text.substringAfter("_")
                "${SEASONS[season]} $year"
            }

            text.length == 10 -> {
                val date = LocalDate.parse(text)
                val season = when (date.month) {
                    JANUARY, FEBRUARY, DECEMBER -> SEASONS.values.elementAt(0)
                    MARCH, APRIL, MAY -> SEASONS.values.elementAt(1)
                    JUNE, JULY, AUGUST -> SEASONS.values.elementAt(2)
                    SEPTEMBER, OCTOBER, NOVEMBER -> SEASONS.values.elementAt(3)
                }
                "$season ${date.year}"
            }

            else -> BLANK
        }
    }

    else -> BLANK
}

fun getStudio(studio: List<Studio>) = try {
    studio.first().name
} catch (e: NoSuchElementException) {
    "Неизвестно"
}

fun getPublisher(publisher: List<Publisher>) = try {
    publisher.first().name
} catch (e: NoSuchElementException) {
    "Неизвестно"
}

fun setScore(status: List<String>, score: Float) = if (STATUSES_A.keys.elementAt(0) in status) null
else score.toInt()
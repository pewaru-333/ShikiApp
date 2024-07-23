package org.application.shikiapp.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.util.Patterns
import org.application.AnimeQuery
import org.application.shikiapp.models.data.Date
import org.application.shikiapp.models.data.Person
import java.time.LocalDate
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

//fun htmlFooter(text: String): List<String> {
//    val links: ArrayList<String> = ArrayList()
//
//    val urls = "((https?):((//)|(\\\\))+[\\w:#@%/;$()~_?+-=\\\\.&]*)"
//    val pattern = Pattern.compile(urls, Pattern.CASE_INSENSITIVE)
//    val matcher = pattern.matcher(text)
//
//    while (matcher.find()) {
//        val link = text.substring(matcher.start(), matcher.end())
//
//        if (!link.contains("user_images/preview")) {
//            links.add(link)
//        }
//    }
//
//    return links.distinct()
//}

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
fun getStatus(status: String?) = STATUSES[status] ?: "Неизвестно"
fun getWatchStatus(status: String?) = WATCH_STATUSES[status] ?: "Неизвестно"
fun getKind(kind: String?) = KINDS[kind] ?: "Неизвестно"
fun getRating(rating: String?) = RATINGS[rating] ?: "Неизвестно"
fun getSex(sex: String?) = when (sex) {
    "male" -> "Мужской"
    "female" -> "Женский"
    else -> "Не указан"
}

fun getSeason(text: String?) = when (text) {
    null -> BLANK
    "?" -> "Неизвестно"
    else -> {
        if (text.contains("-")) BLANK
        else {
            val season = text.substringBefore("_").let { if (it == "fall") "autumn" else it }
            val year = text.substringAfter("_")
            "${SEASONS[season]} $year"
        }
    }
}

fun getStudio(studio: List<AnimeQuery.Studio>) = try {
    studio.first().name
} catch (e: NoSuchElementException) {
    "Неизвестно"
}
package org.application.shikiapp.utils

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import android.util.Patterns
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import org.application.AnimeQuery.Data.Anime.Studio
import org.application.MangaQuery.Data.Manga.Publisher
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
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.reflect.KClass


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

//fun fromISODate(date: String) = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME)
//fun toCalendarDate(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("d MMMM, E"))

fun getBirthday(birthday: Date?) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${birthday?.day}.${birthday?.month}.${birthday?.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun getDeathday(deceasedOn: Date?) = DATE_FORMATS.firstNotNullOfOrNull {
    try {
        LocalDate.parse(
            "${deceasedOn?.day}.${deceasedOn?.month}.${deceasedOn?.year}"
                .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
        ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun getPoster(text: String?): String? {
    val embed = getLinks(text.orEmpty()).find { it.contains("img.youtube.com") }
    val poster = getLinks(text.orEmpty()).find { it.contains(".jpg") }

    return embed ?: poster
}

fun getImage(link: String?) = "https://shikimori.one${link.orEmpty()}"
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

fun setScore(status: List<String>, score: Float) = if (Status.ANONS.name.lowercase() in status) null
else score.toInt()

fun Context.isDomainVerified() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) true
    else {
        val manager = getSystemService(DomainVerificationManager::class.java)
        val userState = manager.getDomainVerificationUserState(packageName)!!

        userState.hostToStateMap.all { it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
    }

fun <T : Any> NavBackStackEntry?.isCurrentRoute(route: KClass<T>) =
    this?.destination?.hierarchy?.any { it.hasRoute(route) } == true

fun NavHostController.toBottomBarItem(route: Any) = currentBackStackEntry?.destination?.route?.let {
    if (!route.toString().contains(it)) {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
            popBackStack(route, true)
        }
    }
}

package org.application.shikiapp.utils

import android.icu.text.NumberFormat
import android.text.format.DateUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Date
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.enums.Season
import org.application.shikiapp.utils.enums.Status
import org.application.shikiapp.utils.enums.WatchStatus
import org.application.shikiapp.utils.extensions.safeEquals
import org.application.shikiapp.utils.extensions.safeValueOf
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit


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

fun convertScore(score: Any?) = when (score) {
    is String -> score.replace(".", ",")
    is Double -> NumberFormat.getNumberInstance(Locale.current.platformLocale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(score)

    else -> BLANK
}

suspend fun getLinks(text: String?) = withContext(Dispatchers.Default) {
    if (text.isNullOrBlank()) return@withContext Triple(emptyList(), emptyList(), null)

    fun String.toHttps() = when {
        isEmpty() -> BLANK
        startsWith("//") -> "https:$this"
        startsWith("http://") -> replace("http://", "https://")
        else -> if (startsWith("http")) this else "https://$this"
    }

    val body = Jsoup.parseBodyFragment(text)

    val (videos, images) = coroutineScope {
        val videos = async {
            body.select(".b-video").mapNotNull { element ->
                val videoUrl = element.select("a").attr("href").toHttps()
                val previewUrl = element.select("img").attr("src").toHttps()
                if (videoUrl.isNotEmpty()) CommentContent.VideoContent(previewUrl, videoUrl, "YouTube") else null
            }
        }

        val images = async {
            body.select("a[href*=/original/], img[src*=/original/]")
                .map { it.attr("href").ifEmpty { it.attr("src") }.toHttps() }
                .distinct()
        }

        Pair(videos.await(), images.await())
    }

    val poster: CommentContent? = when {
        videos.isNotEmpty() -> videos.first()
        images.isNotEmpty() -> CommentContent.ImageContent(
            previewUrl = images.first(),
            fullUrl = images.first(),
            width = 0f,
            height = 0f
        )

        else -> null
    }

    return@withContext Triple(images, videos, poster)
}

fun getPersonDates(birthday: Date?, deathday: Date? = null): Pair<ResourceText?, ResourceText?> {
    if (birthday == null) return Pair(null, null)

    val isDead = deathday != null && (deathday.day != null || deathday.month != null || deathday.year != null)
    val actualDeathday = if (isDead) deathday else null

    val age = run {
        val bYear = birthday.year ?: return@run null
        val dYear = actualDeathday?.year ?: LocalDate.now().year

        if (birthday.day != null && birthday.month != null) {
            val bDate = LocalDate.of(bYear, birthday.month, birthday.day)
            val dDate = if (actualDeathday?.day != null && actualDeathday.month != null) {
                LocalDate.of(dYear, actualDeathday.month, actualDeathday.day)
            } else {
                if (actualDeathday == null) LocalDate.now() else null
            }

            dDate?.let { ChronoUnit.YEARS.between(bDate, it).toInt() } ?: (dYear - bYear)
        } else {
            dYear - bYear
        }
    }

    fun formatDateInfo(date: Date?, showAge: Boolean): ResourceText? {
        if (date == null || (date.day == null && date.month == null && date.year == null)) return null

        val (day, month, year) = date
        val baseString = when {
            day != null && month != null && year != null -> {
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                val formatted = LocalDate.of(year, month, day).format(formatter)

                ResourceText.StaticString(formatted)
            }

            day != null && month != null -> {
                val locale = Locale.current.platformLocale
                val pattern = DateTimeFormatter.ofPattern("d MMMM", locale)
                val formatted = MonthDay.of(month, day).format(pattern)

                ResourceText.StaticString(formatted)
            }

            year != null -> ResourceText.StringResource(R.string.text_year_in, year)

            else -> return null
        }

        return if (!showAge || age == null) baseString else ResourceText.MultiString(
            value = buildList {
                add(baseString)
                add(" ")
                add(ResourceText.PluralStringResource(R.plurals.plural_years_old, age, age))
                if (day == null || month == null) {
                    add("?")
                }
            }
        )
    }

    val birthString = formatDateInfo(date = birthday, showAge = !isDead)
    val deathString = formatDateInfo(date = actualDeathday, showAge = isDead)

    return Pair(birthString, deathString)
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
                    value = listOf(
                        ResourceText.StringResource(Enum.safeValueOf<Season>(season).title),
                        " ",
                        year
                    )
                )
            }

            text.length == 10 -> {
                val date = LocalDate.parse(text)
                val season = Season.entries.first { date.month in it.months }

                ResourceText.MultiString(
                    value = listOf(
                        ResourceText.StringResource(season.title),
                        " ",
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
else AnnotatedString.fromHtml(
    htmlString = localizeNames(text),
    linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = Color(0xFF33BBFF),
            textDecoration = TextDecoration.Underline,
            platformStyle = PlatformSpanStyle.Default
        )
    )
)
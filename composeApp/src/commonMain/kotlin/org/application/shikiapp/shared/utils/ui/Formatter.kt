package org.application.shikiapp.shared.utils.ui

import androidx.compose.ui.text.intl.Locale
import com.fleeksoft.ksoup.Ksoup
import com.ibm.icu.text.RelativeDateTimeFormatter
import com.ibm.icu.util.ULocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.data.Date
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Season
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.application.shikiapp.shared.utils.extensions.safeEquals
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank
import shikiapp.composeapp.generated.resources.plural_years_old
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.text_year_in
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

object Formatter {
    private val zoneId = ZoneId.systemDefault()

    private val scoreFormatter by lazy {
        NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag(Locale.current.toLanguageTag())).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    fun convertDate(
        from: Long = OffsetDateTime.now().toInstant().toEpochMilli(),
        now: Long = System.currentTimeMillis()
    ): String {
        val fromDate = Instant.ofEpochMilli(from).atZone(zoneId).toLocalDate()
        val nowDate = Instant.ofEpochMilli(now).atZone(zoneId).toLocalDate()
        val diffDays = ChronoUnit.DAYS.between(fromDate, nowDate)

        if (diffDays !in 0L..7L) {
            return fromDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }

        val formatter = RelativeDateTimeFormatter.getInstance(ULocale.getDefault())
        return when (diffDays) {
            0L -> formatter.format(
                RelativeDateTimeFormatter.Direction.THIS,
                RelativeDateTimeFormatter.AbsoluteUnit.DAY
            )

            1L -> formatter.format(
                RelativeDateTimeFormatter.Direction.LAST,
                RelativeDateTimeFormatter.AbsoluteUnit.DAY
            )

            else -> formatter.format(
                diffDays.toDouble(),
                RelativeDateTimeFormatter.Direction.LAST,
                RelativeDateTimeFormatter.RelativeUnit.DAYS
            )
        }
    }

    fun convertDate(date: Any?, offset: Boolean = true): String = when {
        date !is String -> BLANK

        offset -> {
            val millis = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toInstant()
                .toEpochMilli()

            convertDate(millis)
        }

        else -> {
            LocalDate.parse(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }
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
                    val locale = Locale.current.toLanguageTag()
                    val pattern = DateTimeFormatter.ofPattern("d MMMM", java.util.Locale.forLanguageTag(locale))
                    val formatted = MonthDay.of(month, day).format(pattern)

                    ResourceText.StaticString(formatted)
                }

                year != null -> ResourceText.StringResource(Res.string.text_year_in, year)

                else -> return null
            }

            return if (!showAge || age == null) baseString else ResourceText.MultiString(
                value = buildList {
                    add(baseString)
                    add(" ")
                    add(ResourceText.PluralStringResource(Res.plurals.plural_years_old, age, age))
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

    fun getSeason(text: Any?, kind: String?): ResourceText {
        if (text is Int) return ResourceText.StaticString(text.toString())
        if (text !is String) return ResourceText.StringResource(Res.string.blank)

        fun formatSeason(season: Season, year: Any) = ResourceText.MultiString(
            value = listOf(ResourceText.StringResource(season.title), " ", year.toString())
        )

        return when {
            text == "?" -> ResourceText.StringResource(Res.string.text_unknown)

            Enum.safeValueOf<Kind>(kind).linkedType.let { it == LinkedType.MANGA || it == LinkedType.RANOBE } -> {
                ResourceText.StaticString(LocalDate.parse(text).year.toString())
            }

            "_" in text -> {
                val (season, year) = text.split("_", limit = 2)
                val seasonMapped = if (season == "fall") "autumn" else season

                formatSeason(Enum.safeValueOf(seasonMapped), year)
            }

            text.length == 10 -> {
                val date = LocalDate.parse(text)
                val season = Season.entries.first { date.month in it.months }

                formatSeason(season, date.year)
            }

            else -> ResourceText.StringResource(Res.string.blank)
        }
    }

    suspend fun getLinks(text: String?): Triple<List<String>, List<CommentContent.VideoContent>, CommentContent?> = withContext(Dispatchers.Default) {
        if (text.isNullOrBlank()) return@withContext Triple(emptyList(), emptyList(), null)

        fun String.toHttps() = when {
            isEmpty() -> BLANK
            startsWith("//") -> "https:$this"
            startsWith("http://") -> replace("http://", "https://")
            else -> if (startsWith("http")) this else "https://$this"

        }

        val body = Ksoup.parse(text, ApiRoutes.workingBaseUrl)

        val (videos, images) = coroutineScope {
            val videos = async {
                val extractedVideos = body.select(".b-video").mapNotNullTo(ArrayList()) { element ->
                    val videoUrl = element.select("a").attr("href").toHttps()
                    val previewUrl = element.select("img").attr("src").toHttps()
                    if (videoUrl.isNotEmpty()) CommentContent.VideoContent(previewUrl, videoUrl, "YouTube") else null
                }

                if (extractedVideos.isNotEmpty()) return@async extractedVideos

                val ytRegex = "img\\.youtube\\.com(?:%2F|/)vi(?:%2F|/)([a-zA-Z0-9_-]+)".toRegex()
                body.select(".b-image").forEach { element ->
                    val imageUrl = element.attr("abs:href").ifEmpty { element.selectFirst("img")?.attr("abs:src") }.orEmpty()
                    val match = ytRegex.find(imageUrl)
                    if (match != null) {
                        val videoId = match.groupValues[1]
                        val fullVideoUrl = "https://youtu.be/$videoId"

                        if (extractedVideos.none { it.videoUrl == fullVideoUrl }) {
                            extractedVideos.add(
                                CommentContent.VideoContent(
                                    previewUrl = imageUrl,
                                    videoUrl = fullVideoUrl,
                                    source = "YouTube"
                                )
                            )
                        }
                    }
                }

                extractedVideos
            }

            val images = async {
                body.select("a[href*=/original/], img[src*=/original/], a[href*=%2Foriginal%2F], img[src*=%2Foriginal%2F]")
                    .map { it.attr("href").ifEmpty { it.attr("src") } }
                    .distinct()
                    .toMutableList()
            }

            Pair(videos.await(), images.await())
        }

        val poster: CommentContent? = when {
            videos.isNotEmpty() -> {
                val video = videos[0]
                videos.remove(video)

                video
            }

            images.isNotEmpty() -> {
                val image = images[0]
                images.remove(image)

                CommentContent.ImageContent(
                    previewUrl = image,
                    fullUrl = image,
                    width = 0f,
                    height = 0f
                )
            }

            else -> null
        }

        return@withContext Triple(images, videos, poster)
    }

    fun replaceMissingAnimePoster(poster: String?, id: Any?) = poster?.takeIf { "missing" !in it }
        ?: id?.let { "https://smarthard.net/static/animes/$it.jpeg" }
            .orEmpty()

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

    fun localizeHtmlBody(html: String) =
        Regex("""<([a-zA-Z0-9]+)([^>]*)>(.*?)</\1>""").replace(html) { result ->
            val tagName = result.groupValues[1]
            val attributes = result.groupValues[2]
            val content = result.groupValues[3]

            val ruMatch = Regex("""data-text-ru="([^"]*)"""").find(attributes)
            val enMatch = Regex("""data-text-en="([^"]*)"""").find(attributes)

            if (ruMatch != null || enMatch != null) {
                val ruText = ruMatch?.groupValues?.get(1)?.takeIf(String::isNotBlank)
                val enText = enMatch?.groupValues?.get(1)?.takeIf(String::isNotBlank)
                val localizedText = ruText ?: enText ?: content

                if (tagName.lowercase() == "span") {
                    localizedText
                } else {
                    "<$tagName$attributes>$localizedText</$tagName>"
                }
            } else {
                result.value
            }
        }

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

    fun getNextEpisode(date: Any?): String = if (date !is String) BLANK
    else OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .format(DateTimeFormatter.ofPattern("d MMMM, H:mm"))

    fun getFullEpisodes(full: Int? = 0, status: String? = Status.ONGOING.name.lowercase()) =
        if (Status.ONGOING.safeEquals(status) && full == 0) "?" else full.toString()

    fun convertScore(score: Any?): String = when (score) {
        is String -> score.replace(".", ",")
        is Double -> scoreFormatter.format(score)
        else -> BLANK
    }

    fun setScore(status: Set<String>, score: Float) = if (Status.ANONS.name.lowercase() in status) null
    else score.toInt()

    fun getWatchStatus(status: String?, type: LinkedType) = if (status == null) Res.string.text_unknown
    else when (type) {
        LinkedType.ANIME -> Enum.safeValueOf<WatchStatus>(status).titleAnime
        LinkedType.MANGA -> Enum.safeValueOf<WatchStatus>(status).titleManga
        else -> Res.string.text_unknown
    }
}
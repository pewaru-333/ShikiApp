package org.application.shikiapp.shared.utils.ui

import com.fleeksoft.ksoup.Ksoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.application.shikiapp.shared.models.data.Date
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.*
import org.application.shikiapp.shared.utils.extensions.format
import org.application.shikiapp.shared.utils.extensions.safeEquals
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.formatRelativeDays
import shikiapp.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.Instant

object Formatter {
    fun convertDate(
        fromMillis: Long = Clock.System.now().toEpochMilliseconds(),
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): String {
        val timeZone = TimeZone.currentSystemDefault()

        val fromDate = Instant.fromEpochMilliseconds(fromMillis).toLocalDateTime(timeZone).date
        val nowDate = Instant.fromEpochMilliseconds(nowMillis).toLocalDateTime(timeZone).date

        val diffDays = fromDate.daysUntil(nowDate)

        return if (diffDays !in 0..7) {
            fromDate.format(DateStyle.MEDIUM)
        } else {
            formatRelativeDays(diffDays)
        }
    }

    fun convertDate(date: Any?, offset: Boolean = true): String = when {
        date !is String -> BLANK

        offset -> {
            try {
                convertDate(Instant.parse(date).toEpochMilliseconds())
            } catch (_: Exception) {
                BLANK
            }
        }

        else -> {
            try {
                LocalDate.parse(date).format(DateStyle.MEDIUM)
            } catch (_: Exception) {
                BLANK
            }
        }
    }

    fun getPersonDates(birthday: Date?, deathday: Date? = null): Pair<ResourceText?, ResourceText?> {
        if (birthday == null) return Pair(null, null)

        val isDead = deathday != null && (deathday.day != null || deathday.month != null || deathday.year != null)
        val actualDeathday = if (isDead) deathday else null

        val age = run {
            val bYear = birthday.year ?: return@run null
            val dYear = actualDeathday?.year ?: Clock.System.todayIn(TimeZone.currentSystemDefault()).year

            if (birthday.day != null && birthday.month != null) {
                val bDate = LocalDate(bYear, birthday.month, birthday.day)
                val dDate = if (actualDeathday?.day != null && actualDeathday.month != null) {
                    LocalDate(dYear, actualDeathday.month, actualDeathday.day)
                } else {
                    if (actualDeathday == null) Clock.System.todayIn(TimeZone.currentSystemDefault()) else null
                }

                dDate?.let { bDate.yearsUntil(it) } ?: (dYear - bYear)
            } else {
                dYear - bYear
            }
        }

        fun formatDateInfo(date: Date?, showAge: Boolean): ResourceText? {
            if (date == null || (date.day == null && date.month == null && date.year == null)) return null

            val (day, month, year) = date
            val baseString = when {
                day != null && month != null && year != null -> {
                    val formatted = LocalDate(year, month, day).format(DateStyle.LONG)

                    ResourceText.StaticString(formatted)
                }

                day != null && month != null -> {
                    val formatted = LocalDate(2000, month, day).format("d MMMM") // 2000 - високосный - хак для работы

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

    fun formatTime(seconds: Float): String {
        if (seconds.isNaN() || seconds < 0f) return "00:00"

        val totalSecs = seconds.toInt()
        val h = totalSecs / 3600
        val m = (totalSecs % 3600) / 60
        val s = totalSecs % 60

        return if (h > 0) {
            "${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        } else {
            "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        }
    }

    suspend fun getLinks(text: String?, baseUrl: String = ApiRoutes.workingBaseUrl): Triple<List<String>, List<CommentContent.VideoContent>, CommentContent?> = withContext(Dispatchers.Default) {
        if (text.isNullOrBlank()) {
            return@withContext Triple(emptyList(), emptyList(), null)
        }

        fun String.toHttps() = when {
            isEmpty() -> BLANK
            startsWith("//") -> "https:$this"
            startsWith("http://[") -> this
            startsWith("http://") -> replace("http://", "https://")
            else -> if (startsWith("http")) this else "https://$this"
        }

        val ytRegex = Regex("""img\.youtube\.com(?:%2F|/)vi(?:%2F|/)([a-zA-Z0-9_-]+)""")
        val videoRegex = Regex("""(?:youtu\.be/|youtube(?:-nocookie)?\.com/(?:embed/|watch\?v=|shorts/))([a-zA-Z0-9_-]+)""")
        val body = Ksoup.parse(text, baseUrl)
        val videos = LinkedHashMap<String, CommentContent.VideoContent>()
        val images = LinkedHashMap<String, String>()

        for (element in body.select(".b-video")) {
            val link = element.selectFirst("a")
            val videoUrl = link?.attr("abs:href").orEmpty()
                .ifEmpty { link?.attr("abs:data-href").orEmpty() }
                .toHttps()

            val previewUrl = element.select("img").attr("abs:src").toHttps()
            val youtubeId = videoRegex.find(videoUrl)?.groupValues?.get(1) ?: ytRegex.find(previewUrl)?.groupValues?.get(1)
            val url = if (youtubeId == null) videoUrl else "https://youtu.be/$youtubeId"

            if (url.isNotEmpty() && url !in videos) {
                val source = element.select(".marker").text().ifBlank { "Video" }
                videos[url] = CommentContent.VideoContent(previewUrl, url, source)
            }

            element.remove()
        }

        for (element in body.select(".b-image, img:not(.smiley), a[href*=/original/], a[href*=%2Foriginal%2F], a[href*=/uploads/]")) {
            if (element.parent()?.closest(".b-image") != null) continue

            val image = if (element.tagName() == "img") element else element.selectFirst("img")
            val link = if (element.tagName() == "a") element else element.closest("a")
            val imageUrl = link?.attr("abs:href").orEmpty()
                .ifEmpty { image?.attr("abs:src").orEmpty() }
                .toHttps()

            if (imageUrl.isEmpty()) continue

            val previewUrl = image?.attr("abs:src").orEmpty().ifEmpty { imageUrl }.toHttps()
            val youtubeId = ytRegex.find(imageUrl)?.groupValues?.get(1) ?: ytRegex.find(previewUrl)?.groupValues?.get(1)

            if (youtubeId != null) {
                val videoUrl = "https://youtu.be/$youtubeId"
                if (videoUrl !in videos) {
                    videos[videoUrl] = CommentContent.VideoContent(previewUrl, videoUrl, "YouTube")
                }
            } else if (imageUrl !in images) {
                images[imageUrl] = previewUrl
            }
        }

        val videoList = videos.values.toList()
        val imageList = images.keys.toList()

        when {
            videoList.isNotEmpty() -> Triple(imageList, videoList.drop(1), videoList.first())
            imageList.isNotEmpty() -> {
                val image = imageList.first()

                Triple(imageList.drop(1), emptyList(), CommentContent.ImageContent(images.getValue(image), image, 0f, 0f))
            }
            else -> Triple(emptyList(), emptyList(), null)
        }
    }

    fun getNewsText(text: String): String {
        val body = Ksoup.parse(text)
        body.select(".b-video, .b-image, img:not(.smiley)").remove()

        return body.body().html()
    }

    fun replaceMissingAnimePoster(poster: String?, id: Any?) = poster
        ?.takeIf { it.isNotBlank() && "missing" !in it }
        ?: id?.let { "/system/animes/original/$it.jpg" }
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
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val currentSeason = Season.entries.first { currentDate.month in it.months }
        val previousSeason = Season.entries.getOrNull(currentSeason.ordinal - 1) ?: Season.AUTUMN

        val currentYear = currentDate.year
        val previousYear = if (currentSeason == Season.WINTER) currentYear - 1 else currentYear

        val (currentSeasonName, previousSeasonName) = listOf(currentSeason, previousSeason).map {
            if (it.safeEquals("autumn")) "fall" else it.name.lowercase()
        }

        return "${currentSeasonName}_$currentYear,${previousSeasonName}_$previousYear"
    }

    fun getNextEpisode(date: Any?): String = when (date) {
        is String -> try {
            Instant.parse(date)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format("d MMMM, H:mm")
        } catch (_: Exception) {
            BLANK
        }

        else -> BLANK
    }

    fun getFullEpisodes(full: Int? = 0, status: String? = Status.ONGOING.name.lowercase()) =
        if (Status.ONGOING.safeEquals(status) && full == 0) "?" else full.toString()

    fun convertScore(score: Any?): String = when (score) {
        is String -> score.replace(".", ",")
        is Double -> score.format()
        else -> BLANK
    }

    fun setScore(status: Set<String>, score: Float) = if (Status.ANONS.name.lowercase() in status) null
    else score.toInt()

    fun getWatchStatus(status: String, type: LinkedType) = type.getWatchStatusTitle(Enum.safeValueOf<WatchStatus>(status))
}
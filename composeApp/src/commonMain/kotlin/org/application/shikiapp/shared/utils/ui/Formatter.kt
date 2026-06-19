package org.application.shikiapp.shared.utils.ui

import com.fleeksoft.ksoup.Ksoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearsUntil
import org.application.shikiapp.shared.models.data.Date
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.DateStyle
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.OpinionType
import org.application.shikiapp.shared.utils.enums.Season
import org.application.shikiapp.shared.utils.enums.Status
import org.application.shikiapp.shared.utils.enums.WatchStatus
import org.application.shikiapp.shared.utils.extensions.format
import org.application.shikiapp.shared.utils.extensions.safeEquals
import org.application.shikiapp.shared.utils.extensions.safeValueOf
import org.application.shikiapp.shared.utils.formatRelativeDays
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.blank
import shikiapp.composeapp.generated.resources.plural_years_old
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.text_year_in
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

    suspend fun getLinks(text: String?): Triple<List<String>, List<CommentContent.VideoContent>, CommentContent?> = withContext(Dispatchers.Default) {
        if (text.isNullOrBlank()) return@withContext Triple(emptyList(), emptyList(), null)

        fun String.toHttps() = when {
            isEmpty() -> BLANK
            startsWith("//") -> "https:$this"
            startsWith("http://") -> replace("http://", "https://")
            else -> if (startsWith("http")) this else "https://$this"

        }

        val ytRegex = "img\\.youtube\\.com(?:%2F|/)vi(?:%2F|/)([a-zA-Z0-9_-]+)".toRegex()
        val body = Ksoup.parse(text, ApiRoutes.workingBaseUrl)

        val videos = body.select(".b-video").mapNotNullTo(ArrayList()) { element ->
            val videoUrl = element.select("a").attr("href").toHttps()
            val previewUrl = element.select("img").attr("src").toHttps()
            if (videoUrl.isNotEmpty()) CommentContent.VideoContent(previewUrl, videoUrl, "YouTube") else null
        }

        if (videos.isEmpty()) {
            body.select(".b-image").forEach { element ->
                val imageUrl = element.attr("abs:href").ifEmpty { element.selectFirst("img")?.attr("abs:src") }.orEmpty()
                val match = ytRegex.find(imageUrl)

                if (match != null) {
                    val fullVideoUrl = "https://youtu.be/${match.groupValues[1]}"
                    if (videos.none { it.videoUrl == fullVideoUrl }) {
                        videos.add(
                            CommentContent.VideoContent(
                                previewUrl = imageUrl,
                                videoUrl = fullVideoUrl,
                                source = "YouTube"
                            )
                        )
                    }
                }
            }
        }

        val images = body.select("a[href*=/original/], img[src*=/original/], a[href*=%2Foriginal%2F], img[src*=%2Foriginal%2F]")
            .mapNotNullTo(LinkedHashSet()) { element ->
                element.attr("href")
                    .ifEmpty { element.attr("src") }
                    .takeIf(String::isNotEmpty)
            }.toList()

        val poster: CommentContent?
        val finalVideos: List<CommentContent.VideoContent>
        val finalImages: List<String>

        when {
            videos.isNotEmpty() -> {
                poster = videos.first()
                finalVideos = videos.drop(1)
                finalImages = images
            }
            images.isNotEmpty() -> {
                val image = images.first()
                poster = CommentContent.ImageContent(image, image, 0f, 0f)
                finalVideos = emptyList()
                finalImages = images.drop(1)
            }
            else -> {
                poster = null
                finalVideos = emptyList()
                finalImages = emptyList()
            }
        }

        Triple(finalImages, finalVideos, poster)
    }

    fun replaceMissingAnimePoster(poster: String?, id: Any?) = poster?.takeIf { it.isNotBlank() && "missing" !in it }
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

    fun parseReviews(dto: org.application.shikiapp.shared.models.data.Review) =
        Ksoup.parse(dto.content).select("article.b-review-topic").map { review ->
            val id = review.attr("id")
            val userId = review.attr("data-user_id").toLongOrNull() ?: 0L
            val nickname = review.selectFirst("meta[itemprop=author]")?.attr("content").orEmpty()

            val avatarImg = review.selectFirst("img")
            val avatarUrl = avatarImg?.attr("src").orEmpty()

            val body = review.selectFirst("div.body[itemprop=text]")?.html().orEmpty()
            val date = review.selectFirst("meta[itemprop=datePublished]")?.attr("content").orEmpty()

            val opinion = when {
                review.selectFirst("div.opinion.positive") != null -> OpinionType.POSITIVE
                review.selectFirst("div.opinion.negative") != null -> OpinionType.NEGATIVE
                review.selectFirst("div.opinion.neutral") != null -> OpinionType.NEUTRAL
                else -> OpinionType.UNKNOWN
            }

            val scoreClass = review.selectFirst("div.stars.score")?.attr("class").orEmpty()
            val animeScore = Regex("score-(\\d+)").find(scoreClass)?.groupValues?.get(1)?.toIntOrNull()

            val watchStatus = review.selectFirst("div.status-name")?.attr("data-text")?.takeIf(String::isNotBlank)

            val votesFor = review.selectFirst("span.votes-for")?.text().orEmpty()
            val votesAgainst = review.selectFirst("span.votes-against")?.text().orEmpty()

            Review(
                id = id,
                userId = userId,
                userNickname = nickname,
                userAvatar = avatarUrl,
                date = convertDate(date),
                body = HtmlParser.parseComment(body),
                opinion = opinion,
                animeScore = animeScore,
                watchStatus = watchStatus,
                votesFor = votesFor,
                votesAgainst = votesAgainst
            )
        }

    fun parseReviewsNextPage(postloader: String?): Int? {
        if (postloader.isNullOrBlank()) return null

        val nextLink = Ksoup.parse(postloader).selectFirst("a.next")?.attr("href") ?: return null
        return nextLink.substringAfterLast("page/").substringBefore(".").toIntOrNull()
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
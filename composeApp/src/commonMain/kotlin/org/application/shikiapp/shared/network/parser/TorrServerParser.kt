package org.application.shikiapp.shared.network.parser

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.PlaylistResult
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.basicJson
import kotlin.concurrent.Volatile
import kotlin.math.log10
import kotlin.math.pow

class TorrServerParser private constructor(client: HttpClient) : BaseParser(client) {
    override val token: String = BLANK

    override suspend fun searchById(id: String): Any {
        return emptyList<VideoVoice>()
    }

    override suspend fun getPlaylistLink(parseUrl: String, quality: Int): PlaylistResult {
        return PlaylistResult(url = parseUrl)
    }

    suspend fun searchTorrents(queries: List<String>): List<VideoVoice> {
        val jackettBaseUrl = Preferences.jackettUrl.value.trim().trimEnd('/')
        val apiKey = Preferences.jackettApiKey.value.trim()

        if (jackettBaseUrl.isBlank() || apiKey.isBlank() || queries.isEmpty()) {
            return emptyList()
        }

        val allResults = mutableListOf<JackettTorrentItem>()
        val seenHashes = mutableSetOf<String>()

        coroutineScope {
            val deferreds = queries.distinct().filter { it.isNotBlank() }.map { query ->
                async {
                    runCatching {
                        queryJackett(jackettBaseUrl, apiKey, query)
                    }.getOrDefault(emptyList())
                }
            }

            for (def in deferreds) {
                for (item in def.await()) {
                    val key = item.infoHash?.lowercase()?.takeIf { it.isNotBlank() } ?: item.title.trim().lowercase()
                    if (seenHashes.add(key) && isVideoTorrent(item)) {
                        allResults.add(item)
                    }
                }
            }
        }

        // Sort by seeders descending
        val sorted = allResults.sortedWith(
            compareByDescending<JackettTorrentItem> { it.seeders }
                .thenByDescending { it.size }
        )

        return sorted.mapIndexed { index, item ->
            val cleanTitle = cleanTorrentTitle(item.title)
            val trackerPrefix = item.tracker?.takeIf { it.isNotBlank() }?.let { "[$it] " } ?: BLANK
            val displayTitle = "$trackerPrefix$cleanTitle"

            val qualityStr = detectQuality(item.title)
            val sizeStr = formatBytes(item.size)
            val qualityLabel = buildString {
                if (qualityStr.isNotBlank()) {
                    append(qualityStr)
                    append(" • ")
                }
                append(sizeStr)
                append(" • S:")
                append(item.seeders)
            }

            val link = item.magnetUri?.takeIf { it.isNotBlank() } ?: normalizeLink(item.link.orEmpty())

            VideoVoice(
                id = item.infoHash?.hashCode() ?: (item.title.hashCode() + index),
                title = ResourceText.StaticString(displayTitle),
                hasDubbers = isDubbed(item.title),
                hasSubtitles = hasSubtitles(item.title),
                episodes = emptyList(),
                quality = ResourceText.StaticString(qualityLabel),
                lastEpisode = extractLastEpisodeFromTitle(item.title) ?: 1,
                torrentLink = link
            )
        }
    }

    suspend fun getTorrentEpisodes(torrentLink: String): List<EpisodeModel> {
        val torrServerBase = Preferences.torrServerUrl.value.trim().trimEnd('/')
        if (torrServerBase.isBlank() || torrentLink.isBlank()) return emptyList()

        val normalizedLink = normalizeLink(torrentLink)

        val statUrl = "$torrServerBase/stream?link=${normalizedLink.encodeURLParameter()}&stat"
        val responseText = client.get(statUrl).bodyAsText()
        val status = basicJson.decodeFromString<TorrServerStatus>(responseText)

        val hash = status.hash.ifEmpty {
            // Extract BTIH from magnet if hash is blank in status
            Regex("""btih:([a-zA-Z0-9]+)""", RegexOption.IGNORE_CASE)
                .find(normalizedLink)?.groupValues?.get(1) ?: normalizedLink
        }

        val videoFiles = status.fileStats.filter { file ->
            val ext = file.path.substringAfterLast('.', BLANK).lowercase()
            ext in VIDEO_EXTENSIONS && !isNonEpisodeFile(file.path)
        }

        val effectiveVideoFiles = if (videoFiles.isEmpty()) {
            // Fallback: accept any video files even if they look like extras if nothing else matched
            status.fileStats.filter { file ->
                val ext = file.path.substringAfterLast('.', BLANK).lowercase()
                ext in VIDEO_EXTENSIONS
            }
        } else videoFiles

        if (effectiveVideoFiles.isEmpty()) return emptyList()

        val subFiles = status.fileStats.filter { file ->
            val ext = file.path.substringAfterLast('.', BLANK).lowercase()
            ext in SUBTITLE_EXTENSIONS
        }

        // Parse episode numbers
        val parsedFiles = effectiveVideoFiles.mapIndexed { index, file ->
            val fileName = file.path.substringAfterLast('/').substringBeforeLast('.')
            val epNum = if (effectiveVideoFiles.size == 1) {
                1
            } else {
                extractEpisodeNumber(fileName) ?: (index + 1)
            }
            ParsedFile(file = file, fileName = fileName, episodeNumber = epNum)
        }

        // Sort by episode number, then natural filename
        val sortedFiles = parsedFiles.sortedWith(
            compareBy<ParsedFile> { it.episodeNumber }
                .thenBy { it.fileName }
        )

        // Ensure unique sequential episode numbers if all matched to the same number
        val finalEpisodes = if (sortedFiles.size > 1 && sortedFiles.all { it.episodeNumber == sortedFiles.first().episodeNumber }) {
            sortedFiles.mapIndexed { i, pf -> pf.copy(episodeNumber = i + 1) }
        } else {
            sortedFiles
        }

        return finalEpisodes.map { pf ->
            val cleanName = pf.fileName.replace('_', ' ').trim()
            val streamUrl = "$torrServerBase/stream/${pf.file.path.substringAfterLast('/').encodeURLPath()}?link=$hash&index=${pf.file.id}&play"

            // Find matching subtitles
            val matchingSubs = subFiles.filter { sub ->
                val subName = sub.path.substringAfterLast('/')
                val subEp = extractEpisodeNumber(subName)
                subEp == pf.episodeNumber || subName.contains(pf.fileName, ignoreCase = true)
            }.map { sub ->
                SubtitleTrack(
                    name = sub.path.substringAfterLast('/'),
                    url = "$torrServerBase/stream?link=$hash&index=${sub.id}&play"
                )
            }

            EpisodeModel(
                number = pf.episodeNumber,
                title = cleanName,
                link = streamUrl,
                subtitles = matchingSubs
            )
        }
    }

    private data class ParsedFile(
        val file: TorrServerFileStat,
        val fileName: String,
        val episodeNumber: Int
    )

    private suspend fun queryJackett(baseUrl: String, apiKey: String, query: String): List<JackettTorrentItem> {
        val paths = listOf(
            "$baseUrl/api/v2.0/indexers/all/results?apikey=$apiKey&Query=${query.encodeURLParameter()}",
            "$baseUrl/jackett/api/v2.0/indexers/all/results?apikey=$apiKey&Query=${query.encodeURLParameter()}"
        )

        for (url in paths) {
            val response = runCatching { client.get(url) }.getOrNull() ?: continue
            if (response.status == HttpStatusCode.OK) {
                val text = response.bodyAsText()
                val parsed = basicJson.decodeFromString<JackettSearchResponse>(text)
                return parsed.results
            }
        }
        return emptyList()
    }

    suspend fun testJackett(baseUrl: String, apiKey: String): Boolean {
        val cleanUrl = baseUrl.trim().trimEnd('/')
        val key = apiKey.trim()
        val paths = listOf(
            "$cleanUrl/api/v2.0/indexers/all/results?apikey=$key&Query=test",
            "$cleanUrl/jackett/api/v2.0/indexers/all/results?apikey=$key&Query=test"
        )
        for (url in paths) {
            val res = runCatching { client.get(url) }.getOrNull() ?: continue
            if (res.status == HttpStatusCode.OK) return true
        }
        return false
    }

    suspend fun testTorrServer(baseUrl: String): Boolean {
        val cleanUrl = baseUrl.trim().trimEnd('/')
        val res = runCatching { client.get("$cleanUrl/echo") }.getOrNull() ?: return false
        return res.status == HttpStatusCode.OK
    }

    companion object {
        private val VIDEO_EXTENSIONS = setOf("mkv", "mp4", "avi", "ts", "mov", "webm", "m4v")
        private val SUBTITLE_EXTENSIONS = setOf("ass", "srt", "vtt")

        private val EPISODE_PATTERNS = listOf(
            Regex("""(?:эпизод|серия|ep|e|s\d+e)\s*(\d{1,4})""", RegexOption.IGNORE_CASE),
            Regex("""\[(\d{1,4})\]"""),
            Regex("""-\s*(\d{1,4})(?:\s*\[|\s*\(|\s*\.|$)""", RegexOption.IGNORE_CASE),
            Regex("""(?:^|[\s_.\-\[(])(\d{1,4})(?:[\s_.\-\])]|$)""")
        )

        private val QUALITY_REGEX = Regex("""\b(2160p|4k|1440p|2k|1080p|720p|480p|bdrip|web-?dl|web-?rip|hdtv)\b""", RegexOption.IGNORE_CASE)

        @Volatile
        private var INSTANCE: TorrServerParser? = null
        private val mutex = Mutex()

        suspend fun getInstance(): TorrServerParser {
            INSTANCE?.let { return it }
            return mutex.withLock {
                INSTANCE?.let { return@withLock it }
                val parser = TorrServerParser(Network.watchClient)
                INSTANCE = parser
                parser
            }
        }

        fun normalizeLink(link: String): String {
            // Fix Jackett bug where port is missing after colon: "tech:/jackett" -> "tech/jackett"
            return link.replace(Regex("""(:/)([^/])"""), "/$2")
        }

        private fun isVideoTorrent(item: JackettTorrentItem): Boolean {
            val cat = (item.categoryDesc ?: BLANK).lowercase()
            val title = item.title.lowercase()

            val bannedCats = listOf("books", "comics", "audio", "music", "games", "game")
            if (bannedCats.any { it in cat }) return false

            val bannedWords = listOf("[manga]", "[epub]", "[cbr]", "[pdf]", "[ost]", "[album]", "[flac]", "[mp3]", "[switch]", "[ps4]", "[ps5]", "[pc]", "[android]", "[xbox]")
            if (bannedWords.any { it in title }) return false

            return true
        }

        private fun cleanTorrentTitle(title: String): String {
            return title
                .replace(Regex("""\s+"""), " ")
                .trim()
        }

        private fun detectQuality(title: String): String {
            return QUALITY_REGEX.find(title)?.value?.uppercase() ?: BLANK
        }

        private fun isDubbed(title: String): Boolean {
            val t = title.lowercase()
            return t.any { it in 'а'..'я' } ||
                    listOf("dub", "дубляж", "озвучк", "anilibria", "dream cast", "anidub", "shiza", "studio band", "кансай", "kansai").any { it in t }
        }

        private fun hasSubtitles(title: String): Boolean {
            val t = title.lowercase()
            return listOf("sub", "субтитр", "subs", "надписи").any { it in t }
        }

        private fun extractLastEpisodeFromTitle(title: String): Int? {
            // Matches [01-28] or [28] or (1-28)
            val rangeMatch = Regex("""[\[\(](\d{1,4})-(\d{1,4})[\]\)]""").find(title)
            if (rangeMatch != null) {
                return rangeMatch.groupValues[2].toIntOrNull()
            }
            val singleMatch = Regex("""[\[\(](\d{1,4})[\]\)]""").find(title)
            if (singleMatch != null) {
                return singleMatch.groupValues[1].toIntOrNull()
            }
            return null
        }

        private fun extractEpisodeNumber(name: String): Int? {
            for (pattern in EPISODE_PATTERNS) {
                val match = pattern.find(name)
                if (match != null) {
                    val num = match.groupValues[1].toIntOrNull()
                    if (num != null && num in 1..9999) {
                        return num
                    }
                }
            }
            return null
        }

        private fun isNonEpisodeFile(path: String): Boolean {
            val lower = path.lowercase()
            return lower.contains("/sample") || lower.contains("/extras") ||
                    lower.contains("trailer") || lower.contains("sample.") ||
                    lower.contains("ncop") || lower.contains("nced")
        }

        private fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.size - 1)
            val value = bytes / 1024.0.pow(digitGroups.toDouble())
            val formatted = ((value * 10).toLong() / 10.0)
            return "$formatted ${units[digitGroups]}"
        }
    }
}
